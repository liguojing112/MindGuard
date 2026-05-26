<template>
  <div class="question-item">
    <div class="question-header">
      <span class="question-num">{{ index + 1 }}</span>
      <span class="question-text">{{ question.questionText || question.content }}</span>
    </div>
    <el-radio-group v-model="selectedOption" class="option-group" @change="handleChange">
      <el-radio
        v-for="opt in question.options"
        :key="opt.id"
        :value="opt.id"
        class="option-radio"
        border
      >
        {{ opt.optionText || opt.content }}
      </el-radio>
    </el-radio-group>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  question: { type: Object, required: true },
  index: { type: Number, required: true },
  modelValue: { type: Number, default: null }
})

const emit = defineEmits(['update:modelValue', 'change'])

const selectedOption = ref(props.modelValue)

watch(() => props.modelValue, (val) => {
  selectedOption.value = val
})

function handleChange(val) {
  emit('update:modelValue', val)
  emit('change', { questionId: props.question.id, optionId: val })
}
</script>

<style lang="scss" scoped>
.question-item {
  background: #fff;
  border-radius: 10px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,.04);
}
.question-header {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
  .question-num {
    width: 28px; height: 28px; border-radius: 50%;
    background: #f0f9eb; color: #67C23A;
    display: flex; align-items: center; justify-content: center;
    font-size: 14px; font-weight: 600; flex-shrink: 0;
  }
  .question-text { font-size: 15px; color: #303133; line-height: 1.6; }
}
.option-group {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.option-radio {
  margin-right: 0 !important;
  padding: 12px 16px;
  border-radius: 8px;
  width: 100%;
  height: auto;
  &.is-checked { border-color: #67C23A; background: #f0f9eb; }
}
</style>
