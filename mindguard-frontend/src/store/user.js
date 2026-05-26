import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, getInfo } from '@/api/auth'
import { setToken, removeToken, getToken } from '@/utils/auth'

export const useUserStore = defineStore('user', () => {
  const token = ref(getToken() || '')
  const userInfo = ref(null)
  const role = ref(localStorage.getItem('userRole') || '')

  const isLoggedIn = computed(() => !!token.value)

  async function login(form) {
    const res = await loginApi(form)
    const data = res.data
    token.value = data.token
    setToken(data.token)
    role.value = data.role
    localStorage.setItem('userRole', data.role)
    userInfo.value = {
      id: data.id,
      username: data.username,
      realName: data.realName,
      role: data.role,
      email: data.email,
      phone: data.phone
    }
    return data
  }

  async function fetchInfo() {
    const res = await getInfo()
    userInfo.value = res.data
    role.value = res.data.role
    localStorage.setItem('userRole', res.data.role)
    return res.data
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    role.value = ''
    removeToken()
    localStorage.removeItem('userRole')
  }

  return { token, userInfo, role, isLoggedIn, login, fetchInfo, logout }
})
