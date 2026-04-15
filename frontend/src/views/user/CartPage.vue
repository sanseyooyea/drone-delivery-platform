<template>
  <div class="cart-page">
    <h2 class="page-title">我的购物车</h2>

    <el-table
      :data="cartItems"
      v-loading="loading"
      style="width: 100%"
      @selection-change="onSelectionChange"
    >
      <el-table-column type="selection" width="50" />
      <el-table-column label="商品" min-width="300">
        <template #default="{ row }">
          <div class="product-cell">
            <el-image :src="row.image" fit="cover" class="cart-thumb">
              <template #error>
                <div class="thumb-placeholder">
                  <el-icon><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <span class="product-name">{{ row.productName }}</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="单价" width="120" align="center">
        <template #default="{ row }">
          <span class="price-text">¥{{ row.productPrice }}</span>
        </template>
      </el-table-column>
      <el-table-column label="数量" width="180" align="center">
        <template #default="{ row }">
          <el-input-number
            v-model="row.quantity"
            :min="1"
            :max="99"
            size="small"
            @change="(val) => handleQuantityChange(row, val)"
          />
        </template>
      </el-table-column>
      <el-table-column label="小计" width="120" align="center">
        <template #default="{ row }">
          <span class="subtotal-text">¥{{ (row.productPrice * row.quantity).toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center">
        <template #default="{ row }">
          <el-popconfirm
            title="确定删除该商品吗？"
            @confirm="handleRemove(row)"
          >
            <template #reference>
              <el-button type="danger" link>删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-empty v-if="!loading && cartItems.length === 0" description="购物车是空的">
      <el-button type="primary" @click="$router.push('/products')">去购物</el-button>
    </el-empty>

    <!-- 底部结算栏 -->
    <div class="cart-footer" v-if="cartItems.length > 0">
      <div class="footer-left">
        <el-checkbox
          v-model="isAllSelected"
          @change="handleSelectAll"
        >
          全选
        </el-checkbox>
        <span class="selected-count">已选 {{ selectedItems.length }} 件</span>
      </div>
      <div class="footer-right">
        <span class="total-label">合计：</span>
        <span class="total-price">¥{{ totalPrice }}</span>
        <el-button
          type="danger"
          size="large"
          :disabled="selectedItems.length === 0"
          @click="handleCheckout"
        >
          去结算
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { getCart, updateCartQuantity, removeFromCart } from '@/api/cart'

const router = useRouter()
const cartItems = ref([])
const selectedItems = ref([])
const loading = ref(false)

const isAllSelected = computed({
  get: () => cartItems.value.length > 0 && selectedItems.value.length === cartItems.value.length,
  set: () => {},
})

const totalPrice = computed(() => {
  return selectedItems.value
    .reduce((sum, item) => sum + item.productPrice * item.quantity, 0)
    .toFixed(2)
})

const fetchCart = async () => {
  loading.value = true
  try {
    const res = await getCart()
    cartItems.value = res.data || []
  } catch (e) {
    ElMessage.error('获取购物车失败')
  } finally {
    loading.value = false
  }
}

const onSelectionChange = (selection) => {
  selectedItems.value = selection
}

const handleSelectAll = (val) => {
  selectedItems.value = val ? [...cartItems.value] : []
}

const handleQuantityChange = async (row, val) => {
  try {
    await updateCartQuantity(row.id, val)
  } catch (e) {
    ElMessage.error('更新数量失败')
    fetchCart()
  }
}

const handleRemove = async (row) => {
  try {
    await removeFromCart(row.id)
    ElMessage.success('已删除')
    fetchCart()
  } catch (e) {
    ElMessage.error('删除失败')
  }
}

const handleCheckout = () => {
  const ids = selectedItems.value.map((item) => item.id).join(',')
  router.push({ path: '/order/confirm', query: { cartIds: ids } })
}

onMounted(() => {
  fetchCart()
})
</script>

<style scoped>
.cart-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px;
}
.page-title {
  font-size: 20px;
  margin-bottom: 20px;
}
.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}
.cart-thumb {
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
.product-name {
  font-size: 14px;
}
.price-text {
  color: #333;
}
.subtotal-text {
  color: #f56c6c;
  font-weight: bold;
}
.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 20px;
  padding: 16px 20px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  position: sticky;
  bottom: 0;
}
.footer-left {
  display: flex;
  align-items: center;
  gap: 16px;
}
.selected-count {
  color: #999;
  font-size: 14px;
}
.footer-right {
  display: flex;
  align-items: center;
  gap: 16px;
}
.total-label {
  font-size: 14px;
}
.total-price {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
}
</style>
