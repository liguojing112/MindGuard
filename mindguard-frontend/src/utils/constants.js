export const statusLabels = {
  PENDING: '待处理',
  NOTICED: '已关注',
  COMMUNICATED: '已沟通',
  RESOLVED: '已解决',
  APPROVED: '已通过',
  REJECTED: '已拒绝',
  IN_PROGRESS: '咨询中',
  COMPLETED: '已完成',
  ARCHIVED: '已归档'
}

export const statusColors = {
  PENDING: 'warning',
  NOTICED: 'info',
  COMMUNICATED: '',
  RESOLVED: 'success',
  APPROVED: 'success',
  REJECTED: 'danger',
  IN_PROGRESS: '',
  COMPLETED: 'success',
  ARCHIVED: 'info'
}

export const severityLabels = {
  LOW: '低',
  MEDIUM: '中',
  HIGH: '高',
  CRITICAL: '极高',
  NORMAL: '正常',
  MILD: '轻度',
  MODERATE: '中度',
  SEVERE: '重度'
}

export const severityColors = {
  LOW: 'info',
  MEDIUM: 'warning',
  HIGH: 'danger',
  CRITICAL: 'danger',
  NORMAL: 'success',
  MILD: 'info',
  MODERATE: 'warning',
  SEVERE: 'danger'
}

export const emotionLabels = {
  HAPPY: '开心',
  CALM: '平静',
  SAD: '难过',
  ANXIOUS: '焦虑',
  ANGRY: '生气',
  EXCITED: '兴奋',
  TIRED: '疲惫',
  GRATEFUL: '感恩',
  WORRIED: '担忧',
  HOPEFUL: '期待',
  CONFUSED: '困惑',
  LONELY: '孤独',
  PROUD: '自豪',
  FEARFUL: '害怕',
  NEUTRAL: '一般',
  OPTIMISTIC: '乐观'
}

export const moodEmojis = [
  { emoji: '😊', label: '开心', value: 'HAPPY' },
  { emoji: '😌', label: '平静', value: 'CALM' },
  { emoji: '😢', label: '难过', value: 'SAD' },
  { emoji: '😰', label: '焦虑', value: 'ANXIOUS' },
  { emoji: '😠', label: '生气', value: 'ANGRY' },
  { emoji: '🤩', label: '兴奋', value: 'EXCITED' },
  { emoji: '😴', label: '疲惫', value: 'TIRED' },
  { emoji: '🥰', label: '感恩', value: 'GRATEFUL' },
  { emoji: '😟', label: '担忧', value: 'WORRIED' },
  { emoji: '🌟', label: '期待', value: 'HOPEFUL' },
  { emoji: '😕', label: '困惑', value: 'CONFUSED' },
  { emoji: '🥺', label: '孤独', value: 'LONELY' },
  { emoji: '😎', label: '自豪', value: 'PROUD' },
  { emoji: '😨', label: '害怕', value: 'FEARFUL' },
  { emoji: '😐', label: '一般', value: 'NEUTRAL' },
  { emoji: '🤗', label: '乐观', value: 'OPTIMISTIC' }
]

export const issueTypes = [
  '学业压力',
  '人际关系',
  '情感困扰',
  '家庭问题',
  '职业规划',
  '自我认知',
  '情绪管理',
  '其他'
]

export const alertStatusFlow = ['PENDING', 'NOTICED', 'COMMUNICATED', 'RESOLVED']
