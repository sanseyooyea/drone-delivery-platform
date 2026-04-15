<template>
  <div class="product-manage">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>商品管理</span>
          <el-button type="primary" @click="openDialog(null)">添加商品</el-button>
        </div>
      </template>

      <el-table :data="productList" v-loading="loading" border stripe>
        <el-table-column prop="name" label="商品名称" min-width="150" />
        <el-table-column prop="price" label="价格（元）" width="120" align="center">
          <template #default="{ row }">
            <span>¥{{ row.price?.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="categoryName" label="分类" width="120" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openDialog(row)">编辑</el-button>
            <el-button
              size="small"
              :type="row.status === 1 ? 'warning' : 'success'"
              link
              @click="handleToggle(row)"
            >
              {{ row.status === 1 ? '下架' : '上架' }}
            </el-button>
            <el-popconfirm title="确定删除该商品吗？" @confirm="handleDelete(row.id)">
              <template #reference>
                <el-button size="small" type="danger" link>删除</el-button>
              </template>
            </el-popconfirm>
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
          @size-change="fetchProducts"
          @current-change="fetchProducts"
        />
      </div>
    </el-card>

    <!-- 添加/编辑商品弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑商品' : '添加商品'"
      width="550px"
      destroy-on-close
    >
      <el-form ref="productFormRef" :model="productForm" :rules="productRules" label-width="100px">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="productForm.name" placeholder="请输入商品名称" />
        </el-form-item>

        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="productForm.price"
            :min="0"
            :precision="2"
            :step="1"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="商品分类" prop="categoryId">
          <el-select v-model="productForm.categoryId" placeholder="请选择分类" style="width: 100%">
            <el-option
              v-for="cat in categoryList"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="图片地址" prop="imageUrl">
          <el-input v-model="productForm.imageUrl" placeholder="请输入图片URL" />
        </el-form-item>

        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入商品描述"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import {
  getMerchantProducts,
  createProduct,
  updateProduct,
  deleteProduct,
  toggleProductStatus
} from '@/api/product'
import { getCategories } from '@/api/product'

const loading = ref(false)
const saving = ref(false)
const productList = ref([])
const categoryList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const productFormRef = ref(null)

const pagination = reactive({
  page: 1,
  pageSize: 10,
  total: 0
})

const productForm = reactive({
  name: '',
  price: 0,
  categoryId: null,
  imageUrl: '',
  description: ''
})

const productRules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const res = await getMerchantProducts({
      page: pagination.page,
      size: pagination.pageSize
    })
    productList.value = res.data.list || []
    pagination.total = res.data.total || 0
  } catch {
    ElMessage.error('获取商品列表失败')
  } finally {
    loading.value = false
  }
}

const fetchCategories = async () => {
  try {
    const res = await getCategories()
    categoryList.value = res.data || []
  } catch {
    // silent
  }
}

const openDialog = (row) => {
  isEdit.value = !!row
  editingId.value = row?.id || null
  if (row) {
    Object.assign(productForm, {
      name: row.name,
      price: row.price,
      categoryId: row.categoryId,
      imageUrl: row.imageUrl || '',
      description: row.description || ''
    })
  } else {
    Object.assign(productForm, {
      name: '',
      price: 0,
      categoryId: null,
      imageUrl: '',
      description: ''
    })
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  const valid = await productFormRef.value.validate().catch(() => false)
  if (!valid) return

  saving.value = true
  try {
    if (isEdit.value) {
      await updateProduct(editingId.value, { ...productForm })
      ElMessage.success('修改成功')
    } else {
      await createProduct({ ...productForm })
      ElMessage.success('添加成功')
    }
    dialogVisible.value = false
    fetchProducts()
  } catch {
    ElMessage.error('操作失败，请重试')
  } finally {
    saving.value = false
  }
}

const handleDelete = async (id) => {
  try {
    await deleteProduct(id)
    ElMessage.success('删除成功')
    fetchProducts()
  } catch {
    ElMessage.error('删除失败')
  }
}

const handleToggle = async (row) => {
  try {
    await toggleProductStatus(row.id, row.status === 1 ? 0 : 1)
    ElMessage.success(row.status === 1 ? '已下架' : '已上架')
    fetchProducts()
  } catch {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchProducts()
  fetchCategories()
})
</script>

<style scoped>
.product-manage {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}
</style>
