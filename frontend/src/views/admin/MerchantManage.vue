<template>
  <div class="merchant-manage">
    <h2>商家管理</h2>

    <el-table :data="merchantList" v-loading="loading" border stripe style="width: 100%">
      <el-table-column prop="shopName" label="店铺名称" min-width="150" />
      <el-table-column prop="contact" label="联系方式" min-width="130" />
      <el-table-column prop="category" label="经营类目" min-width="120" />
      <el-table-column prop="auditStatus" label="审核状态" width="120">
        <template #default="{ row }">
          <el-tag :type="auditTagType(row.auditStatus)">
            {{ auditStatusMap[row.auditStatus] || '未知' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="170" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button
            type="primary"
            size="small"
            link
            :disabled="row.auditStatus !== 0"
            @click="openAuditDialog(row)"
          >
            审核
          </el-button>
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
        @size-change="fetchMerchants"
        @current-change="fetchMerchants"
      />
    </div>

    <!-- 审核弹窗 -->
    <el-dialog v-model="auditDialogVisible" title="商家审核" width="460px" destroy-on-close>
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="店铺名称">
          <span>{{ currentMerchant?.shopName }}</span>
        </el-form-item>
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.result">
            <el-radio :value="1">通过</el-radio>
            <el-radio :value="2">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="auditForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入审核备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="auditLoading" @click="handleAudit">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMerchants, auditMerchant } from '@/api/admin'

const auditStatusMap = {
  0: '待审核',
  1: '已通过',
  2: '已驳回',
}

const auditTagType = (status) => {
  const map = { 0: 'warning', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}

const loading = ref(false)
const merchantList = ref([])

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0,
})

const auditDialogVisible = ref(false)
const auditLoading = ref(false)
const currentMerchant = ref(null)
const auditForm = reactive({
  result: 1,
  remark: '',
})

const fetchMerchants = async () => {
  loading.value = true
  try {
    const res = await getMerchants({
      page: pagination.page,
      size: pagination.pageSize,
    })
    merchantList.value = res.data.list
    pagination.total = res.data.total
  } catch (e) {
    console.error('获取商家列表失败', e)
  } finally {
    loading.value = false
  }
}

const openAuditDialog = (row) => {
  currentMerchant.value = row
  auditForm.result = 1
  auditForm.remark = ''
  auditDialogVisible.value = true
}

const handleAudit = async () => {
  auditLoading.value = true
  try {
    await auditMerchant(currentMerchant.value.id, {
      auditStatus: auditForm.result,
      remark: auditForm.remark,
    })
    ElMessage.success('审核提交成功')
    auditDialogVisible.value = false
    fetchMerchants()
  } catch (e) {
    console.error('审核提交失败', e)
  } finally {
    auditLoading.value = false
  }
}

onMounted(() => {
  fetchMerchants()
})
</script>

<style scoped>
.merchant-manage {
  padding: 20px;
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
