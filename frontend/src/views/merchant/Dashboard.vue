<template>
  <div class="merchant-dashboard">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="12">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="今日订单数" :value="stats.todayOrders" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card shadow="hover" class="stat-card">
          <el-statistic title="累计销售额（元）" :value="stats.totalSales" :precision="2" />
        </el-card>
      </el-col>
    </el-row>

    <!-- 最近订单 -->
    <el-card style="margin-top: 20px">
      <template #header>
        <span>最近订单</span>
      </template>
      <el-table :data="recentOrders" v-loading="loading" border stripe>
        <el-table-column prop="orderNo" label="订单编号" min-width="180" />
        <el-table-column label="订单金额" width="120" align="center">
          <template #default="{ row }">
            <span>¥{{ row.totalPrice?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180" align="center" />
        <el-table-column label="订单状态" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMerchantOrders } from '@/api/order'

const loading = ref(false)
const recentOrders = ref([])

const stats = reactive({
  todayOrders: 0,
  totalSales: 0
})

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

const fetchDashboardData = async () => {
  loading.value = true
  try {
    const res = await getMerchantOrders({ page: 1, size: 10 })
    const list = res.data.list || []
    recentOrders.value = list

    // 简单统计：今日订单 & 累计销售额
    const today = new Date().toISOString().slice(0, 10)
    stats.todayOrders = list.filter(
      (o) => o.createTime && o.createTime.startsWith(today)
    ).length
    stats.totalSales = list.reduce((sum, o) => sum + (o.totalPrice || 0), 0)
  } catch {
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchDashboardData()
})
</script>

<style scoped>
.merchant-dashboard {
  padding: 20px;
}
.stats-row {
  margin-bottom: 4px;
}
.stat-card {
  text-align: center;
}
</style>
