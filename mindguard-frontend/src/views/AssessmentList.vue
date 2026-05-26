<template>
  <div class="page-container">
    <div class="page-header">
      <h2>心理测评</h2>
      <p class="subtitle">选择量表开始测评，了解你的心理状态</p>
    </div>

    <div v-loading="loading">
      <template v-if="scales.length">
        <ScaleCard v-for="scale in scales" :key="scale.id" :scale="scale" @start="goToAssessment" />
      </template>
      <EmptyState v-else-if="!loading" message="暂无可用量表" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getScales } from '@/api/assessment'
import ScaleCard from '@/components/common/ScaleCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const router = useRouter()
const loading = ref(false)
const scales = ref([])

onMounted(async () => {
  loading.value = true
  try {
    const res = await getScales()
    scales.value = res.data || []
  } catch { /* */ }
  finally { loading.value = false }
})

function goToAssessment(scale) {
  router.push(`/student/assessments/${scale.id}`)
}
</script>

<style lang="scss" scoped>
.subtitle { color: #909399; font-size: 14px; margin-top: 4px; }
</style>
