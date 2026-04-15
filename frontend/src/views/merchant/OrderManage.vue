<template>
  <div class="order-manage">
    <el-card>
      <template #header>
        <span>订单管理</span>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="待处理" name="pending" />
        <el-tab-pane label="备货中" name="accepted" />
        <el-tab-pane label="配送中" name="delivering" />
        <el-tab-pane label="历史订单" name="history" />
      </el-tabs>

      <el-table :data="orderList" v-loading="loading" border stripe>
        <el-table-column prop="orderNo" label="订单编号" min-width="180" />
        <el-table-column label="订单金额" width="120" align="center">
          <template #default="{ row }">
            <span style="color: #e6a23c; font-weight: bold">¥{{ row.totalPrice?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" align="center" />
        <el-table-column label="订单状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="{ row }">
            <!-- 待处理：接单 / 拒单 -->
            <template v-if="row.status === 0">
              <el-button size="small" type="success" link @click="handleAccept(row)">
                接单
              </el-button>
              <el-button size="small" type="danger" link @click="openRejectDialog(row)">
                拒单
              </el-button>
            </template>
            <!-- 备货中：备货完成 -->
            <template v-if="row.status === 1">
              <el-button size="small" type="primary" link @click="handleReady(row)">
                备货完成
              </el-button>
            </template>
            <!-- 通用：查看详情 -->
            <el-button size="small" type="info" link @click="openDetailDialog(row)">
              详情
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchOrders"
          @current-change="fetchOrders"
        />
      </div>
    </el-card>

    <!-- 拒单原因弹窗 -->
    <el-dialog v-model="rejectDialogVisible" title="拒绝订单" width="450px" destroy-on-close>
      <el-form ref="rejectFormRef" :model="rejectForm" :rules="rejectRules" label-width="80px">
        <el-form-item label="拒单原因" prop="reason">
          <el-input
            v-model="rejectForm.reason"
            type="textarea"
            :rows="3"
            placeholder="请输入拒单原因"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" :loading="rejecting" @click="handleReject">确认拒单</el-button>
      </template>
    </el-dialog>

    <!-- 订单详情弹窗 -->
    <el-dialog v-model="detailDialogVisible" title="订单详情" width="550px" destroy-on-close>
      <div v-loading="detailLoading">
        <el-descriptions :column="1" border v-if="currentOrder">
          <el-descriptions-item label="订单编号">{{ currentOrder.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="订单金额">
            <span style="color: #e6a23c; font-weight: bold">¥{{ currentOrder.totalPrice }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ currentOrder.createTime }}</el-descriptions-item>
          <el-descriptions-item label="订单状态">
            <el-tag :type="statusTagType(currentOrder.status)">
              {{ statusLabel(currentOrder.status) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="收货地址">
            <template v-if="currentOrderAddress">
              {{ currentOrderAddress.contactName }} {{ currentOrderAddress.contactPhone }}<br/>
              {{ currentOrderAddress.address }}
            </template>
            <template v-else>-</template>
          </el-descriptions-item>
          <el-descriptions-item label="备注">
            {{ currentOrder.remark || '无' }}
          </el-descriptions-item>
        </el-descriptions>

        <template v-if="currentOrderItems.length">
          <h4 style="margin: 16px 0 8px">商品明细</h4>
          <el-table :data="currentOrderItems" border size="small">
            <el-table-column prop="productName" label="商品" />
            <el-table-column prop="quantity" label="数量" width="80" align="center" />
            <el-table-column label="小计" width="100" align="center">
              <template #default="{ row }">¥{{ (row.productPrice * row.quantity).toFixed(2) }}</template>
            </el-table-column>
          </el-table>
        </template>
      </div>

      <template #footer>
        <el-button @click="detailDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMerchantOrders, acceptOrder, rejectOrder, markOrderReady, getOrder } from '@/api/order'

const loading = ref(false)
const rejecting = ref(false)
const orderList = ref([])
const activeTab = ref('pending')

const rejectDialogVisible = ref(false)
const detailDialogVisible = ref(false)
const detailLoading = ref(false)
const currentOrder = ref(null)
const currentOrderItems = ref([])
const currentOrderAddress = ref(null)
const rejectFormRef = ref(null)

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const rejectForm = reactive({ reason: '' })
const rejectRules = {
  reason: [{ required: true, message: '请输入拒单原因', trigger: 'blur' }]
}

const tabStatusMap = {
  pending: [0],
  accepted: [1],
  delivering: [2],
  history: [3, 4, 5]
}

const statusLabelMap = {
  0: '待处理',
  1: '备货中',
  2: '配送中',
  3: '已完成',
  4: '已取消',
  5: '已拒绝'
}

const statusTagTypeMap = {
  0: 'warning',
  1: '',
  2: 'primary',
  3: 'success',
  4: 'info',
  5: 'danger'
}

const statusLabel = (status) => statusLabelMap[status] || '未知'
const statusTagType = (status) => statusTagTypeMap[status] || 'info'

const fetchOrders = async () => {
  loading.value = true
  try {
    const statusList = tabStatusMap[activeTab.value]
    const res = await getMerchantOrders({
      status: statusList,
      page: pagination.page,
      size: pagination.pageSize
    })
    orderList.value = res.data.list || []
    pagination.total = res.data.total || 0
  } catch {
    ElMessage.error('获取订单列表失败')
  } finally {
    loading.value = false
  }
}

const handleTabChange = () => {
  pagination.page = 1
  fetchOrders()
}

const handleAccept = async (row) => {
  try {
    await acceptOrder(row.id)
    ElMessage.success('已接单')
    fetchOrders()
  } catch {
    ElMessage.error('操作失败')
  }
}

const openRejectDialog = (row) => {
  currentOrder.value = row
  rejectForm.reason = ''
  rejectDialogVisible.value = true
}

const handleReject = async () => {
  const valid = await rejectFormRef.value.validate().catch(() => false)
  if (!valid) return

  rejecting.value = true
  try {
    await rejectOrder(currentOrder.value.id, { reason: rejectForm.reason })
    ElMessage.success('已拒单')
    rejectDialogVisible.value = false
    fetchOrders()
  } catch {
    ElMessage.error('操作失败')
  } finally {
    rejecting.value = false
  }
}

const handleReady = async (row) => {
  try {
    await markOrderReady(row.id)
    ElMessage.success('已标记备货完成')
    fetchOrders()
  } catch {
    ElMessage.error('操作失败')
  }
}

const openDetailDialog = async (row) => {
  currentOrder.value = row
  currentOrderItems.value = []
  currentOrderAddress.value = null
  detailDialogVisible.value = true
  detailLoading.value = true
  try {
    const res = await getOrder(row.id)
    currentOrderItems.value = res.data.items || []
    currentOrderAddress.value = res.data.address || null
  } catch {
    ElMessage.error('获取订单详情失败')
  } finally {
    detailLoading.value = false
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-manage {
  padding: 20px;
}
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
