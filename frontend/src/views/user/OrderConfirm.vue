<template>
  <div class="order-confirm-page" v-loading="loading">
    <h2 class="page-title">确认订单</h2>

    <!-- 收货地址 -->
    <el-card class="section-card">
      <template #header>
        <div class="card-header">
          <span>收货地址</span>
          <el-button type="primary" link @click="$router.push('/address')">管理地址</el-button>
        </div>
      </template>
      <el-radio-group v-model="selectedAddressId" class="address-list">
        <el-radio
          v-for="addr in addresses"
          :key="addr.id"
          :value="addr.id"
          class="address-item"
        >
          <div class="address-info">
            <span class="name">{{ addr.name }}</span>
            <span class="phone">{{ addr.phone }}</span>
            <el-tag v-if="addr.isDefault" size="small" type="warning">默认</el-tag>
          </div>
          <div class="address-detail">{{ addr.province }}{{ addr.city }}{{ addr.district }}{{ addr.detail }}</div>
        </el-radio>
      </el-radio-group>
      <el-empty v-if="addresses.length === 0" description="暂无收货地址，请先添加" :image-size="60" />
    </el-card>

    <!-- 订单商品 -->
    <el-card class="section-card">
      <template #header>
        <span>订单商品</span>
      </template>
      <el-table :data="orderItems" style="width: 100%">
        <el-table-column label="商品" min-width="280">
          <template #default="{ row }">
            <div class="product-cell">
              <el-image :src="row.image" fit="cover" class="item-thumb">
                <template #error>
                  <div class="thumb-placeholder"><el-icon><Picture /></el-icon></div>
                </template>
              </el-image>
              <span>{{ row.productName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="单价" width="120" align="center">
          <template #default="{ row }">¥{{ row.price }}</template>
        </el-table-column>
        <el-table-column label="数量" prop="quantity" width="100" align="center" />
        <el-table-column label="小计" width="120" align="center">
          <template #default="{ row }">
            <span class="subtotal">¥{{ (row.price * row.quantity).toFixed(2) }}</span>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 备注 -->
    <el-card class="section-card">
      <template #header>
        <span>订单备注</span>
      </template>
      <el-input
        v-model="remark"
        type="textarea"
        :rows="3"
        placeholder="选填，可以告诉商家您的特殊需求"
        maxlength="200"
        show-word-limit
      />
    </el-card>

    <!-- 底部提交 -->
    <div class="submit-bar">
      <div class="total-info">
        <span>共 {{ totalQuantity }} 件商品</span>
        <span class="total-label">合计：</span>
        <span class="total-price">¥{{ totalPrice }}</span>
      </div>
      <el-button
        type="danger"
        size="large"
        :disabled="!selectedAddressId || orderItems.length === 0"
        :loading="submitting"
        @click="handleSubmit"
      >
        提交订单
      </el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Picture } from '@element-plus/icons-vue'
import { getCart } from '@/api/cart'
import { getAddresses } from '@/api/address'
import { createOrder } from '@/api/order'

const route = useRoute()
const router = useRouter()

const addresses = ref([])
const selectedAddressId = ref(null)
const orderItems = ref([])
const remark = ref('')
const loading = ref(false)
const submitting = ref(false)

const totalQuantity = computed(() =>
  orderItems.value.reduce((sum, item) => sum + item.quantity, 0)
)

const totalPrice = computed(() =>
  orderItems.value.reduce((sum, item) => sum + item.price * item.quantity, 0).toFixed(2)
)

const fetchAddresses = async () => {
  try {
    const res = await getAddresses()
    addresses.value = res.data || []
    const defaultAddr = addresses.value.find((a) => a.isDefault)
    if (defaultAddr) {
      selectedAddressId.value = defaultAddr.id
    }
  } catch (e) {
    console.error('获取地址失败', e)
  }
}

const fetchOrderItems = async () => {
  loading.value = true
  try {
    const res = await getCart()
    const allItems = res.data || []
    const cartIds = route.query.cartIds?.split(',') || []
    if (cartIds.length > 0) {
      orderItems.value = allItems.filter((item) => cartIds.includes(String(item.id)))
    } else {
      orderItems.value = allItems
    }
  } catch (e) {
    ElMessage.error('获取订单商品失败')
  } finally {
    loading.value = false
  }
}

const handleSubmit = async () => {
  if (!selectedAddressId.value) {
    ElMessage.warning('请选择收货地址')
    return
  }
  submitting.value = true
  try {
    const res = await createOrder({
      addressId: selectedAddressId.value,
      cartIds: orderItems.value.map((item) => item.id),
      remark: remark.value,
    })
    ElMessage.success('下单成功')
    router.push(`/orders/${res.data?.id || ''}`)
  } catch (e) {
    ElMessage.error('提交订单失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  fetchAddresses()
  fetchOrderItems()
})
</script>

<style scoped>
.order-confirm-page {
  max-width: 1000px;
  margin: 0 auto;
  padding: 16px;
}
.page-title {
  font-size: 20px;
  margin-bottom: 20px;
}
.section-card {
  margin-bottom: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.address-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}
.address-item {
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 12px 16px;
  width: 100%;
}
.address-info {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 4px;
}
.address-info .name {
  font-weight: bold;
}
.address-info .phone {
  color: #666;
}
.address-detail {
  color: #666;
  font-size: 13px;
  padding-left: 24px;
}
.product-cell {
  display: flex;
  align-items: center;
  gap: 12px;
}
.item-thumb {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  flex-shrink: 0;
}
.thumb-placeholder {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
}
.subtotal {
  color: #f56c6c;
  font-weight: bold;
}
.submit-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 24px;
  padding: 20px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  position: sticky;
  bottom: 0;
}
.total-info {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
}
.total-price {
  color: #f56c6c;
  font-size: 24px;
  font-weight: bold;
}
</style>
