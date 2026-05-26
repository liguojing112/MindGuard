<template>
  <v-chart v-if="option" :option="option" :style="{ height: height || '300px' }" autoresize />
  <EmptyState v-else message="暂无数据" />
</template>

<script setup>
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { PieChart } from 'echarts/charts'
import { TooltipComponent, LegendComponent } from 'echarts/components'
import { CanvasRenderer } from 'echarts/renderers'
import EmptyState from './EmptyState.vue'

use([PieChart, TooltipComponent, LegendComponent, CanvasRenderer])

const props = defineProps({
  data: { type: Array, default: () => [] },
  height: { type: String, default: '300px' }
})

const option = computed(() => {
  if (!props.data || !props.data.length) return null
  return {
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 6, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      data: props.data.map(d => ({ name: d.name || d.label, value: d.value || d.count }))
    }],
    color: ['#67C23A', '#A78BFA', '#E6A23C', '#F56C6C', '#409EFF', '#909399', '#85CE61', '#B3E19D']
  }
})
</script>
