<template>
  <div class="page-container">
    <div class="page-header">
      <h2>系统设置</h2>
    </div>

    <div class="card-wrapper">
      <h4>AI 情绪分析配置</h4>
      <p class="text-secondary mt-8">配置 AI 服务模式：Mock 模拟无需任何外部依赖，DeepSeek 需填入有效 API Key。</p>
      <el-form :model="form" label-width="100px" class="mt-16">
        <el-form-item label="AI 模式">
          <el-switch
            v-model="form.mock"
            active-text="Mock 模拟"
            inactive-text="DeepSeek API"
          />
        </el-form-item>
        <template v-if="!form.mock">
          <el-form-item label="API Key">
            <el-input
              v-model="form.apiKey"
              type="password"
              show-password
              placeholder="请输入 DeepSeek API Key（sk-...）"
            />
          </el-form-item>
          <el-form-item label="API 地址">
            <el-input v-model="form.apiUrl" placeholder="https://api.deepseek.com/v1/chat/completions" />
          </el-form-item>
        </template>
        <el-form-item>
          <el-button type="primary" :loading="saving" @click="handleSave">保存配置</el-button>
          <span v-if="saved" class="ml-8 saved-tip">保存成功，下次分析即刻生效</span>
        </el-form-item>
      </el-form>
    </div>

    <div class="card-wrapper mt-16">
      <h4>当前状态</h4>
      <el-descriptions :column="1" class="mt-16">
        <el-descriptions-item label="运行模式">
          <el-tag :type="form.mock ? 'success' : 'warning'">{{ form.mock ? 'Mock 模拟' : 'DeepSeek API' }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="API Key">
          {{ form.apiKey || '未配置' }}
        </el-descriptions-item>
      </el-descriptions>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAISettings, saveAISettings } from '@/api/settings'

const saving = ref(false)
const saved = ref(false)

const form = reactive({
  mock: true,
  apiKey: '',
  apiUrl: 'https://api.deepseek.com/v1/chat/completions'
})

onMounted(async () => {
  try {
    const res = await getAISettings()
    if (res.data) {
      form.mock = res.data.mock
      form.apiKey = res.data.apiKey || ''
      form.apiUrl = res.data.apiUrl || 'https://api.deepseek.com/v1/chat/completions'
    }
  } catch { /* */ }
})

async function handleSave() {
  if (!form.mock && !form.apiKey) {
    ElMessage.warning('使用 DeepSeek 模式需要填写 API Key')
    return
  }
  saving.value = true
  saved.value = false
  try {
    await saveAISettings({
      mock: form.mock,
      apiKey: form.apiKey,
      apiUrl: form.apiUrl
    })
    ElMessage.success('AI 配置保存成功')
    saved.value = true
    setTimeout(() => { saved.value = false }, 3000)
  } catch { /* */ }
  finally { saving.value = false }
}
</script>

<style lang="scss" scoped>
.text-secondary { font-size: 13px; color: #909399; }
.saved-tip { font-size: 13px; color: #67C23A; }
</style>
