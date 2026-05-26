<template>
  <v-chart v-if="option" :option="option" style="height: 200px" autoresize />
</template>

<script setup>
import { computed } from 'vue'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { GaugeChart } from 'echarts/charts'
import { CanvasRenderer } from 'echarts/renderers'

use([GaugeChart, CanvasRenderer])

const props = defineProps({
  score: { type: Number, default: 0 },
  max: { type: Number, default: 100 }
})

const option = computed(() => ({
  series: [{
    type: 'gauge',
    startAngle: 210,
    endAngle: -30,
    min: 0,
    max: props.max,
    progress: { show: true, width: 14, itemStyle: { color: '#67C23A' } },
    axisLine: { lineStyle: { width: 14, color: [[1, '#f0f0f0']] } },
    axisTick: { show: false },
    splitLine: { show: false },
    axisLabel: { show: false },
    detail: {
      valueAnimation: true,
      fontSize: 28,
      fontWeight: 'bold',
      color: '#303133',
      formatter: '{value}'
    },
    pointer: { show: false },
    data: [{ value: props.score }]
  }]
}))
</script>
