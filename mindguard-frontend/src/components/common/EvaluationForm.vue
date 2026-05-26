<template>
  <div class="evaluation-form">
    <el-form :model="form" label-width="80px">
      <el-form-item label="评分">
        <el-rate v-model="form.rating" :max="5" show-score :texts="rateTexts" />
      </el-form-item>
      <el-form-item label="评语">
        <el-input
          v-model="form.comment"
          type="textarea"
          :rows="3"
          placeholder="分享你的咨询体验..."
          maxlength="500"
          show-word-limit
        />
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'

const form = reactive({
  rating: 5,
  comment: ''
})

const rateTexts = ['非常不满意', '不满意', '一般', '满意', '非常满意']

function getData() {
  return { ...form }
}

function reset() {
  form.rating = 5
  form.comment = ''
}

defineExpose({ getData, reset })
</script>

<style lang="scss" scoped>
.evaluation-form {
  padding: 8px 0;
}
</style>
