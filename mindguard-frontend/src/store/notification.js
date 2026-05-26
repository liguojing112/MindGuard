import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getPendingCount } from '@/api/alert'

export const useNotificationStore = defineStore('notification', () => {
  const unreadCount = ref(0)

  async function fetchUnreadCount() {
    try {
      const res = await getPendingCount()
      unreadCount.value = res.data || 0
    } catch {
      unreadCount.value = 0
    }
  }

  function clearCount() {
    unreadCount.value = 0
  }

  return { unreadCount, fetchUnreadCount, clearCount }
})
