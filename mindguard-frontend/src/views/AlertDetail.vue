<template>
  <div class="page-container">
    <div class="page-header">
      <h2>预警详情</h2>
      <el-button @click="$router.back()">返回列表</el-button>
    </div>

    <div v-if="loading" v-loading="true" style="min-height:300px" />

    <template v-else-if="alert">
      <div class="card-wrapper mb-16">
        <h4>倾诉内容</h4>
        <div class="post-info">
          <span>发布者：{{ alert.studentName || alert.realName || '匿名' }}</span>
          <span class="ml-8">时间：{{ formatTime(alert.postCreatedAt || alert.createdAt) }}</span>
        </div>
        <p class="post-content mt-16">{{ alert.content }}</p>
      </div>

      <div class="card-wrapper mb-16">
        <h4>AI 分析结果</h4>
        <div class="analysis-result mt-16">
          <AnalysisBadge :label="alert.analysisLabel || alert.analysisResult" />
          <p v-if="alert.analysisReport || alert.analysisDetail" class="mt-8">{{ alert.analysisReport || alert.analysisDetail }}</p>
        </div>
      </div>

      <div class="card-wrapper mb-16">
        <h4>状态流转</h4>
        <div class="status-flow mt-16">
          <el-steps :active="currentStepIndex" finish-status="success" align-center>
            <el-step
              v-for="s in statusFlow"
              :key="s"
              :title="statusLabels[s]"
              :description="s === alert.status && alert.statusRemarks ? alert.statusRemarks : ''"
            />
          </el-steps>
        </div>
      </div>

      <div v-if="alert.status !== 'RESOLVED'" class="card-wrapper">
        <h4>状态更新</h4>
        <el-form class="mt-16" label-width="80px">
          <el-form-item label="备注">
            <el-input v-model="remarks" type="textarea" :rows="3" placeholder="填写备注信息..." />
          </el-form-item>
          <el-form-item>
            <el-button
              v-for="next in nextStatuses"
              :key="next.status"
              :type="next.type"
              :loading="updating === next.status"
              @click="handleUpdateStatus(next.status)"
            >
              {{ next.label }}
            </el-button>
          </el-form-item>
        </el-form>
      </div>
    </template>

    <el-result v-else icon="error" title="预警不存在或已删除" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAlertDetail, updateAlertStatus } from '@/api/alert'
import { formatTime } from '@/utils/helpers'
import { statusLabels, alertStatusFlow } from '@/utils/constants'
import AnalysisBadge from '@/components/common/AnalysisBadge.vue'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const alert = ref(null)
const remarks = ref('')
const updating = ref('')

const statusFlow = alertStatusFlow

const currentStepIndex = computed(() => {
  if (!alert.value) return 0
  return statusFlow.indexOf(alert.value.status)
})

const nextStatuses = computed(() => {
  if (!alert.value) return []
  const currentIdx = statusFlow.indexOf(alert.value.status)
  if (currentIdx < 0 || currentIdx >= statusFlow.length - 1) return []
  const next = statusFlow[currentIdx + 1]
  const labelMap = {
    NOTICED: { status: 'NOTICED', label: '标记为已关注', type: 'primary' },
    COMMUNICATED: { status: 'COMMUNICATED', label: '标记为已沟通', type: 'warning' },
    RESOLVED: { status: 'RESOLVED', label: '标记为已解决', type: 'success' }
  }
  return [labelMap[next]].filter(Boolean)
})

onMounted(async () => {
  loading.value = true
  try {
    const res = await getAlertDetail(route.params.id)
    alert.value = res.data
  } catch { /* */ }
  finally { loading.value = false }
})

async function handleUpdateStatus(status) {
  updating.value = status
  try {
    await updateAlertStatus(route.params.id, { status, remarks: remarks.value })
    ElMessage.success('状态更新成功')
    remarks.value = ''
    const res = await getAlertDetail(route.params.id)
    alert.value = res.data
  } catch { /* */ }
  finally { updating.value = '' }
}
</script>

<style lang="scss" scoped>
.post-info { font-size: 13px; color: #909399; }
.post-content { font-size: 15px; line-height: 1.8; white-space: pre-wrap; }
.analysis-result p { font-size: 14px; color: #606266; line-height: 1.8; }
.status-flow { padding: 16px 0; }
</style>
