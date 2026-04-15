<template>
  <div class="admin-dashboard">
    <h2>管理后台总览</h2>
    <el-row :gutter="20" v-loading="loading">
      <el-col :span="4" :offset="0" v-for="item in statCards" :key="item.key">
        <el-card shadow="hover" class="stat-card">
          <template #header>
            <div class="stat-header">
              <el-icon :size="22" :color="item.color"><component :is="item.icon" /></el-icon>
              <span>{{ item.label }}</span>
            </div>
          </template>
          <div class="stat-value" :style="{ color: item.color }">
            {{ item.prefix }}{{ stats[item.key] ?? '--' }}
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { User, Shop, List, Tickets, Money } from '@element-plus/icons-vue'
import { getStats } from '@/api/admin'

const loading = ref(false)

const stats = reactive({
  totalUsers: 0,
  totalMerchants: 0,
  totalOrders: 0,
  todayOrders: 0,
  todaySales: 0,
})

const statCards = [
  { key: 'totalUsers', label: '总用户数', icon: User, color: '#409EFF', prefix: '' },
  { key: 'totalMerchants', label: '总商家数', icon: Shop, color: '#67C23A', prefix: '' },
  { key: 'totalOrders', label: '总订单数', icon: List, color: '#E6A23C', prefix: '' },
  { key: 'todayOrders', label: '今日订单', icon: Tickets, color: '#F56C6C', prefix: '' },
  { key: 'todaySales', label: '今日销售额', icon: Money, color: '#909399', prefix: '¥' },
]

const fetchStats = async () => {
  loading.value = true
  try {
    const res = await getStats()
    Object.assign(stats, res.data)
  } catch (e) {
    console.error('获取统计数据失败', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchStats()
})
</script>

<style scoped>
.admin-dashboard {
  padding: 20px;
}

.stat-card {
  text-align: center;
  margin-bottom: 20px;
}

.stat-header {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  padding: 10px 0;
}
</style>
