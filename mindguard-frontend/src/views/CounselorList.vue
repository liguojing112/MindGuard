<template>
  <div class="page-container">
    <div class="page-header">
      <h2>心理咨询师</h2>
      <p class="subtitle">选择一位咨询师，开始你的心灵对话</p>
    </div>

    <div v-loading="loading">
      <template v-if="counselors.length">
        <CounselorCard
          v-for="counselor in counselors"
          :key="counselor.id"
          :counselor="counselor"
          @book="goBook"
        />
      </template>
      <EmptyState v-else-if="!loading" message="暂无可用咨询师" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getCounselors } from '@/api/appointment'
import CounselorCard from '@/components/common/CounselorCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const router = useRouter()
const loading = ref(false)
const counselors = ref([])

onMounted(async () => {
  loading.value = true
  try {
    const res = await getCounselors()
    counselors.value = res.data || []
  } catch { /* */ }
  finally { loading.value = false }
})

function goBook(counselor) {
  router.push(`/student/counselors/${counselor.id}/book`)
}
</script>

<style lang="scss" scoped>
.subtitle { color: #909399; font-size: 14px; margin-top: 4px; }
</style>
