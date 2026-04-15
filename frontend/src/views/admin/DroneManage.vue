<template>
  <div class="drone-manage">
    <div class="header-bar">
      <h2>无人机管理</h2>
      <el-button type="primary" @click="openAddDialog">新增无人机</el-button>
    </div>

    <el-table :data="droneList" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="code" label="无人机编号" min-width="140" />
      <el-table-column prop="model" label="型号" min-width="120" />
      <el-table-column prop="status" label="状态" width="120">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">
            {{ statusMap[row.status] || '未知' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" link @click="openEditDialog(row)">编辑</el-button>
          <el-button type="warning" size="small" link @click="openLogsDrawer(row)">配送日志</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="fetchDrones"
        @current-change="fetchDrones"
      />
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="formDialogVisible"
      :title="isEdit ? '编辑无人机' : '新增无人机'"
      width="460px"
      destroy-on-close
    >
      <el-form :model="droneForm" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="无人机编号" prop="code">
          <el-input v-model="droneForm.code" placeholder="请输入无人机编号" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="型号" prop="model">
          <el-input v-model="droneForm.model" placeholder="请输入型号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="formLoading" @click="handleSubmitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 配送日志抽屉 -->
    <el-drawer
      v-model="logsDrawerVisible"
      :title="`配送日志 - ${currentDrone?.code || ''}`"
      size="55%"
      destroy-on-close
    >
      <el-table :data="logList" v-loading="logsLoading" border stripe>
        <el-table-column prop="orderNo" label="订单号" min-width="180" />
        <el-table-column prop="deliveryAddress" label="送达地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="startTime" label="开始时间" min-width="170" />
        <el-table-column prop="endTime" label="结束时间" min-width="170">
          <template #default="{ row }">
            {{ row.endTime || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'primary'">
              {{ row.status === 1 ? '已完成' : '配送中' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="logPagination.page"
          v-model:page-size="logPagination.pageSize"
          :total="logPagination.total"
          :page-sizes="[10, 20]"
          layout="total, sizes, prev, pager, next"
          background
          small
          @size-change="fetchLogs"
          @current-change="fetchLogs"
        />
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getDrones, createDrone, updateDrone, getDroneLogs } from '@/api/admin'

const statusMap = {
  0: '空闲',
  1: '配送中',
  2: '维护中',
}

const statusTagType = (status) => {
  const map = { 0: 'success', 1: 'primary', 2: 'warning' }
  return map[status] || 'info'
}

const loading = ref(false)
const droneList = ref([])

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0,
})

// 表单相关
const formDialogVisible = ref(false)
const formLoading = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const currentDroneId = ref(null)

const droneForm = reactive({
  code: '',
  model: '',
})

const formRules = {
  code: [{ required: true, message: '请输入无人机编号', trigger: 'blur' }],
  model: [{ required: true, message: '请输入型号', trigger: 'blur' }],
}

// 日志相关
const logsDrawerVisible = ref(false)
const logsLoading = ref(false)
const currentDrone = ref(null)
const logList = ref([])

const logPagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0,
})

const fetchDrones = async () => {
  loading.value = true
  try {
    const res = await getDrones({
      page: pagination.page,
      size: pagination.pageSize,
    })
    droneList.value = res.data.list
    pagination.total = res.data.total
  } catch (e) {
    console.error('获取无人机列表失败', e)
  } finally {
    loading.value = false
  }
}

const openAddDialog = () => {
  isEdit.value = false
  currentDroneId.value = null
  droneForm.code = ''
  droneForm.model = ''
  formDialogVisible.value = true
}

const openEditDialog = (row) => {
  isEdit.value = true
  currentDroneId.value = row.id
  droneForm.code = row.code
  droneForm.model = row.model
  formDialogVisible.value = true
}

const handleSubmitForm = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  formLoading.value = true
  try {
    if (isEdit.value) {
      await updateDrone(currentDroneId.value, { model: droneForm.model })
    } else {
      await createDrone({ code: droneForm.code, model: droneForm.model })
    }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    formDialogVisible.value = false
    fetchDrones()
  } catch (e) {
    console.error('提交无人机信息失败', e)
  } finally {
    formLoading.value = false
  }
}

const openLogsDrawer = (row) => {
  currentDrone.value = row
  logPagination.page = 1
  logList.value = []
  logsDrawerVisible.value = true
  fetchLogs()
}

const fetchLogs = async () => {
  if (!currentDrone.value) return
  logsLoading.value = true
  try {
    const res = await getDroneLogs(currentDrone.value.id, {
      page: logPagination.page,
      size: logPagination.pageSize,
    })
    logList.value = res.data.list
    logPagination.total = res.data.total
  } catch (e) {
    console.error('获取配送日志失败', e)
  } finally {
    logsLoading.value = false
  }
}

onMounted(() => {
  fetchDrones()
})
</script>

<style scoped>
.drone-manage {
  padding: 20px;
}

.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.header-bar h2 {
  margin: 0;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
