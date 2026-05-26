<template>
  <el-form :model="form" label-width="100px">
    <el-form-item label="内容摘要">
      <el-input
        v-model="form.contentSummary"
        type="textarea"
        :rows="4"
        placeholder="记录本次咨询的主要内容..."
        maxlength="2000"
        show-word-limit
      />
    </el-form-item>
    <el-form-item label="初步诊断">
      <el-input
        v-model="form.diagnosis"
        type="textarea"
        :rows="3"
        placeholder="初步诊断结论..."
        maxlength="1000"
        show-word-limit
      />
    </el-form-item>
    <el-form-item label="跟进建议">
      <el-input
        v-model="form.suggestions"
        type="textarea"
        :rows="3"
        placeholder="后续跟进建议..."
        maxlength="1000"
        show-word-limit
      />
    </el-form-item>
  </el-form>
</template>

<script setup>
import { reactive } from 'vue'

const form = reactive({
  contentSummary: '',
  diagnosis: '',
  suggestions: ''
})

function getData() {
  return { ...form }
}

function reset() {
  form.contentSummary = ''
  form.diagnosis = ''
  form.suggestions = ''
}

defineExpose({ getData, reset })
</script>
