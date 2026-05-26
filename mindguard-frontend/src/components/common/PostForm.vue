<template>
  <div class="post-form">
    <el-input
      v-model="content"
      type="textarea"
      :rows="4"
      placeholder="在这里写下你的心情...（真诚表达，我们在这里倾听）"
      maxlength="2000"
      show-word-limit
    />
    <div class="form-options">
      <div class="option-left">
        <span class="option-label">今日心情：</span>
        <MoodEmojiPicker v-model="moodEmoji" />
      </div>
      <div class="option-right">
        <el-switch v-model="isAnonymous" active-text="匿名" />
        <el-button type="primary" :loading="submitting" @click="handleSubmit" :disabled="!content.trim()">
          发布倾诉
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import MoodEmojiPicker from './MoodEmojiPicker.vue'

const emit = defineEmits(['submit'])

const props = defineProps({
  submitting: { type: Boolean, default: false }
})

const content = ref('')
const moodEmoji = ref('😊')
const isAnonymous = ref(false)

function handleSubmit() {
  if (!content.value.trim()) return
  emit('submit', {
    content: content.value.trim(),
    moodEmoji: moodEmoji.value,
    isAnonymous: isAnonymous.value
  })
}

function reset() {
  content.value = ''
  moodEmoji.value = '😊'
  isAnonymous.value = false
}

defineExpose({ reset })
</script>

<style lang="scss" scoped>
.post-form {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,.04);
}
.form-options {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  margin-top: 14px;
  flex-wrap: wrap;
  gap: 12px;
}
.option-left {
  display: flex;
  flex-direction: column;
  gap: 6px;
  .option-label { font-size: 13px; color: #909399; }
}
.option-right {
  display: flex;
  align-items: center;
  gap: 12px;
  align-self: flex-end;
}
</style>
