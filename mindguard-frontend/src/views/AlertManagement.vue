<template>
  <div class="page-container">
    <div class="page-header">
      <h2>预警管理</h2>
      <el-select v-model="statusFilter" placeholder="状态筛选" clearable style="width:150px" @change="fetchAlerts">
        <el-option v-for="(label, key) in statusLabels" :key="key" :label="label" :value="key" />
      </el-select>
    </div>

    <div v-loading="loading">
      <template v-if="alerts.length">
        <el-table :data="alerts" stripe @row-click="goDetail" style="cursor:pointer">
          <el-table-column label="学生" width="120">
            <template #default="{ row }">{{ row.studentName || row.realName || '匿名' }}</template>
          </el-table-column>
          <el-table-column prop="postContent" label="倾诉内容" min-width="200" show-overflow-tooltip />
          <el-table-column label="分析结果" width="140">
            <template #default="{ row }">
              <AnalysisBadge :label="row.analysisLabel || row.analysisResult" />
            </template>
          </el-table-column>
          <el-table-column label="严重程度" width="100">
            <template #default="{ row }">
              <el-tag :type="severityColors[row.alertLevel]" size="small" effect="plain">
                {{ severityLabels[row.alertLevel] || row.alertLevel }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <StatusTag :status="row.status" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="时间" width="160">
            <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
          </el-table-column>
        </el-table>
        <div class="pagination-wrap mt-16">
          <el-pagination
            v-model:current-page="page"
            :page-size="size"
            :total="total"
            layout="prev, pager, next"
            @current-change="fetchAlerts"
          />
        </div>
      </template>
      <EmptyState v-else-if="!loading" message="暂无预警记录" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getAlerts } from '@/api/alert'
import { statusLabels, severityLabels, severityColors } from '@/utils/constants'
import { formatTime } from '@/utils/helpers'
import StatusTag from '@/components/common/StatusTag.vue'
import AnalysisBadge from '@/components/common/AnalysisBadge.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const router = useRouter()
const loading = ref(false)
const alerts = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const statusFilter = ref('')

onMounted(() => fetchAlerts())

async function fetchAlerts() {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (statusFilter.value) params.status = statusFilter.value
    const res = await getAlerts(params)
    alerts.value = res.data.records || []
    total.value = res.data.total || 0
  } catch { /* */ }
  finally { loading.value = false }
}

function goDetail(row) {
  router.push(`/counselor/alerts/${row.id}`)
}
</script>

<style lang="scss" scoped>
.pagination-wrap { display: flex; justify-content: center; }
</style>
