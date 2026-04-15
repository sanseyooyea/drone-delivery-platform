import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getToken, setToken, removeToken, getUserInfo, setUserInfo, type UserInfo } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken())
  const userInfo = ref<UserInfo | null>(getUserInfo())

  function login(data: { token: string; userId: number; username: string; role: number }) {
    token.value = data.token
    const info: UserInfo = { userId: data.userId, username: data.username, role: data.role }
    userInfo.value = info
    setToken(data.token)
    setUserInfo(info)
  }

  function logout() {
    token.value = null
    userInfo.value = null
    removeToken()
  }

  const isLoggedIn = () => !!token.value
  const isAdmin = () => userInfo.value?.role === 2
  const isMerchant = () => userInfo.value?.role === 1

  return { token, userInfo, login, logout, isLoggedIn, isAdmin, isMerchant }
})
