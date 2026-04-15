<template>
  <div class="order-list-page">
    <h2 class="page-title">我的订单</h2>

    <!-- 状态筛选 -->
    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <el-tab-pane label="全部订单" name="all" />
      <el-tab-pane label="待接单" name="pending" />
      <el-tab-pane label="配送中" name="delivering" />
      <el-tab-pane label="已完成" name="completed" />
      <el-tab-pane label="已取消" name="cancelled" />
    </el-tabs>

    <!-- 订单列表 -->
    <div v-loading="loading" class="order-list">
      <el-card
        v-for="order in orders"
        :key="order.id"
        class="order-card"
        shadow="hover"
        @click="goDetail(order.id)"
      >
        <div class="order-header">
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <span class="order-time">{{ order.createTime }}</span>
          <el-tag :type="statusTagType(order.status)" size="small">
            {{ statusText(order.status) }}
          </el-tag>
        </div>

        <div class="order-items">
          <div
            v-for="item in order.items?.slice(0, 3)"
            :key="item.id"
            class="order-item"
          >
            <el-image :src="item.image" fit="cover" class="item-thumb">
              <template #error>
                <div class="thumb-placeholder"><el-icon><Picture /></el-icon></div>
              </template>
            </el-image>
            <div class="item-info">
              <div class="item-name">{{ item.productName }}</div>
              <div class="item-meta">
                <span>¥{{ item.price }}</span>
                <span>x{{ item.quantity }}</span>
              </div>
            </div>
          </div>
          <div v-if="(order.items?.length || 0) > 3" class="more-items">
            等共 {{ order.items.length }} 件商品
          </div>
        </div>

        <div class="order-footer">
          <span class="total">合计：<b>¥{{ order.totalPrice }}</b></span>
          <div class="actions">
            <el-button
              v-if="order.status === 0"
              type="danger"
              size="small"
              plain
              @click.stop="handleCancel(order)"
            >
              取消订单
            </el-button>
            <el-button
              v-if="order.status === 2"
              type="success"
              size="small"
              @click.stop="handleConfirm(order)"
            >
              确认收货
            </el-button>
            <el-button size="small" @click.stop="goDetail(order.id)">查看详情</el-button>
          </div>
        </div>
      </el-card>

      <el-empty v-if="!loading && orders.length === 0" description="暂无订单" />
    </div>

    <!-- 分页 -->
    <div class="pagination-wrap" v-if="total > 0">
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        layout="prev, pager, next"
        @current-change="fetchOrders"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { getOrders, cancelOrder, confirmReceive } from '@/api/order'

const router = useRouter()

const activeTab = ref('all')
const orders = ref([])
const loading = ref(false)
const page = ref(1)
const pageSize = ref(10)
const total = ref(0)

const statusMap = {
  0: '待接单',
  1: '备货中',
  2: '配送中',
  3: '已完成',
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

const tabStatusMap = {
  all: undefined,
  pending: 0,
  delivering: 2,
  completed: 3,
  cancelled: 4,
}

const statusText = (status) => statusMap[status] || status
const statusTagType = (status) => statusTagMap[status] || 'info'

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: pageSize.value,
    }
    const statusVal = tabStatusMap[activeTab.value]
    if (statusVal !== undefined) {
      params.status = statusVal
    }
    const res = await getOrders(params)
    orders.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) {
    ElMessage.error('获取订单失败')
  } finally {
    loading.value = false
  }
}

const onTabChange = () => {
  page.value = 1
  fetchOrders()
}

const handleCancel = async (order) => {
  try {
    await ElMessageBox.confirm('确定取消该订单吗？', '提示', { type: 'warning' })
    await cancelOrder(order.id)
    ElMessage.success('已取消')
    fetchOrders()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('取消失败')
  }
}

const handleConfirm = async (order) => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '提示', { type: 'success' })
    await confirmReceive(order.id)
    ElMessage.success('已确认收货')
    fetchOrders()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

const goDetail = (id) => {
  router.push(`/orders/${id}`)
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-list-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 16px;
}
.page-title {
  font-size: 20px;
  margin-bottom: 16px;
}
.order-list {
  min-height: 300px;
}
.order-card {
  margin-bottom: 16px;
  cursor: pointer;
}
.order-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
}
.order-no {
  font-size: 14px;
  color: #666;
}
.order-time {
  color: #999;
  font-size: 13px;
  flex: 1;
}
.order-items {
  display: flex;
  gap: 16px;
  flex-wrap: wrap;
  padding: 12px 0;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}
.order-item {
  display: flex;
  gap: 8px;
  align-items: center;
}
.item-thumb {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  flex-shrink: 0;
}
.thumb-placeholder {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
}
.item-name {
  font-size: 13px;
  max-width: 150px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.item-meta {
  color: #999;
  font-size: 12px;
  display: flex;
  gap: 8px;
}
.more-items {
  display: flex;
  align-items: center;
  color: #999;
  font-size: 13px;
}
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 12px;
}
.total b {
  color: #f56c6c;
  font-size: 16px;
}
.actions {
  display: flex;
  gap: 8px;
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
