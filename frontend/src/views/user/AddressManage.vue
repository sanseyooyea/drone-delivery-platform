<template>
  <div class="address-manage-page">
    <div class="page-header">
      <h2 class="page-title">收货地址管理</h2>
      <el-button type="primary" @click="openDialog(null)">
        <el-icon><Plus /></el-icon>
        新增地址
      </el-button>
    </div>

    <div v-loading="loading" class="address-list">
      <el-card
        v-for="addr in addresses"
        :key="addr.id"
        class="address-card"
        shadow="hover"
      >
        <div class="address-content">
          <div class="address-main">
            <div class="info-row">
              <span class="name">{{ addr.name }}</span>
              <span class="phone">{{ addr.phone }}</span>
              <el-tag v-if="addr.isDefault" type="warning" size="small">默认</el-tag>
            </div>
            <div class="detail">
              {{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}
            </div>
          </div>
          <div class="address-actions">
            <el-button
              v-if="!addr.isDefault"
              type="warning"
              link
              @click="handleSetDefault(addr)"
            >
              设为默认
            </el-button>
            <el-button type="primary" link @click="openDialog(addr)">编辑</el-button>
            <el-popconfirm
              title="确定删除该地址吗？"
              @confirm="handleDelete(addr.id)"
            >
              <template #reference>
                <el-button type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
          </div>
        </div>
      </el-card>

      <el-empty v-if="!loading && addresses.length === 0" description="暂无收货地址" />
    </div>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑地址' : '新增地址'"
      width="500px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="收货人" prop="name">
          <el-input v-model="form.name" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="省份" prop="province">
          <el-input v-model="form.province" placeholder="省" />
        </el-form-item>
        <el-form-item label="城市" prop="city">
          <el-input v-model="form.city" placeholder="市" />
        </el-form-item>
        <el-form-item label="区县" prop="district">
          <el-input v-model="form.district" placeholder="区/县" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detail">
          <el-input
            v-model="form.detail"
            type="textarea"
            :rows="2"
            placeholder="请输入详细地址（街道、门牌号等）"
          />
        </el-form-item>
        <el-form-item label="默认地址">
          <el-switch v-model="form.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'
import {
  getAddresses,
  createAddress,
  updateAddress,
  deleteAddress,
  setDefaultAddress,
} from '@/api/address'

const addresses = ref([])
const loading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const saving = ref(false)
const editingId = ref(null)
const formRef = ref(null)

const initialForm = () => ({
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false,
})

const form = reactive(initialForm())

const rules = {
  name: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1\d{10}$/, message: '手机号格式不正确', trigger: 'blur' },
  ],
  province: [{ required: true, message: '请输入省份', trigger: 'blur' }],
  city: [{ required: true, message: '请输入城市', trigger: 'blur' }],
  district: [{ required: true, message: '请输入区县', trigger: 'blur' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }],
}

const fetchAddresses = async () => {
  loading.value = true
  try {
    const res = await getAddresses()
    addresses.value = res.data || []
  } catch (e) {
    ElMessage.error('获取地址列表失败')
  } finally {
    loading.value = false
  }
}

const openDialog = (addr) => {
  if (addr) {
    isEdit.value = true
    editingId.value = addr.id
    Object.assign(form, {
      name: addr.name,
      phone: addr.phone,
      province: addr.province,
      city: addr.city,
      district: addr.district,
      detail: addr.detail,
      isDefault: addr.isDefault,
    })
  } else {
    isEdit.value = false
    editingId.value = null
    Object.assign(form, initialForm())
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  saving.value = true
  try {
    if (isEdit.value) {
      await updateAddress(editingId.value, { ...form })
      ElMessage.success('地址已更新')
    } else {
      await createAddress({ ...form })
      ElMessage.success('地址已添加')
    }
    dialogVisible.value = false
    fetchAddresses()
  } catch (e) {
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

const handleSetDefault = async (addr) => {
  try {
    await setDefaultAddress(addr.id)
    ElMessage.success('已设为默认地址')
    fetchAddresses()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (id) => {
  try {
    await deleteAddress(id)
    ElMessage.success('已删除')
    fetchAddresses()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

onMounted(() => {
  fetchAddresses()
})
</script>

<style scoped>
.address-manage-page {
  max-width: 800px;
  margin: 0 auto;
  padding: 16px;
}
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}
.page-title {
  font-size: 20px;
  margin: 0;
}
.address-card {
  margin-bottom: 12px;
}
.address-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.info-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 6px;
}
.info-row .name {
  font-weight: bold;
  font-size: 15px;
}
.info-row .phone {
  color: #666;
}
.detail {
  color: #666;
  font-size: 14px;
}
.address-actions {
  display: flex;
  gap: 4px;
  flex-shrink: 0;
}
</style>
