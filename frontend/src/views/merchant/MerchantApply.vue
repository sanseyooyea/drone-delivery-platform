<template>
  <div class="merchant-apply">
    <!-- 已提交申请：显示状态 -->
    <el-card v-if="applyStatus !== null" class="status-card">
      <template #header>
        <span>申请状态</span>
      </template>
      <el-result
        v-if="applyStatus === 'pending'"
        icon="info"
        title="审核中"
        sub-title="您的商户入驻申请正在审核，请耐心等待。"
      />
      <el-result
        v-if="applyStatus === 'approved'"
        icon="success"
        title="审核通过"
        sub-title="恭喜！您的商户入驻申请已通过。"
      />
      <el-result
        v-if="applyStatus === 'rejected'"
        icon="error"
        title="审核未通过"
        :sub-title="rejectReason || '您的商户入驻申请未通过，请修改后重新提交。'"
      >
        <template #extra>
          <el-button type="primary" @click="resetForm">重新申请</el-button>
        </template>
      </el-result>
    </el-card>

    <!-- 申请表单 -->
    <el-card v-if="applyStatus === null || showForm" class="apply-card">
      <template #header>
        <span>商户入驻申请</span>
      </template>
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        style="max-width: 600px"
      >
        <el-form-item label="店铺名称" prop="shopName">
          <el-input v-model="form.shopName" placeholder="请输入店铺名称" />
        </el-form-item>

        <el-form-item label="联系方式" prop="contact">
          <el-input v-model="form.contact" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="经营类目" prop="category">
          <el-select v-model="form.category" placeholder="请选择经营类目" style="width: 100%">
            <el-option label="餐饮美食" value="food" />
            <el-option label="生鲜果蔬" value="fresh" />
            <el-option label="医药健康" value="medicine" />
            <el-option label="日用百货" value="daily" />
            <el-option label="数码电子" value="digital" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="店铺描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="4"
            placeholder="请简要描述您的店铺"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit">
            提交申请
          </el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { applyMerchant, getApplyStatus } from '@/api/merchant'

const formRef = ref(null)
const applyStatus = ref(null)
const rejectReason = ref('')
const submitting = ref(false)
const showForm = ref(false)

const form = reactive({
  shopName: '',
  contact: '',
  category: '',
  description: ''
})

const rules = {
  shopName: [{ required: true, message: '请输入店铺名称', trigger: 'blur' }],
  contact: [{ required: true, message: '请输入联系方式', trigger: 'blur' }],
  category: [{ required: true, message: '请选择经营类目', trigger: 'change' }]
}

const fetchStatus = async () => {
  try {
    const res = await getApplyStatus()
    if (res.data && res.data.status) {
      applyStatus.value = res.data.status
      rejectReason.value = res.data.rejectReason || ''
    }
  } catch {
    // 未申请过，显示表单
    applyStatus.value = null
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  submitting.value = true
  try {
    await applyMerchant({ ...form })
    ElMessage.success('申请提交成功，请等待审核')
    applyStatus.value = 'pending'
    showForm.value = false
  } catch {
    ElMessage.error('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

const handleReset = () => {
  formRef.value?.resetFields()
}

const resetForm = () => {
  applyStatus.value = null
  showForm.value = true
  handleReset()
}

onMounted(() => {
  fetchStatus()
})
</script>

<style scoped>
.merchant-apply {
  padding: 20px;
}
.status-card {
  margin-bottom: 20px;
  max-width: 700px;
}
.apply-card {
  max-width: 700px;
}
</style>
