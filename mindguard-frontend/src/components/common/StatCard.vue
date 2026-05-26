<template>
  <div class="stat-card" :style="{ cursor: clickable ? 'pointer' : 'default' }" @click="$emit('click')">
    <div class="stat-icon" :style="{ background: color + '1a', color }">
      <el-icon :size="24"><component :is="icon" /></el-icon>
    </div>
    <div class="stat-info">
      <div class="stat-value">{{ value }}</div>
      <div class="stat-label">{{ label }}</div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  icon: { type: String, default: 'Odometer' },
  color: { type: String, default: '#67C23A' },
  value: { type: [Number, String], default: 0 },
  label: { type: String, default: '' },
  clickable: { type: Boolean, default: false }
})

defineEmits(['click'])
</script>

<style lang="scss" scoped>
.stat-card {
  display: flex;
  align-items: center;
  gap: 14px;
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,.04);
  transition: all .2s;
  &:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,.08); }
}
.stat-icon {
  width: 52px; height: 52px; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  flex-shrink: 0;
}
.stat-value { font-size: 28px; font-weight: 700; color: #303133; line-height: 1.2; }
.stat-label { font-size: 13px; color: #909399; margin-top: 2px; }
</style>
