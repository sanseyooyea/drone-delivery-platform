<template>
  <div class="product-detail-page" v-loading="loading">
    <el-breadcrumb separator="/">
      <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
      <el-breadcrumb-item :to="{ path: '/products' }">商品列表</el-breadcrumb-item>
      <el-breadcrumb-item>商品详情</el-breadcrumb-item>
    </el-breadcrumb>

    <div class="detail-content" v-if="product">
      <el-row :gutter="32">
        <!-- 商品图片 -->
        <el-col :span="10">
          <el-image
            :src="product.image"
            fit="contain"
            class="main-image"
          >
            <template #error>
              <div class="image-placeholder">
                <el-icon :size="64"><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </el-col>

        <!-- 商品信息 -->
        <el-col :span="14">
          <h1 class="product-name">{{ product.name }}</h1>
          <p class="product-desc">{{ product.description }}</p>

          <div class="price-box">
            <span class="label">价格</span>
            <span class="price">¥{{ product.price }}</span>
            <span v-if="product.originalPrice" class="original-price">
              ¥{{ product.originalPrice }}
            </span>
          </div>

          <div class="info-row">
            <span class="label">销量</span>
            <span>{{ product.sales || 0 }}</span>
          </div>
          <div class="info-row">
            <span class="label">库存</span>
            <span>{{ product.stock || 0 }}</span>
          </div>

          <el-divider />

          <div class="quantity-row">
            <span class="label">数量</span>
            <el-input-number
              v-model="quantity"
              :min="1"
              :max="product.stock || 99"
              size="large"
            />
          </div>

          <div class="action-buttons">
            <el-button
              type="warning"
              size="large"
              @click="handleAddToCart"
              :loading="addingCart"
            >
              <el-icon><ShoppingCart /></el-icon>
              加入购物车
            </el-button>
            <el-button
              type="danger"
              size="large"
              @click="handleBuyNow"
            >
              立即购买
            </el-button>
          </div>
        </el-col>
      </el-row>

      <!-- 商品详情描述 -->
      <el-card class="detail-card">
        <template #header>
          <span>商品详情</span>
        </template>
        <div class="rich-content">{{ product.description || '暂无详细描述' }}</div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture, ShoppingCart } from '@element-plus/icons-vue'
import { getProduct } from '@/api/product'
import { addToCart } from '@/api/cart'

const route = useRoute()
const router = useRouter()

const product = ref(null)
const quantity = ref(1)
const loading = ref(false)
const addingCart = ref(false)

const fetchProduct = async () => {
  loading.value = true
  try {
    const res = await getProduct(route.params.id)
    product.value = res.data
  } catch (e) {
    ElMessage.error('获取商品信息失败')
  } finally {
    loading.value = false
  }
}

const handleAddToCart = async () => {
  addingCart.value = true
  try {
    await addToCart(product.value.id, quantity.value)
    ElMessage.success('已加入购物车')
  } catch (e) {
    ElMessage.error('加入购物车失败')
  } finally {
    addingCart.value = false
  }
}

const handleBuyNow = async () => {
  try {
    await addToCart(product.value.id, quantity.value)
    router.push('/cart')
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  fetchProduct()
})
</script>

<style scoped>
.product-detail-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px;
}
.detail-content {
  margin-top: 20px;
}
.main-image {
  width: 100%;
  height: 400px;
  border-radius: 8px;
  border: 1px solid #eee;
}
.image-placeholder {
  width: 100%;
  height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
}
.product-name {
  font-size: 22px;
  margin: 0 0 8px;
}
.product-desc {
  color: #666;
  font-size: 14px;
  margin-bottom: 16px;
}
.price-box {
  background: #fff8f0;
  padding: 16px;
  border-radius: 8px;
  margin-bottom: 16px;
}
.price-box .price {
  color: #f56c6c;
  font-size: 28px;
  font-weight: bold;
}
.price-box .original-price {
  color: #999;
  text-decoration: line-through;
  margin-left: 12px;
  font-size: 16px;
}
.info-row {
  padding: 8px 0;
  font-size: 14px;
}
.label {
  color: #999;
  margin-right: 16px;
  min-width: 40px;
  display: inline-block;
}
.quantity-row {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}
.action-buttons {
  display: flex;
  gap: 16px;
}
.action-buttons .el-button {
  width: 160px;
  height: 48px;
  font-size: 16px;
}
.detail-card {
  margin-top: 32px;
}
.rich-content {
  min-height: 200px;
  line-height: 1.8;
}
</style>
