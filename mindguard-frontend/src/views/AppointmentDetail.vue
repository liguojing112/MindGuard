<template>
  <div class="page-container">
    <div class="page-header">
      <h2>预约详情</h2>
      <el-button @click="$router.back()">返回列表</el-button>
    </div>

    <div v-if="loading" v-loading="true" style="min-height:300px" />

    <template v-else-if="appointment">
      <div class="card-wrapper mb-16">
        <h4>基本信息</h4>
        <el-descriptions :column="2" class="mt-16">
          <el-descriptions-item label="学生">{{ appointment.studentName || appointment.realName || '匿名' }}</el-descriptions-item>
          <el-descriptions-item label="状态"><StatusTag :status="appointment.status" /></el-descriptions-item>
          <el-descriptions-item label="日期">{{ appointment.appointmentDate }}</el-descriptions-item>
          <el-descriptions-item label="时段">{{ appointment.timeSlot }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ appointment.issueType }}</el-descriptions-item>
          <el-descriptions-item label="匿名"><el-tag v-if="appointment.isAnonymous" size="small" type="info">是</el-tag><span v-else>否</span></el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- Actions: Approve / Reject -->
      <div v-if="appointment.status === 'PENDING'" class="card-wrapper mb-16">
        <h4>审核操作</h4>
        <div class="mt-16">
          <el-button type="success" @click="handleApprove">通过预约</el-button>
          <el-button type="danger" @click="rejectVisible = true">拒绝预约</el-button>
        </div>
      </div>

      <!-- Action: Start -->
      <div v-if="appointment.status === 'APPROVED'" class="card-wrapper mb-16">
        <h4>开始咨询</h4>
        <el-button type="warning" class="mt-16" @click="handleStart">开始咨询</el-button>
      </div>

      <!-- Action: Complete -->
      <div v-if="appointment.status === 'IN_PROGRESS'" class="card-wrapper mb-16">
        <h4>完成咨询记录</h4>
        <ConsultationForm ref="consultFormRef" class="mt-16" />
        <el-button type="success" class="mt-16" :loading="completing" @click="handleComplete">提交咨询记录</el-button>
      </div>

      <!-- Evaluation -->
      <div v-if="appointment.evaluated && appointment.rating" class="card-wrapper mb-16">
        <h4>学生评价</h4>
        <div class="mt-16">
          <el-rate :model-value="appointment.rating" disabled show-score />
          <p v-if="appointment.comment" class="mt-8">{{ appointment.comment }}</p>
          <p v-else class="mt-8 text-secondary">无文字评价</p>
        </div>
      </div>

      <!-- Consultation Record -->
      <div v-if="appointment.status === 'COMPLETED' || appointment.status === 'ARCHIVED'" class="card-wrapper mb-16">
        <h4>咨询记录</h4>
        <div v-if="appointment.contentSummary || appointment.diagnosis || appointment.suggestions" class="mt-16">
          <div v-if="appointment.contentSummary">
            <strong>内容摘要：</strong>
            <p class="mt-4">{{ appointment.contentSummary }}</p>
          </div>
          <div v-if="appointment.diagnosis" class="mt-8">
            <strong>初步诊断：</strong>
            <p class="mt-4">{{ appointment.diagnosis }}</p>
          </div>
          <div v-if="appointment.suggestions" class="mt-8">
            <strong>跟进建议：</strong>
            <p class="mt-4">{{ appointment.suggestions }}</p>
          </div>
        </div>
        <EmptyState v-else message="暂无咨询记录" :image-size="60" />
      </div>
    </template>

    <!-- Reject Dialog -->
    <el-dialog v-model="rejectVisible" title="拒绝预约" width="450px">
      <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请填写拒绝原因..." />
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" :loading="actionLoading" @click="handleReject">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getAppointmentDetail, approveAppointment, rejectAppointment, startAppointment, completeAppointment } from '@/api/appointment'
import StatusTag from '@/components/common/StatusTag.vue'
import ConsultationForm from '@/components/common/ConsultationForm.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const route = useRoute()
const loading = ref(false)
const appointment = ref(null)
const actionLoading = ref(false)
const completing = ref(false)

const rejectVisible = ref(false)
const rejectReason = ref('')
const consultFormRef = ref(null)

onMounted(async () => {
  loading.value = true
  try {
    const res = await getAppointmentDetail(route.params.id)
    appointment.value = res.data
  } catch { /* */ }
  finally { loading.value = false }
})

async function refresh() {
  try {
    const res = await getAppointmentDetail(route.params.id)
    appointment.value = res.data
  } catch { /* */ }
}

async function handleApprove() {
  actionLoading.value = true
  try {
    await approveAppointment(route.params.id)
    ElMessage.success('已通过')
    await refresh()
  } catch { /* */ }
  finally { actionLoading.value = false }
}

async function handleReject() {
  if (!rejectReason.value.trim()) { ElMessage.warning('请填写拒绝原因'); return }
  actionLoading.value = true
  try {
    await rejectAppointment(route.params.id, rejectReason.value)
    ElMessage.success('已拒绝')
    rejectVisible.value = false
    await refresh()
  } catch { /* */ }
  finally { actionLoading.value = false }
}

async function handleStart() {
  actionLoading.value = true
  try {
    await startAppointment(route.params.id)
    ElMessage.success('咨询已开始')
    await refresh()
  } catch { /* */ }
  finally { actionLoading.value = false }
}

async function handleComplete() {
  const data = consultFormRef.value?.getData()
  if (!data || !data.contentSummary) { ElMessage.warning('请至少填写内容摘要'); return }
  completing.value = true
  try {
    await completeAppointment(route.params.id, data)
    ElMessage.success('咨询记录已提交')
    consultFormRef.value?.reset()
    await refresh()
  } catch { /* */ }
  finally { completing.value = false }
}
</script>
