<template>
  <div class="page-container">
    <!-- Student Home -->
    <template v-if="role === 'STUDENT'">
      <div class="welcome-banner">
        <div class="banner-text">
          <h2>{{ greeting }}，{{ userStore.userInfo?.realName || '同学' }}</h2>
          <p>每一天都值得被温柔对待，你的感受很重要</p>
        </div>
        <div class="banner-emoji">🌈</div>
      </div>

      <div class="stat-cards">
        <div class="stat-card" @click="$router.push('/student/checkin')">
          <div class="stat-icon" style="background:#f0f9eb"><span>📝</span></div>
          <div class="stat-num">{{ stats.checkinCount }}</div>
          <div class="stat-label">本月打卡</div>
        </div>
        <div class="stat-card" @click="$router.push('/student/posts')">
          <div class="stat-icon" style="background:#ede9fe"><span>💬</span></div>
          <div class="stat-num">{{ stats.postCount }}</div>
          <div class="stat-label">我的倾诉</div>
        </div>
        <div class="stat-card" @click="$router.push('/student/assessments/my')">
          <div class="stat-icon" style="background:#fef0f0"><span>📊</span></div>
          <div class="stat-num">{{ stats.assessmentCount }}</div>
          <div class="stat-label">测评记录</div>
        </div>
        <div class="stat-card" @click="$router.push('/student/appointments')">
          <div class="stat-icon" style="background:#e6f7ff"><span>🤝</span></div>
          <div class="stat-num">{{ stats.appointmentCount }}</div>
          <div class="stat-label">咨询预约</div>
        </div>
      </div>

      <div class="quick-actions">
        <h3>快捷入口</h3>
        <div class="action-grid">
          <div class="action-card" @click="$router.push('/student/emotion-square')">
            <span class="action-icon">🌟</span>
            <span class="action-title">情绪广场</span>
            <span class="action-desc">看看大家的心情</span>
          </div>
          <div class="action-card" @click="$router.push('/student/checkin')">
            <span class="action-icon">📅</span>
            <span class="action-title">心情打卡</span>
            <span class="action-desc">记录今日心情</span>
          </div>
          <div class="action-card" @click="$router.push('/student/assessments')">
            <span class="action-icon">📋</span>
            <span class="action-title">心理测评</span>
            <span class="action-desc">了解心理状态</span>
          </div>
          <div class="action-card" @click="$router.push('/student/counselors')">
            <span class="action-icon">👩‍⚕️</span>
            <span class="action-title">预约咨询</span>
            <span class="action-desc">寻求专业帮助</span>
          </div>
          <div class="action-card" @click="$router.push('/student/articles')">
            <span class="action-icon">📖</span>
            <span class="action-title">心理科普</span>
            <span class="action-desc">了解更多知识</span>
          </div>
          <div class="action-card" @click="$router.push('/student/posts')">
            <span class="action-icon">✍️</span>
            <span class="action-title">写倾诉</span>
            <span class="action-desc">表达内心感受</span>
          </div>
        </div>
      </div>

      <div v-if="recommendedArticles.length" class="recommend-section">
        <h3>推荐阅读</h3>
        <div class="article-list">
          <div v-for="a in recommendedArticles" :key="a.id" class="article-item" @click="$router.push(`/student/articles/${a.id}`)">
            <span class="article-title">{{ a.title }}</span>
            <el-tag size="small" :type="severityColors[a.severityLevel]">{{ severityLabels[a.severityLevel] }}</el-tag>
          </div>
        </div>
      </div>
    </template>

    <!-- Counselor Home -->
    <template v-if="role === 'COUNSELOR'">
      <div class="page-header">
        <h2>{{ greeting }}，{{ userStore.userInfo?.realName || '辅导员' }}</h2>
      </div>

      <div class="stat-cards counselor-stats">
        <StatCard icon="Warning" color="#E6A23C" :value="overview.pendingAlerts || 0" label="待处理预警" @click="$router.push('/counselor/alerts')" />
        <StatCard icon="Tickets" color="#67C23A" :value="overview.todayAppointments || 0" label="今日预约" @click="$router.push('/counselor/appointments')" />
        <StatCard icon="UserFilled" color="#409EFF" :value="overview.activeStudents || 0" label="活跃学生" />
        <StatCard icon="WarningFilled" color="#F56C6C" :value="overview.totalAlerts || 0" label="总预警数" />
      </div>

      <el-row :gutter="20" class="charts-row">
        <el-col :span="12">
          <div class="chart-card">
            <h4>近期预警趋势</h4>
            <LineChart :data="crisisTrend" height="280px" />
          </div>
        </el-col>
        <el-col :span="12">
          <div class="chart-card">
            <h4>情绪分布</h4>
            <PieChart :data="emotionDist" height="280px" />
          </div>
        </el-col>
      </el-row>

      <el-row :gutter="20" class="mt-16">
        <el-col :span="24">
          <div class="chart-card">
            <h4>测评程度分布</h4>
            <BarChart :data="assessmentDist" height="260px" />
          </div>
        </el-col>
      </el-row>
    </template>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useUserStore } from '@/store/user'
import { getCheckins, getMyPosts } from '@/api/emotion'
import { getMyAssessments } from '@/api/assessment'
import { getMyAppointments } from '@/api/appointment'
import { getRecommended } from '@/api/article'
import { getOverview, getEmotionDistribution, getAssessmentDist, getCrisisTrend } from '@/api/dashboard'
import { severityLabels, severityColors } from '@/utils/constants'
import StatCard from '@/components/common/StatCard.vue'
import LineChart from '@/components/common/LineChart.vue'
import PieChart from '@/components/common/PieChart.vue'
import BarChart from '@/components/common/BarChart.vue'

const userStore = useUserStore()
const role = computed(() => userStore.role)

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '夜深了'
  if (h < 12) return '上午好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const stats = reactive({ checkinCount: 0, postCount: 0, assessmentCount: 0, appointmentCount: 0 })
const recommendedArticles = ref([])

const overview = reactive({ pendingAlerts: 0, todayAppointments: 0, activeStudents: 0, totalAlerts: 0 })
const crisisTrend = ref([])
const emotionDist = ref([])
const assessmentDist = ref([])

onMounted(async () => {
  try {
    if (role.value === 'STUDENT') {
      const [checkins, posts, assessments, appointments, recomms] = await Promise.allSettled([
        getCheckins(), getMyPosts({ page: 1, size: 1 }),
        getMyAssessments({ page: 1, size: 1 }),
        getMyAppointments({ page: 1, size: 1 }), getRecommended()
      ])
      if (checkins.status === 'fulfilled') stats.checkinCount = checkins.value.data?.length || 0
      if (posts.status === 'fulfilled') stats.postCount = posts.value.data?.total || 0
      if (assessments.status === 'fulfilled') stats.assessmentCount = assessments.value.data?.total || 0
      if (appointments.status === 'fulfilled') stats.appointmentCount = appointments.value.data?.total || 0
      if (recomms.status === 'fulfilled') recommendedArticles.value = recomms.value.data || []
    } else {
      const [ov, trend, dist, crisis] = await Promise.allSettled([
        getOverview(), getEmotionDistribution(), getAssessmentDist(), getCrisisTrend(30)
      ])
      if (ov.status === 'fulfilled') {
        const d = ov.value.data
        Object.assign(overview, {
          pendingAlerts: d.pendingAlerts || 0,
          todayAppointments: d.todayAppointments || 0,
          activeStudents: d.activeStudents || 0,
          totalAlerts: d.totalAlerts || 0
        })
      }
      if (trend.status === 'fulfilled') {
        emotionDist.value = trend.value.data || []
      }
      if (dist.status === 'fulfilled') {
        assessmentDist.value = dist.value.data || []
      }
      if (crisis.status === 'fulfilled') {
        crisisTrend.value = crisis.value.data || []
      }
    }
  } catch { /* ignore */ }
})
</script>

<style lang="scss" scoped>
.welcome-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(135deg, #f0f9eb, #ede9fe);
  border-radius: 12px;
  padding: 28px 32px;
  margin-bottom: 24px;
  .banner-text h2 { font-size: 22px; margin-bottom: 6px; }
  .banner-text p { color: #606266; font-size: 14px; }
  .banner-emoji { font-size: 48px; }
}
.stat-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}
.stat-card {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,.04);
  transition: all .2s;
  &:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,.08); }
  .stat-icon { width: 48px; height: 48px; border-radius: 50%; display: flex; align-items: center; justify-content: center; margin: 0 auto 10px; span { font-size: 22px; } }
  .stat-num { font-size: 26px; font-weight: 700; color: #303133; }
  .stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
}
.counselor-stats { grid-template-columns: repeat(4, 1fr); }
.quick-actions {
  margin-bottom: 24px;
  h3 { font-size: 16px; margin-bottom: 12px; }
}
.action-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
}
.action-card {
  background: #fff;
  border-radius: 10px;
  padding: 16px;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(0,0,0,.04);
  transition: all .2s;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  &:hover { transform: translateY(-2px); box-shadow: 0 4px 12px rgba(0,0,0,.08); }
  .action-icon { font-size: 28px; }
  .action-title { font-size: 14px; font-weight: 600; }
  .action-desc { font-size: 12px; color: #909399; }
}
.recommend-section {
  h3 { font-size: 16px; margin-bottom: 12px; }
}
.article-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.article-item {
  background: #fff;
  border-radius: 8px;
  padding: 10px 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  box-shadow: 0 1px 4px rgba(0,0,0,.04);
  &:hover { box-shadow: 0 2px 8px rgba(0,0,0,.08); }
  .article-title { font-size: 14px; color: #303133; }
}
.charts-row { margin-bottom: 16px; }
.chart-card {
  background: #fff;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,.04);
  h4 { font-size: 15px; margin-bottom: 12px; color: #303133; }
}
</style>
