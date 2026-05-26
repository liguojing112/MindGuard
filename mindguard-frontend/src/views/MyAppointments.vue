<template>
  <div class="page-container">
    <div class="page-header">
      <h2>我的预约</h2>
    </div>

    <div v-loading="loading">
      <template v-if="appointments.length">
        <div v-for="apt in appointments" :key="apt.id" class="appointment-card">
          <div class="apt-header flex-between">
            <div>
              <strong>{{ apt.counselorName || '咨询师' }}</strong>
              <span class="apt-type ml-8">{{ apt.issueType }}</span>
            </div>
            <StatusTag :status="apt.status" />
          </div>
          <div class="apt-body">
            <span>日期：{{ apt.appointmentDate }} {{ apt.timeSlot }}</span>
            <el-tag v-if="apt.isAnonymous" size="small" type="info" effect="plain" class="ml-8">匿名</el-tag>
          </div>

          <!-- Timeline for status transitions -->
          <div v-if="apt.history && apt.history.length" class="mt-8">
            <AppointmentTimeline :history="apt.history" />
          </div>

          <!-- Evaluation for completed -->
          <div v-if="apt.status === 'COMPLETED' && !apt.evaluated" class="apt-actions mt-8">
            <el-button type="warning" size="small" @click="openEvaluation(apt)">评价咨询</el-button>
          </div>
          <div v-if="apt.evaluated || apt.rating" class="apt-eval mt-8">
            <el-rate :model-value="apt.rating" disabled show-score text-color="#ff9900" />
            <p v-if="apt.comment" class="eval-comment mt-4">{{ apt.comment }}</p>
          </div>

          <!-- Consultation record for completed -->
          <div v-if="(apt.status === 'COMPLETED' || apt.status === 'ARCHIVED') && (apt.contentSummary || apt.diagnosis || apt.suggestions)" class="consult-record mt-12">
            <el-divider />
            <h5>咨询记录</h5>
            <div v-if="apt.contentSummary" class="mt-4"><strong>内容摘要：</strong>{{ apt.contentSummary }}</div>
            <div v-if="apt.diagnosis" class="mt-4"><strong>初步诊断：</strong>{{ apt.diagnosis }}</div>
            <div v-if="apt.suggestions" class="mt-4"><strong>跟进建议：</strong>{{ apt.suggestions }}</div>
          </div>
        </div>
        <div class="pagination-wrap mt-16">
          <el-pagination
            v-model:current-page="page"
            :page-size="size"
            :total="total"
            layout="prev, pager, next"
            @current-change="fetchData"
          />
        </div>
      </template>
      <EmptyState v-else-if="!loading" message="还没有预约记录">
        <el-button type="primary" @click="$router.push('/student/counselors')">去预约</el-button>
      </EmptyState>
    </div>

    <!-- Evaluation Dialog -->
    <el-dialog v-model="evalVisible" title="评价咨询" width="500px">
      <EvaluationForm ref="evalFormRef" />
      <template #footer>
        <el-button @click="evalVisible = false">取消</el-button>
        <el-button type="primary" :loading="evalSubmitting" @click="submitEvaluation">提交评价</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyAppointments, evaluateAppointment } from '@/api/appointment'
import StatusTag from '@/components/common/StatusTag.vue'
import AppointmentTimeline from '@/components/common/AppointmentTimeline.vue'
import EvaluationForm from '@/components/common/EvaluationForm.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const loading = ref(false)
const appointments = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const evalVisible = ref(false)
const evalSubmitting = ref(false)
const currentApt = ref(null)
const evalFormRef = ref(null)

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getMyAppointments({ page: page.value, size: size.value })
    appointments.value = res.data.records || []
    total.value = res.data.total || 0
  } catch { /* */ }
  finally { loading.value = false }
}

function openEvaluation(apt) {
  currentApt.value = apt
  evalVisible.value = true
}

async function submitEvaluation() {
  const data = evalFormRef.value?.getData()
  if (!data) return
  evalSubmitting.value = true
  try {
    await evaluateAppointment(currentApt.value.id, data)
    ElMessage.success('评价成功')
    evalVisible.value = false
    evalFormRef.value?.reset()
    await fetchData()
  } catch { /* */ }
  finally { evalSubmitting.value = false }
}
</script>

<style lang="scss" scoped>
.appointment-card {
  background: #fff;
  border-radius: 10px;
  padding: 18px 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,.04);
  margin-bottom: 12px;
}
.apt-header strong { font-size: 15px; }
.apt-type { font-size: 13px; color: #909399; }
.apt-body { margin-top: 8px; font-size: 14px; color: #606266; }
.apt-eval { padding-top: 8px; }
.eval-comment { font-size: 13px; color: #606266; line-height: 1.5; }
.consult-record { font-size: 14px; color: #303133; line-height: 1.8; }
.consult-record h5 { font-size: 14px; color: #303133; margin-bottom: 4px; }
.pagination-wrap { display: flex; justify-content: center; }
</style>
