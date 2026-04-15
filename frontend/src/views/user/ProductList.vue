<template>
  <div class="product-list-page">
    <el-row :gutter="20">
      <!-- 左侧分类筛选 -->
      <el-col :span="4">
        <el-card class="filter-card">
          <template #header>
            <span class="filter-title">商品分类</span>
          </template>
          <el-menu :default-active="activeCategoryId" @select="onCategorySelect">
            <el-menu-item index="">全部分类</el-menu-item>
            <el-menu-item
              v-for="cat in categories"
              :key="cat.id"
              :index="String(cat.id)"
            >
              {{ cat.name }}
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>

      <!-- 右侧商品列表 -->
      <el-col :span="20">
        <!-- 搜索栏 -->
        <div class="search-bar">
          <el-input
            v-model="keyword"
            placeholder="搜索商品"
            clearable
            @keyup.enter="search"
            size="large"
          >
            <template #append>
              <el-button @click="search" :icon="Search">搜索</el-button>
            </template>
          </el-input>
        </div>

        <!-- 排序 -->
        <div class="sort-bar">
          <el-radio-group v-model="sortBy" @change="fetchProducts">
            <el-radio-button value="default">综合排序</el-radio-button>
            <el-radio-button value="sales">销量优先</el-radio-button>
            <el-radio-button value="priceAsc">价格升序</el-radio-button>
            <el-radio-button value="priceDesc">价格降序</el-radio-button>
          </el-radio-group>
        </div>

        <!-- 商品网格 -->
        <el-row :gutter="16" v-loading="loading">
          <el-col :xs="12" :sm="8" :md="6" v-for="product in products" :key="product.id">
            <el-card shadow="hover" class="product-card" @click="goDetail(product.id)">
              <el-image :src="product.image" fit="cover" class="product-image">
                <template #error>
                  <div class="image-placeholder">
                    <el-icon :size="40"><Picture /></el-icon>
                  </div>
                </template>
              </el-image>
              <div class="product-info">
                <div class="product-name">{{ product.name }}</div>
                <div class="product-meta">
                  <span class="product-price">¥{{ product.price }}</span>
                  <span class="product-sales">已售 {{ product.sales || 0 }}</span>
                </div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="24" v-if="!loading && products.length === 0">
            <el-empty description="暂无商品" />
          </el-col>
        </el-row>

        <!-- 分页 -->
        <div class="pagination-wrap">
          <el-pagination
            v-model:current-page="page"
            v-model:page-size="pageSize"
            :total="total"
            :page-sizes="[12, 24, 36]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="fetchProducts"
            @current-change="fetchProducts"
          />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search, Picture } from '@element-plus/icons-vue'
import { getProducts, getCategories } from '@/api/product'

const router = useRouter()
const route = useRoute()

const categories = ref([])
const products = ref([])
const loading = ref(false)
const keyword = ref('')
const activeCategoryId = ref('')
const sortBy = ref('default')
const page = ref(1)
const pageSize = ref(12)
const total = ref(0)

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
    const res = await getProducts({
      page: page.value,
      size: pageSize.value,
      keyword: keyword.value,
      categoryId: activeCategoryId.value || undefined,
      sort: sortBy.value,
    })
    products.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) {
    console.error('获取商品失败', e)
  } finally {
    loading.value = false
  }
}

const onCategorySelect = (index) => {
  activeCategoryId.value = index
  page.value = 1
  fetchProducts()
}

const search = () => {
  page.value = 1
  fetchProducts()
}

const goDetail = (id) => {
  router.push(`/products/${id}`)
}

onMounted(() => {
  if (route.query.categoryId) {
    activeCategoryId.value = String(route.query.categoryId)
  }
  fetchCategories()
  fetchProducts()
})
</script>

<style scoped>
.product-list-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 16px;
}
.filter-card :deep(.el-card__body) {
  padding: 0;
}
.filter-title {
  font-weight: bold;
}
.search-bar {
  margin-bottom: 16px;
}
.sort-bar {
  margin-bottom: 16px;
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
.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}
.product-price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}
.product-sales {
  color: #999;
  font-size: 12px;
}
.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
