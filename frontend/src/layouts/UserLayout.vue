<template>
  <div class="user-layout">
    <el-header class="header">
      <div class="logo" @click="$router.push('/')">无人机送货平台</div>
      <el-menu mode="horizontal" :ellipsis="false" router class="nav-menu">
        <el-menu-item index="/">首页</el-menu-item>
        <el-menu-item index="/products">商品</el-menu-item>
        <el-menu-item index="/orders">我的订单</el-menu-item>
        <el-menu-item index="/chat">在线客服</el-menu-item>
      </el-menu>
      <div class="header-right">
        <el-badge :value="cartCount" :hidden="cartCount === 0" class="cart-badge">
          <el-button :icon="ShoppingCart" circle @click="$router.push('/cart')" />
        </el-badge>
        <el-dropdown>
          <el-button :icon="User" circle />
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="$router.push('/profile')">个人中心</el-dropdown-item>
              <el-dropdown-item @click="$router.push('/addresses')">地址管理</el-dropdown-item>
              <el-dropdown-item divided @click="handleLogout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </el-header>
    <el-main class="main">
      <router-view />
    </el-main>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ShoppingCart, User } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const cartCount = ref(0)

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.user-layout { min-height: 100vh; background: #f5f5f5; }
.header {
  display: flex;
  align-items: center;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  padding: 0 20px;
}
.logo {
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
  cursor: pointer;
  white-space: nowrap;
  margin-right: 20px;
}
.nav-menu { flex: 1; border-bottom: none; }
.header-right { display: flex; align-items: center; gap: 12px; }
.cart-badge { margin-right: 4px; }
.main { max-width: 1200px; margin: 0 auto; padding: 20px; }
</style>
