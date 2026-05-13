import { defineStore } from 'pinia'
import { login as loginApi, logout as logoutApi } from '@/api/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    refreshToken: localStorage.getItem('refreshToken') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
  }),
  getters: {
    isLoggedIn: (state) => !!state.token,
  },
  actions: {
    async login(loginData) {
      const data = await loginApi(loginData)
      this.token = data.token
      this.refreshToken = data.refreshToken
      this.userInfo = data.user
      localStorage.setItem('token', data.token)
      localStorage.setItem('refreshToken', data.refreshToken)
      localStorage.setItem('userInfo', JSON.stringify(data.user))
      return data
    },
    async logout() {
      try {
        await logoutApi()
      } finally {
        this.token = ''
        this.refreshToken = ''
        this.userInfo = null
        localStorage.clear()
      }
    },
  },
})
