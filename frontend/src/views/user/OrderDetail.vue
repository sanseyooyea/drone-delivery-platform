<template>
  <div class="order-detail-page" v-loading="loading">
    <h2 class="page-title">订单详情</h2>

    <template v-if="order">
      <!-- 订单状态时间线 -->
      <el-card class="section-card">
        <template #header>
          <div class="card-header">
            <span>订单状态</span>
            <el-tag :type="statusTagType(order.status)" size="large">
              {{ statusText(order.status) }}
            </el-tag>
          </div>
        </template>
        <el-steps :active="activeStep" finish-status="success" align-center>
          <el-step title="待接单" />
          <el-step title="备货中" />
          <el-step title="配送中" />
          <el-step title="已送达" />
        </el-steps>
        <div v-if="order.status === 5" style="text-align:center;color:#f56c6c;margin-top:12px;">
          拒单原因：{{ order.rejectReason }}
        </div>
      </el-card>

      <!-- 配送信息 -->
      <el-card class="section-card" v-if="order.droneId">
        <template #header>
          <span>配送信息</span>
        </template>
        <el-descriptions :column="1" border>
          <el-descriptions-item label="无人机编号">{{ order.droneId }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 收货地址 -->
      <el-card class="section-card" v-if="address">
        <template #header>
          <span>收货地址</span>
        </template>
        <div class="address-info">
          <span class="name">{{ address.contactName }}</span>
          <span class="phone">{{ address.contactPhone }}</span>
        </div>
        <div class="address-detail">{{ address.address }}</div>
      </el-card>

      <!-- 订单商品 -->
      <el-card class="section-card">
        <template #header>
          <span>商品信息</span>
        </template>
        <el-table :data="items" style="width: 100%">
          <el-table-column label="商品" min-width="280">
            <template #default="{ row }">
              <div class="product-cell">
                <el-image :src="row.image" fit="cover" class="item-thumb">
                  <template #error>
                    <div class="thumb-placeholder"><el-icon><Picture /></el-icon></div>
                  </template>
                </el-image>
                <span>{{ row.productName }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="单价" width="120" align="center">
            <template #default="{ row }">¥{{ row.productPrice }}</template>
          </el-table-column>
          <el-table-column label="数量" prop="quantity" width="100" align="center" />
          <el-table-column label="小计" width="120" align="center">
            <template #default="{ row }">
              <span class="subtotal">¥{{ (row.productPrice * row.quantity).toFixed(2) }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 订单信息 -->
      <el-card class="section-card">
        <template #header>
          <span>订单信息</span>
        </template>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="订单编号">{{ order.orderNo }}</el-descriptions-item>
          <el-descriptions-item label="下单时间">{{ order.createTime }}</el-descriptions-item>
          <el-descriptions-item label="备注">{{ order.remark || '无' }}</el-descriptions-item>
          <el-descriptions-item label="实付金额">
            <span class="pay-amount">¥{{ order.totalPrice }}</span>
          </el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 操作按钮 -->
      <div class="action-bar">
        <el-button @click="$router.back()">返回</el-button>
        <el-button
          v-if="order.status === 0"
          type="danger"
          plain
          @click="handleCancel"
        >
          取消订单
        </el-button>
        <el-button
          v-if="order.status === 2"
          type="success"
          @click="handleConfirmReceive"
        >
          确认收货
        </el-button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { getOrder, cancelOrder, confirmReceive } from '@/api/order'
import { getAddresses } from '@/api/address'

const route = useRoute()
const router = useRouter()

const order = ref(null)
const items = ref([])
const address = ref(null)
const loading = ref(false)

const statusMap = {
  0: '待接单',
  1: '备货中',
  2: '配送中',
  3: '已送达',
  4: '已取消',
  5: '已拒单',
}
const statusTagMap = {
  0: 'warning',
  1: 'info',
  2: '',
  3: 'success',
  4: 'danger',
  5: 'danger',
}
const stepMap = { 0: 0, 1: 1, 2: 2, 3: 3 }

const statusText = (s) => statusMap[s] || '未知'
const statusTagType = (s) => statusTagMap[s] || 'info'

const activeStep = computed(() => {
  if (!order.value) return 0
  return stepMap[order.value.status] ?? 0
})

const fetchOrder = async () => {
  loading.value = true
  try {
    const res = await getOrder(route.params.id)
    // 后端返回 { order: {...}, items: [...] }
    order.value = res.data.order
    items.value = res.data.items || []

    // 加载收货地址
    if (order.value.addressId) {
      try {
        const addrRes = await getAddresses()
        const list = addrRes.data || []
        address.value = list.find(a => a.id === order.value.addressId) || null
      } catch {}
    }
  } catch (e) {
    ElMessage.error('获取订单详情失败')
  } finally {
    loading.value = false
  }
}

const handleCancel = async () => {
  try {
    await ElMessageBox.confirm('确定取消该订单吗？', '提示', { type: 'warning' })
    await cancelOrder(order.value.id)
    ElMessage.success('已取消订单')
    fetchOrder()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('取消失败')
  }
}

const handleConfirmReceive = async () => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', { type: 'success' })
    await confirmReceive(order.value.id)
    ElMessage.success('已确认收货')
    fetchOrder()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchOrder()
})
</script>

<style scoped>
.order-detail-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 16px;
}
.page-title {
  font-size: 20px;
  margin-bottom: 20px;
}
.section-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.address-info {
  display: flex;
  gap: 12px;
  margin-bottom: 4px;
}
.address-info .name {
  font-weight: bold;
}
.address-info .phone {
  color: #666;
}
.address-detail {
  color: #666;
  font-size: 14px;
}
.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}
.item-thumb {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  flex-shrink: 0;
}
.thumb-placeholder {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
}
.subtotal {
  color: #f56c6c;
  font-weight: bold;
}
.pay-amount {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}
.action-bar {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 0;
}
</style>
