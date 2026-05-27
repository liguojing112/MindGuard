<template>
  <div class="post-card" :class="{ clickable: clickable }" @click="clickable && $emit('click')">
    <div class="post-header">
      <div class="post-user">
        <span class="mood-emoji">{{ getEmoji(post.moodEmoji) }}</span>
        <SurnameAvatar :name="post.isAnonymous ? '匿' : (post.realName || post.username || '用户')" :size="36" />
        <span class="post-author">{{ post.realName || post.username || (post.isAnonymous ? '匿名用户' : '用户') }}</span>
        <span class="post-time">{{ formatTime(post.createdAt) }}</span>
      </div>
      <div class="post-meta">
        <AnalysisBadge v-if="post.label" :label="post.label" />
        <el-tag v-if="post.isAnonymous" size="small" type="info" effect="plain">匿名</el-tag>
      </div>
    </div>
    <div class="post-content">{{ post.content }}</div>
    <div v-if="(post.score || post.analysis)" class="analysis-feedback mt-12">
      <el-divider />
      <div class="feedback-header">
        <span class="feedback-title">AI 情绪分析</span>
        <el-tag v-if="post.score" size="small" :type="post.score < 40 ? 'danger' : post.score < 70 ? 'warning' : 'success'">
          分数：{{ post.score }}
        </el-tag>
      </div>
      <div v-if="post.keywords && post.keywords.length" class="mt-4">
        <span class="feedback-label">关键词：</span>
        <el-tag v-for="kw in post.keywords" :key="kw" size="small" effect="plain" class="mr-4">{{ kw }}</el-tag>
      </div>
      <div v-if="post.analysis" class="mt-4 feedback-text">{{ post.analysis }}</div>
    </div>
    <div v-if="showActions && !hideActions" class="post-actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<script setup>
import AnalysisBadge from './AnalysisBadge.vue'
import SurnameAvatar from '@/components/common/SurnameAvatar.vue'
import { moodEmojis } from '@/utils/constants'

const props = defineProps({
  post: { type: Object, required: true },
  clickable: { type: Boolean, default: false },
  showActions: { type: Boolean, default: false },
  hideActions: { type: Boolean, default: false }
})

defineEmits(['click'])

const emojiMap = Object.fromEntries(moodEmojis.map(m => [m.value, m.emoji]))

function getEmoji(val) {
  if (!val) return '😐'
  return emojiMap[val.toUpperCase()] || val
}

function formatTime(t) {
  if (!t) return ''
  const d = new Date(t)
  const now = new Date()
  const diff = now - d
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 604800000) return Math.floor(diff / 86400000) + '天前'
  return d.toLocaleDateString()
}
</script>

<style lang="scss" scoped>
.post-card {
  background: #fff;
  border-radius: 10px;
  padding: 18px 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,.04);
  margin-bottom: 12px;
  &.clickable { cursor: pointer; transition: all .2s; &:hover { box-shadow: 0 4px 12px rgba(0,0,0,.08); } }
}
.post-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}
.post-user {
  display: flex;
  align-items: center;
  gap: 8px;
  .mood-emoji { font-size: 22px; }
  .post-author { font-size: 14px; font-weight: 500; }
  .post-time { font-size: 12px; color: #c0c4cc; }
}
.post-meta {
  display: flex;
  align-items: center;
  gap: 6px;
}
.post-content {
  font-size: 14px;
  color: #303133;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
}
.analysis-feedback {
  .feedback-header { display: flex; align-items: center; gap: 8px; }
  .feedback-title { font-size: 14px; font-weight: 600; color: #303133; }
  .feedback-label { font-size: 13px; color: #909399; }
  .feedback-text { font-size: 13px; color: #606266; line-height: 1.7; background: #fafafa; padding: 10px 12px; border-radius: 6px; }
}
.post-actions {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  gap: 8px;
}
</style>
