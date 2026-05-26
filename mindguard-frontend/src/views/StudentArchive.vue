<template>
  <div class="page-container">
    <div class="page-header">
      <h2>学生档案</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <div v-if="loading" v-loading="true" style="min-height:300px" />

    <template v-else-if="archive">
      <!-- Basic Info -->
      <div class="card-wrapper mb-16">
        <h4>基本信息</h4>
        <el-descriptions :column="2" class="mt-16">
          <el-descriptions-item label="姓名">{{ archive.user?.realName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="账号">{{ archive.user?.username || '-' }}</el-descriptions-item>
          <el-descriptions-item label="邮箱">{{ archive.user?.email || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机">{{ archive.user?.phone || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- Emotion Records -->
      <div class="card-wrapper mb-16">
        <h4>情绪记录</h4>
        <div v-if="archive.emotionPosts?.length" class="mt-16">
          <el-timeline>
            <el-timeline-item
              v-for="record in archive.emotionPosts.slice(0, 10)"
              :key="record.id"
              :timestamp="formatTime(record.createdAt)"
            >
              {{ getEmoji(record.moodEmoji) }} {{ record.content || '' }}
            </el-timeline-item>
          </el-timeline>
        </div>
        <EmptyState v-else message="暂无情绪记录" :image-size="60" />
      </div>

      <!-- Assessment History -->
      <div class="card-wrapper mb-16">
        <h4>测评历史</h4>
        <div v-if="archive.assessments?.length">
          <el-table :data="archive.assessments" class="mt-16">
            <el-table-column prop="scaleName" label="量表" />
            <el-table-column label="得分" width="80">
              <template #default="{ row }">{{ row.totalScore || row.score || '-' }}</template>
            </el-table-column>
            <el-table-column label="程度" width="100">
              <template #default="{ row }">
                <el-tag size="small" effect="plain">{{ row.severityLevel || row.level || row.severity || '正常' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="时间" width="160">
              <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
            </el-table-column>
          </el-table>
        </div>
        <EmptyState v-else message="暂无测评记录" :image-size="60" />
      </div>

      <!-- Appointment History -->
      <div class="card-wrapper">
        <h4>预约历史</h4>
        <div v-if="archive.appointments?.length">
          <el-table :data="archive.appointments" class="mt-16">
            <el-table-column label="咨询师" width="100">
              <template #default="{ row }">{{ row.counselorName || row.counselorId || '-' }}</template>
            </el-table-column>
            <el-table-column label="日期" width="120">
              <template #default="{ row }">{{ row.appointmentDate }}</template>
            </el-table-column>
            <el-table-column label="类型" width="100">
              <template #default="{ row }">{{ row.issueType }}</template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template #default="{ row }"><StatusTag :status="row.status" size="small" /></template>
            </el-table-column>
            <el-table-column label="评分" width="80">
              <template #default="{ row }">
                <el-rate v-if="row.rating" :model-value="row.rating" disabled size="small" />
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
        <EmptyState v-else message="暂无预约记录" :image-size="60" />
      </div>
    </template>

    <el-result v-else icon="error" title="加载失败" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getStudentArchive } from '@/api/appointment'
import StatusTag from '@/components/common/StatusTag.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { formatTime } from '@/utils/helpers'
import { moodEmojis } from '@/utils/constants'

const emojiMap = Object.fromEntries(moodEmojis.map(m => [m.value, m.emoji]))

function getEmoji(val) {
  if (!val) return '😐'
  return emojiMap[val.toUpperCase()] || val
}

const route = useRoute()
const loading = ref(false)
const archive = ref(null)

onMounted(async () => {
  loading.value = true
  try {
    const res = await getStudentArchive(route.params.id)
    archive.value = res.data
  } catch { /* */ }
  finally { loading.value = false }
})
</script>
