<template>
  <div class="report-view">
    <div class="report-header">
      <ScoreGauge :score="result.totalScore || result.score || 0" :max="100" />
      <div class="report-summary">
        <h3>{{ result.scaleName || '测评报告' }}</h3>
        <div class="result-level" :style="{ color: levelColor }">
          {{ result.severityDescription || severityLabels[result.severityLevel] || result.severityLevel || '正常' }}
        </div>
        <div class="result-score">
          得分：<strong>{{ result.totalScore || result.score || 0 }}</strong>
        </div>
      </div>
    </div>

    <div v-if="result.reportText || result.report || result.analysisText" class="report-text">
      <h4>分析报告</h4>
      <p>{{ result.reportText || result.report || result.analysisText }}</p>
    </div>

    <div v-if="result.recommendations || result.suggestions" class="report-suggestions">
      <h4>建议</h4>
      <p>{{ result.recommendations || result.suggestions }}</p>
    </div>

    <div v-if="result.recommendedArticles && result.recommendedArticles.length" class="report-articles">
      <h4>推荐阅读</h4>
      <div v-for="a in result.recommendedArticles" :key="a.id" class="rec-article">
        <span>{{ a.title }}</span>
        <el-tag size="small" effect="plain">{{ severityLabels[a.severityLevel] || a.severityLevel }}</el-tag>
      </div>
    </div>

    <div class="report-time">
      测评时间：{{ formatTime(result.createdAt || result.assessmentDate) }}
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import ScoreGauge from './ScoreGauge.vue'
import { severityLabels } from '@/utils/constants'

const props = defineProps({
  result: { type: Object, required: true }
})

const levelColor = computed(() => {
  const code = props.result.severityLevel || ''
  if (code === 'SEVERE') return '#F56C6C'
  if (code === 'MODERATE' || code === 'MILD') return '#E6A23C'
  return '#67C23A'
})

function formatTime(t) {
  if (!t) return '-'
  return new Date(t).toLocaleString()
}
</script>

<style lang="scss" scoped>
.report-view {
  padding: 8px;
}
.report-header {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 24px;
  .report-summary {
    h3 { font-size: 18px; margin-bottom: 8px; }
    .result-level { font-size: 24px; font-weight: 700; margin-bottom: 6px; }
    .result-score { font-size: 14px; color: #606266; }
  }
}
.report-text, .report-suggestions, .report-articles {
  margin-bottom: 20px;
  h4 { font-size: 15px; margin-bottom: 8px; color: #303133; }
  p { font-size: 14px; color: #606266; line-height: 1.8; }
}
.rec-article {
  display: flex; align-items: center; justify-content: space-between;
  padding: 8px 12px; background: #f5f7fa; border-radius: 6px;
  margin-bottom: 6px; font-size: 13px;
}
.report-time {
  font-size: 12px; color: #c0c4cc; text-align: right;
}
</style>
