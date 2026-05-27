<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getToken } from '@/utils/auth'

const userStore = useUserStore()

onMounted(() => {
  if (getToken() && !userStore.userInfo) {
    userStore.fetchInfo()
  }
})
</script>
