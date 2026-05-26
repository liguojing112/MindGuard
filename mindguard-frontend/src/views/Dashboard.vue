<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ greeting }}，{{ userStore.userInfo?.realName || '辅导员' }}</h2>
    </div>

    <div class="stat-cards">
      <StatCard icon="WarningFilled" color="#F56C6C" :value="overview.totalAlerts" label="总预警数" clickable @click="$router.push('/counselor/alerts')" />
      <StatCard icon="Warning" color="#E6A23C" :value="overview.pendingAlerts" label="待处理" clickable @click="$router.push('/counselor/alerts')" />
      <StatCard icon="Tickets" color="#67C23A" :value="overview.todayAppointments" label="本月预约" clickable @click="$router.push('/counselor/appointments')" />
      <StatCard icon="UserFilled" color="#409EFF" :value="overview.activeStudents" label="活跃学生" />
    </div>

    <el-row :gutter="20" class="mt-16">
      <el-col :span="12">
        <div class="chart-card">
          <h4>情绪分布</h4>
          <PieChart :data="emotionDist" height="280px" />
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-card">
          <h4>预警趋势（近30天）</h4>
          <LineChart :data="crisisTrend" height="280px" />
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="mt-16">
      <el-col :span="12">
        <div class="chart-card">
          <h4>测评程度分布</h4>
          <BarChart :data="assessmentDist" height="260px" />
        </div>
      </el-col>
      <el-col :span="12">
        <div class="chart-card">
          <h4>预约统计</h4>
          <BarChart :data="appointmentStats" height="260px" />
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getOverview, getEmotionDistribution, getAssessmentDist, getAppointmentStats, getCrisisTrend } from '@/api/dashboard'
import StatCard from '@/components/common/StatCard.vue'
import LineChart from '@/components/common/LineChart.vue'
import PieChart from '@/components/common/PieChart.vue'
import BarChart from '@/components/common/BarChart.vue'

const userStore = useUserStore()

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '夜深了'
  if (h < 12) return '上午好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const overview = reactive({
  totalAlerts: 0,
  pendingAlerts: 0,
  todayAppointments: 0,
  activeStudents: 0
})

const emotionDist = ref([])
const crisisTrend = ref([])
const assessmentDist = ref([])
const appointmentStats = ref([])

onMounted(async () => {
  const [ov, emo, crisis, assess, apt] = await Promise.allSettled([
    getOverview(), getEmotionDistribution(), getCrisisTrend(30), getAssessmentDist(), getAppointmentStats()
  ])

  if (ov.status === 'fulfilled') {
    const d = ov.value.data
    Object.assign(overview, {
      totalAlerts: d.totalAlerts || 0,
      pendingAlerts: d.pendingAlerts || 0,
      todayAppointments: d.todayAppointments || 0,
      activeStudents: d.activeStudents || 0
    })
  }
  if (emo.status === 'fulfilled') emotionDist.value = emo.value.data || []
  if (crisis.status === 'fulfilled') crisisTrend.value = crisis.value.data || []
  if (assess.status === 'fulfilled') assessmentDist.value = assess.value.data || []
  if (apt.status === 'fulfilled') appointmentStats.value = apt.value.data || []
})
</script>

<style lang="scss" scoped>
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 8px;
}
.chart-card {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,.04);
  h4 { font-size: 15px; margin-bottom: 12px; color: #303133; }
}
</style>
