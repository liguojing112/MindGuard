<template>
  <v-chart v-if="option" :option="option" :style="{ height: height || '300px' }" autoresize />
  <EmptyState v-else message="暂无数据" />
</template>

<script setup>
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { LineChart } from 'echarts/charts'
import { TooltipComponent, GridComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import EmptyState from './EmptyState.vue'

use([LineChart, TooltipComponent, GridComponent, LegendComponent, CanvasRenderer])

const props = defineProps({
  data: { type: [Array, Object], default: () => [] },
  height: { type: String, default: '300px' }
})

const option = computed(() => {
  const d = props.data
  if (!d) return null
  const arr = Array.isArray(d) ? d : (d.dates ? d : null)
  if (!arr || !arr.length) return null

  const xData = arr.map(i => i.date || i.name || i.label)
  const yData = arr.map(i => i.count || i.value || 0)

  return {
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: xData, axisLine: { lineStyle: { color: '#ddd' } } },
    yAxis: { type: 'value', axisLine: { show: false }, splitLine: { lineStyle: { color: '#f0f0f0' } } },
    series: [{
      type: 'line',
      data: yData,
      smooth: true,
      symbol: 'circle',
      symbolSize: 6,
      lineStyle: { color: '#67C23A', width: 2 },
      itemStyle: { color: '#67C23A' },
      areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(103,194,58,0.2)' }, { offset: 1, color: 'rgba(103,194,58,0)' }] } }
    }]
  }
})
</script>
