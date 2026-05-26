<template>
  <div class="page-container">
    <div class="page-header">
      <h2>预约咨询</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <div v-if="counselor" class="counselor-brief card-wrapper mb-16">
      <div class="flex-between">
        <div>
          <h3>{{ counselor.realName || counselor.name }}</h3>
          <p>{{ counselor.title }} | {{ counselor.specialty || counselor.expertise }}</p>
        </div>
        <SurnameAvatar :name="counselor.realName || counselor.name" :size="56" />
      </div>
    </div>

    <div class="card-wrapper">
      <el-form :model="form" label-width="100px">
        <el-form-item label="预约日期" required>
          <el-date-picker
            v-model="form.appointmentDate"
            type="date"
            placeholder="选择日期"
            :disabled-date="disabledDate"
            @change="onDateChange"
          />
        </el-form-item>
        <el-form-item label="时间段" required>
          <TimeSlotPicker
            v-model="form.timeSlot"
            :slots="slotOptions"
            :booked-slots="bookedSlots"
          />
        </el-form-item>
        <el-form-item label="咨询类型" required>
          <el-select v-model="form.issueType" placeholder="选择类型" style="width:100%">
            <el-option v-for="t in issueTypes" :key="t" :label="t" :value="t" />
          </el-select>
        </el-form-item>
        <el-form-item label="匿名预约">
          <el-switch v-model="form.isAnonymous" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="submitting" @click="handleSubmit" :disabled="!canSubmit">
            {{ submitting ? '提交中...' : '确认预约' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getCounselorDetail, getSlots, createAppointment } from '@/api/appointment'
import { issueTypes } from '@/utils/constants'
import TimeSlotPicker from '@/components/common/TimeSlotPicker.vue'
import SurnameAvatar from '@/components/common/SurnameAvatar.vue'

const route = useRoute()
const router = useRouter()
const counselor = ref(null)
const slotOptions = ref([])
const bookedSlots = ref([])
const submitting = ref(false)

const form = reactive({
  appointmentDate: '',
  timeSlot: '',
  issueType: '',
  isAnonymous: false
})

const canSubmit = computed(() => form.appointmentDate && form.timeSlot && form.issueType)

const disabledDate = (time) => {
  const today = new Date()
  today.setHours(0, 0, 0, 0)
  return time.getTime() < today.getTime() || time.getTime() > today.getTime() + 14 * 86400000
}

onMounted(async () => {
  try {
    const res = await getCounselorDetail(route.params.id)
    counselor.value = res.data
  } catch { /* */ }
})

async function onDateChange(date) {
  if (!date) return
  const dateStr = typeof date === 'string' ? date : date.toISOString().slice(0, 10)
  form.timeSlot = ''
  try {
    const res = await getSlots(route.params.id, dateStr)
    slotOptions.value = res.data || []
    bookedSlots.value = []
  } catch { slotOptions.value = []; bookedSlots.value = [] }
}

async function handleSubmit() {
  if (!canSubmit.value) return
  submitting.value = true
  try {
    await createAppointment({
      counselorId: Number(route.params.id),
      appointmentDate: typeof form.appointmentDate === 'string' ? form.appointmentDate : form.appointmentDate.toISOString().slice(0, 10),
      timeSlot: form.timeSlot,
      issueType: form.issueType,
      isAnonymous: form.isAnonymous
    })
    ElMessage.success('预约提交成功，请等待审核')
    router.push('/student/appointments')
  } catch { /* */ }
  finally { submitting.value = false }
}
</script>
