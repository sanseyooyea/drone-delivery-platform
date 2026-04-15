<template>
  <div class="order-manage">
    <h2>订单管理</h2>

    <!-- 筛选栏 -->
    <el-form :model="filterForm" inline class="filter-bar">
      <el-form-item label="订单状态">
        <el-select
          v-model="filterForm.status"
          placeholder="全部状态"
          clearable
          style="width: 160px"
          @change="handleFilter"
        >
          <el-option
            v-for="item in statusOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="商家ID">
        <el-input
          v-model="filterForm.merchantId"
          placeholder="请输入商家ID"
          clearable
          style="width: 180px"
          @clear="handleFilter"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleFilter">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="orderList" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="orderNo" label="订单号" min-width="180" />
      <el-table-column prop="userName" label="用户" min-width="120" />
      <el-table-column prop="merchantName" label="商家" min-width="140" />
      <el-table-column prop="totalPrice" label="总价" width="110">
        <template #default="{ row }">
          <span style="color: #f56c6c; font-weight: bold;">¥{{ row.totalPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="orderTagType(row.status)">
            {{ orderStatusMap[row.status] || '未知' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="170" />
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="fetchOrders"
        @current-change="fetchOrders"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAdminOrders } from '@/api/admin'

const orderStatusMap = {
  0: '待支付',
  1: '已支付',
  2: '备货中',
  3: '配送中',
  4: '已完成',
  5: '已取消',
}

const statusOptions = Object.entries(orderStatusMap).map(([value, label]) => ({
  value: Number(value),
  label,
}))

const orderTagType = (status) => {
  const map = { 0: 'info', 1: 'warning', 2: '', 3: 'primary', 4: 'success', 5: 'danger' }
  return map[status] || 'info'
}

const loading = ref(false)
const orderList = ref([])

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0,
})

const filterForm = reactive({
  status: undefined,
  merchantId: '',
})

const fetchOrders = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      size: pagination.pageSize,
    }
    if (filterForm.status !== undefined && filterForm.status !== null && filterForm.status !== '') {
      params.status = filterForm.status
    }
    if (filterForm.merchantId) {
      params.merchantId = filterForm.merchantId
    }
    const res = await getAdminOrders(params)
    orderList.value = res.data.list
    pagination.total = res.data.total
  } catch (e) {
    console.error('获取订单列表失败', e)
  } finally {
    loading.value = false
  }
}

const handleFilter = () => {
  pagination.page = 1
  fetchOrders()
}

const handleReset = () => {
  filterForm.status = undefined
  filterForm.merchantId = ''
  pagination.page = 1
  fetchOrders()
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.order-manage {
  padding: 20px;
}

.filter-bar {
  margin-bottom: 16px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
