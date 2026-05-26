<template>
  <v-chart v-if="option" :option="option" :style="{ height: height || '300px' }" autoresize />
  <EmptyState v-else message="暂无数据" />
</template>

<script setup>
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { BarChart } from 'echarts/charts'
import { TooltipComponent, GridComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import EmptyState from './EmptyState.vue'

use([BarChart, TooltipComponent, GridComponent, CanvasRenderer])

const props = defineProps({
  data: { type: [Array, Object], default: () => [] },
  height: { type: String, default: '300px' }
})

const option = computed(() => {
  const d = props.data
  if (!d) return null
  const entries = Array.isArray(d) ? d : Object.entries(d).map(([k, v]) => ({ name: k, value: typeof v === 'object' ? v.count || 0 : v }))
  if (!entries.length) return null

  const xData = entries.map(i => i.name || i.label)
  const yData = entries.map(i => i.value || i.count || 0)

  return {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: xData, axisLine: { lineStyle: { color: '#ddd' } } },
    yAxis: { type: 'value', axisLine: { show: false }, splitLine: { lineStyle: { color: '#f0f0f0' } } },
    series: [{
      type: 'bar',
      data: yData,
      barWidth: '50%',
      itemStyle: {
        color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: '#95D475' }, { offset: 1, color: '#67C23A' }] },
        borderRadius: [6, 6, 0, 0]
      }
    }]
  }
})
</script>
