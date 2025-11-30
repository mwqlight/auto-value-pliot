import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { loginApi, registerApi, getUserInfoApi } from '@/api/auth'
import type { LoginRequest, UserInfo } from '@/types/auth'

export const useUserStore = defineStore('user', () => {
  // 状态
  const token = ref<string>('')
  const userInfo = ref<UserInfo | null>(null)
  
  // 计算属性
  const isAuthenticated = computed(() => !!token.value)
  const username = computed(() => userInfo.value?.username || '')
  const nickname = computed(() => userInfo.value?.nickname || '')
  
  // Actions
  const login = async (loginData: LoginRequest) => {
    try {
      const response = await loginApi(loginData)
      if (response.code === 200) {
        token.value = response.data.accessToken
        userInfo.value = {
          userId: response.data.userId,
          username: response.data.username,
          nickname: response.data.nickname
        }
        
        // 保存到localStorage
        localStorage.setItem('token', token.value)
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
        
        return Promise.resolve(response)
      } else {
        return Promise.reject(new Error(response.message))
      }
    } catch (error) {
      return Promise.reject(error)
    }
  }
  
  const register = async (userData: any) => {
    try {
      const response = await registerApi(userData)
      return Promise.resolve(response)
    } catch (error) {
      return Promise.reject(error)
    }
  }
  
  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }
  
  const loadUserInfo = async () => {
    if (!token.value) return
    
    try {
      const response = await getUserInfoApi()
      if (response.code === 200) {
        userInfo.value = response.data
      }
    } catch (error) {
      console.error('加载用户信息失败:', error)
      logout()
    }
  }
  
  const initAuth = () => {
    const savedToken = localStorage.getItem('token')
    const savedUserInfo = localStorage.getItem('userInfo')
    
    if (savedToken && savedUserInfo) {
      token.value = savedToken
      userInfo.value = JSON.parse(savedUserInfo)
    }
  }
  
  return {
    token,
    userInfo,
    isAuthenticated,
    username,
    nickname,
    login,
    register,
    logout,
    loadUserInfo,
    initAuth
  }
})