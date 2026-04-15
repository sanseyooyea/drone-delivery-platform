<template>
  <div class="home-page">
    <!-- 轮播图 -->
    <el-carousel height="300px" class="banner">
      <el-carousel-item v-for="i in 3" :key="i">
        <div class="banner-item" :style="{ background: ['#409EFF', '#67C23A', '#E6A23C'][i - 1] }">
          <h2>无人机配送 · 快速送达</h2>
        </div>
      </el-carousel-item>
    </el-carousel>

    <!-- 分类网格 -->
    <div class="section">
      <h3 class="section-title">商品分类</h3>
      <el-row :gutter="16">
        <el-col :xs="8" :sm="6" :md="4" v-for="cat in categories" :key="cat.id">
          <div class="category-card" @click="goCategory(cat.id)">
            <el-icon :size="32"><Goods /></el-icon>
            <span>{{ cat.name }}</span>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 推荐商品 -->
    <div class="section">
      <h3 class="section-title">热门推荐</h3>
      <el-row :gutter="16">
        <el-col :xs="12" :sm="8" :md="6" v-for="product in products" :key="product.id">
          <el-card shadow="hover" class="product-card" @click="goProduct(product.id)">
            <el-image
              :src="product.image"
              fit="cover"
              class="product-image"
            >
              <template #error>
                <div class="image-placeholder">
                  <el-icon :size="40"><Picture /></el-icon>
                </div>
              </template>
            </el-image>
            <div class="product-info">
              <div class="product-name">{{ product.name }}</div>
              <div class="product-price">¥{{ product.price }}</div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Goods, Picture } from '@element-plus/icons-vue'
import { getProducts, getCategories } from '@/api/product'

const router = useRouter()
const categories = ref([])
const products = ref([])
const loading = ref(false)

const fetchCategories = async () => {
  try {
    const res = await getCategories()
    categories.value = res.data || []
  } catch (e) {
    console.error('获取分类失败', e)
  }
}

const fetchProducts = async () => {
  loading.value = true
  try {
    const res = await getProducts({ page: 1, size: 8 })
    products.value = res.data?.list || []
  } catch (e) {
    console.error('获取商品失败', e)
  } finally {
    loading.value = false
  }
}

const goCategory = (id) => {
  router.push({ path: '/products', query: { categoryId: id } })
}

const goProduct = (id) => {
  router.push(`/products/${id}`)
}

onMounted(() => {
  fetchCategories()
  fetchProducts()
})
</script>

<style scoped>
.home-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px;
}
.banner-item {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 24px;
  border-radius: 8px;
}
.section {
  margin-top: 32px;
}
.section-title {
  font-size: 20px;
  margin-bottom: 16px;
  padding-left: 8px;
  border-left: 4px solid #409eff;
}
.category-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 0;
  cursor: pointer;
  border-radius: 8px;
  transition: background 0.2s;
}
.category-card:hover {
  background: #f0f9ff;
}
.product-card {
  cursor: pointer;
  margin-bottom: 16px;
}
.product-card :deep(.el-card__body) {
  padding: 0;
}
.product-image {
  width: 100%;
  height: 180px;
}
.image-placeholder {
  width: 100%;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
}
.product-info {
  padding: 12px;
}
.product-name {
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.product-price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
  margin-top: 6px;
}
</style>
