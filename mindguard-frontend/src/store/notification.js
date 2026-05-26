import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { getPendingCount } from '@/api/alert'
import { getUnreadCount } from '@/api/notification'

export const useNotificationStore = defineStore('notification', () => {
  const alertCount = ref(0)
  const notifyCount = ref(0)

  const totalCount = computed(() => alertCount.value + notifyCount.value)

  async function fetchUnreadCount() {
    try {
      const [alertRes, notifyRes] = await Promise.allSettled([
        getPendingCount(),
        getUnreadCount()
      ])
      alertCount.value = alertRes.status === 'fulfilled' ? (alertRes.value.data || 0) : 0
      notifyCount.value = notifyRes.status === 'fulfilled' ? (notifyRes.value.data || 0) : 0
    } catch {
      alertCount.value = 0
      notifyCount.value = 0
    }
  }

  function clearCount() {
    alertCount.value = 0
    notifyCount.value = 0
  }

  return { alertCount, notifyCount, totalCount, fetchUnreadCount, clearCount }
})
