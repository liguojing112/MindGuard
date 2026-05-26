<template>
  <div class="scale-card">
    <div class="scale-icon">📋</div>
    <div class="scale-info">
      <h4>{{ scale.name }}</h4>
      <p>{{ scale.description }}</p>
      <div class="scale-meta">
        <span>{{ scale.totalQuestions || scale.questionCount || scale.questions?.length || 0 }} 题</span>
        <span class="dot">·</span>
        <span>约 {{ Math.round((scale.totalQuestions || scale.questionCount || scale.questions?.length || 10) * 0.5) }} 分钟</span>
      </div>
    </div>
    <el-button type="primary" @click="$emit('start', scale)">开始测评</el-button>
  </div>
</template>

<script setup>
defineProps({
  scale: { type: Object, required: true }
})

defineEmits(['start'])
</script>

<style lang="scss" scoped>
.scale-card {
  display: flex;
  align-items: center;
  gap: 16px;
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,.04);
  transition: all .2s;
  &:hover { box-shadow: 0 4px 12px rgba(0,0,0,.08); }
}
.scale-icon { font-size: 36px; flex-shrink: 0; }
.scale-info {
  flex: 1;
  h4 { font-size: 16px; margin-bottom: 4px; }
  p { font-size: 13px; color: #909399; margin-bottom: 6px; }
  .scale-meta { font-size: 12px; color: #c0c4cc; .dot { margin: 0 4px; } }
}
</style>
