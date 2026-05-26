<template>
  <div class="emoji-picker">
    <div
      v-for="item in moodEmojis"
      :key="item.value"
      class="emoji-item"
      :class="{ active: modelValue === item.emoji }"
      @click="$emit('update:modelValue', item.emoji)"
    >
      <span class="emoji-icon">{{ item.emoji }}</span>
      <span class="emoji-label">{{ item.label }}</span>
    </div>
  </div>
</template>

<script setup>
import { moodEmojis } from '@/utils/constants'

defineProps({
  modelValue: { type: String, default: '' }
})

defineEmits(['update:modelValue'])
</script>

<style lang="scss" scoped>
.emoji-picker {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.emoji-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all .2s;
  min-width: 64px;
  &:hover { background: #f0f9eb; border-color: #d2edc4; }
  &.active { background: #f0f9eb; border-color: #67C23A; }
  .emoji-icon { font-size: 22px; }
  .emoji-label { font-size: 11px; color: #909399; }
  &.active .emoji-label { color: #67C23A; }
}
</style>
