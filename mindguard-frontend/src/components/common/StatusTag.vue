<template>
  <el-tag :type="color" :size="size" :effect="effect">
    {{ label }}
  </el-tag>
</template>

<script setup>
import { computed } from 'vue'
import { statusLabels, statusColors } from '@/utils/constants'

const props = defineProps({
  status: { type: String, required: true },
  size: { type: String, default: 'default' },
  effect: { type: String, default: 'light' }
})

const label = computed(() => statusLabels[props.status] || props.status)
const color = computed(() => statusColors[props.status] || 'info')
</script>
