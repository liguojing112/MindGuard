<template>
  <div class="counselor-card">
    <div class="counselor-avatar">
      <SurnameAvatar :name="counselor.realName || counselor.name" :size="64" />
    </div>
    <div class="counselor-info">
      <h4>{{ counselor.realName || counselor.name }} <el-tag size="small" effect="plain">{{ counselor.title || counselor.level }}</el-tag></h4>
      <p class="specialty">{{ counselor.specialty || counselor.expertise }}</p>
      <p class="intro">{{ counselor.introduction || counselor.bio }}</p>
    </div>
    <el-button type="primary" @click="$emit('book', counselor)">预约咨询</el-button>
  </div>
</template>

<script setup>
import SurnameAvatar from '@/components/common/SurnameAvatar.vue'

defineProps({
  counselor: { type: Object, required: true }
})

defineEmits(['book'])
</script>

<style lang="scss" scoped>
.counselor-card {
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
.counselor-avatar { flex-shrink: 0; }
.counselor-info {
  flex: 1;
  h4 { font-size: 16px; margin-bottom: 4px; display: flex; align-items: center; gap: 8px; }
  .specialty { font-size: 13px; color: #67C23A; margin-bottom: 4px; }
  .intro { font-size: 13px; color: #909399; line-height: 1.5; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
}
</style>
