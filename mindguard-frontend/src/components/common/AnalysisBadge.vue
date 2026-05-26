<template>
  <el-tag :type="tagType" size="small" effect="plain">{{ analysisLabel }}</el-tag>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  label: { type: String, default: '' }
})

const labelMap = {
  POSITIVE: '积极',
  NEUTRAL: '中性',
  MILD_NEGATIVE: '轻度消极',
  SEVERE_NEGATIVE: '严重消极'
}

const analysisLabel = computed(() => {
  if (!props.label) return '未分析'
  return labelMap[props.label] || props.label
})

const tagType = computed(() => {
  if (!props.label) return 'info'
  const l = props.label
  if (l === 'SEVERE_NEGATIVE' || l.includes('危机') || l.includes('自杀') || l.includes('绝望')) return 'danger'
  if (l === 'MILD_NEGATIVE' || l.includes('焦虑') || l.includes('抑郁') || l.includes('难过')) return 'warning'
  return 'success'
})
</script>
