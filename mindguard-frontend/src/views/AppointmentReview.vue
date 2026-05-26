<template>
  <div class="page-container">
    <div class="page-header">
      <h2>预约管理</h2>
    </div>

    <el-tabs v-model="activeTab" @tab-change="onTabChange">
      <el-tab-pane label="待审核" name="PENDING" />
      <el-tab-pane label="已通过" name="APPROVED" />
      <el-tab-pane label="已拒绝" name="REJECTED" />
      <el-tab-pane label="已完成" name="COMPLETED" />
      <el-tab-pane label="全部" name="" />
    </el-tabs>

    <div v-loading="loading">
      <template v-if="appointments.length">
        <div v-for="apt in appointments" :key="apt.id" class="appointment-card">
          <div class="flex-between">
            <div>
              <strong>{{ apt.studentName || apt.realName || '学生' }}</strong>
              <el-tag v-if="apt.isAnonymous" size="small" type="info" effect="plain" class="ml-8">匿名</el-tag>
            </div>
            <StatusTag :status="apt.status" />
          </div>
          <div class="apt-info mt-8">
            <span>日期：{{ apt.appointmentDate }} {{ apt.timeSlot }}</span>
            <span class="ml-16">类型：{{ apt.issueType }}</span>
          </div>
          <div v-if="apt.status === 'PENDING'" class="apt-actions mt-8">
            <el-button size="small" type="success" @click="handleApprove(apt)">通过</el-button>
            <el-button size="small" type="danger" @click="openReject(apt)">拒绝</el-button>
          </div>
          <div class="apt-actions mt-8">
            <el-button link type="primary" size="small" @click="$router.push(`/counselor/appointments/${apt.id}`)">查看详情</el-button>
            <el-button v-if="apt.studentId" link type="primary" size="small" @click="$router.push(`/counselor/students/${apt.studentId}`)">学生档案</el-button>
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
      <EmptyState v-else-if="!loading" message="暂无预约记录" />
    </div>

    <!-- Reject Dialog -->
    <el-dialog v-model="rejectVisible" title="拒绝预约" width="450px">
      <el-input v-model="rejectReason" type="textarea" :rows="3" placeholder="请填写拒绝原因..." />
      <template #footer>
        <el-button @click="rejectVisible = false">取消</el-button>
        <el-button type="danger" :loading="rejecting" @click="handleReject">确认拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getCounselorAppointments, approveAppointment, rejectAppointment } from '@/api/appointment'
import StatusTag from '@/components/common/StatusTag.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const loading = ref(false)
const appointments = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const activeTab = ref('PENDING')

const rejectVisible = ref(false)
const rejectReason = ref('')
const rejecting = ref(false)
const currentReject = ref(null)

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (activeTab.value) params.status = activeTab.value
    const res = await getCounselorAppointments(params)
    appointments.value = res.data.records || []
    total.value = res.data.total || 0
  } catch { /* */ }
  finally { loading.value = false }
}

function onTabChange() {
  page.value = 1
  fetchData()
}

async function handleApprove(apt) {
  try {
    await approveAppointment(apt.id)
    ElMessage.success('已通过')
    await fetchData()
  } catch { /* */ }
}

function openReject(apt) {
  currentReject.value = apt
  rejectReason.value = ''
  rejectVisible.value = true
}

async function handleReject() {
  if (!rejectReason.value.trim()) { ElMessage.warning('请填写拒绝原因'); return }
  rejecting.value = true
  try {
    await rejectAppointment(currentReject.value.id, rejectReason.value)
    ElMessage.success('已拒绝')
    rejectVisible.value = false
    await fetchData()
  } catch { /* */ }
  finally { rejecting.value = false }
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
.apt-info { font-size: 14px; color: #606266; }
.pagination-wrap { display: flex; justify-content: center; }
</style>
