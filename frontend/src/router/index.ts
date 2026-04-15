import { createRouter, createWebHistory } from 'vue-router'
import { getToken } from '@/utils/auth'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: '/login', component: () => import('@/views/common/LoginPage.vue') },
    { path: '/register', component: () => import('@/views/common/RegisterPage.vue') },
    {
      path: '/',
      component: () => import('@/layouts/UserLayout.vue'),
      children: [
        { path: '', component: () => import('@/views/user/HomePage.vue') },
        { path: 'products', component: () => import('@/views/user/ProductList.vue') },
        { path: 'products/:id', component: () => import('@/views/user/ProductDetail.vue') },
        { path: 'cart', component: () => import('@/views/user/CartPage.vue') },
        { path: 'order/confirm', component: () => import('@/views/user/OrderConfirm.vue') },
        { path: 'orders', component: () => import('@/views/user/OrderList.vue') },
        { path: 'orders/:id', component: () => import('@/views/user/OrderDetail.vue') },
        { path: 'addresses', component: () => import('@/views/user/AddressManage.vue') },
        { path: 'chat', component: () => import('@/views/user/ChatPage.vue') },
        { path: 'profile', component: () => import('@/views/user/ProfilePage.vue') },
      ],
    },
    {
      path: '/merchant',
      component: () => import('@/layouts/MerchantLayout.vue'),
      children: [
        { path: 'apply', component: () => import('@/views/merchant/MerchantApply.vue') },
        { path: 'products', component: () => import('@/views/merchant/ProductManage.vue') },
        { path: 'orders', component: () => import('@/views/merchant/OrderManage.vue') },
        { path: 'dashboard', component: () => import('@/views/merchant/Dashboard.vue') },
      ],
    },
    {
      path: '/admin',
      component: () => import('@/layouts/AdminLayout.vue'),
      children: [
        { path: 'dashboard', component: () => import('@/views/admin/AdminDashboard.vue') },
        { path: 'users', component: () => import('@/views/admin/UserManage.vue') },
        { path: 'merchants', component: () => import('@/views/admin/MerchantManage.vue') },
        { path: 'drones', component: () => import('@/views/admin/DroneManage.vue') },
        { path: 'orders', component: () => import('@/views/admin/OrderManage.vue') },
        { path: 'chat', component: () => import('@/views/admin/ChatWorkbench.vue') },
      ],
    },
    { path: '/:pathMatch(.*)*', component: () => import('@/views/common/NotFound.vue') },
  ],
})

router.beforeEach((to, _from, next) => {
  const whiteList = ['/login', '/register']
  if (whiteList.includes(to.path)) {
    next()
  } else if (!getToken()) {
    next('/login')
  } else {
    next()
  }
})

export default router
