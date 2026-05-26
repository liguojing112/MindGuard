<template>
  <div class="page-container">
    <div class="page-header">
      <h2>我的测评记录</h2>
    </div>

    <div v-loading="loading">
      <template v-if="assessments.length">
        <el-table :data="assessments" stripe style="width:100%">
          <el-table-column prop="scaleName" label="量表名称" min-width="160" />
          <el-table-column label="得分" width="100">
            <template #default="{ row }">
              <strong>{{ row.score || row.totalScore || '-' }}</strong>
            </template>
          </el-table-column>
          <el-table-column label="程度" width="120">
            <template #default="{ row }">
              <el-tag :type="levelType(row.level || row.severity)" effect="plain">
                {{ row.level || row.severity || '正常' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="测评时间" width="180">
            <template #default="{ row }">{{ formatTime(row.createdAt || row.assessmentDate) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="100" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="showReport(row)">查看报告</el-button>
            </template>
          </el-table-column>
        </el-table>
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
      <EmptyState v-else-if="!loading" message="还没有测评记录">
        <el-button type="primary" @click="$router.push('/student/assessments')">去测评</el-button>
      </EmptyState>
    </div>

    <el-dialog v-model="dialogVisible" title="测评报告" width="680px">
      <ReportView v-if="currentReport" :result="currentReport" />
      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getMyAssessments, getResult } from '@/api/assessment'
import ReportView from '@/components/common/ReportView.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { formatTime } from '@/utils/helpers'

const loading = ref(false)
const assessments = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const currentReport = ref(null)

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getMyAssessments({ page: page.value, size: size.value })
    assessments.value = res.data.records || []
    total.value = res.data.total || 0
  } catch { /* */ }
  finally { loading.value = false }
}

async function showReport(row) {
  try {
    const res = await getResult(row.id || row.assessmentId)
    currentReport.value = res.data
    dialogVisible.value = true
  } catch { /* */ }
}

function levelType(level) {
  if (!level) return 'info'
  const code = String(level).toUpperCase()
  if (code === 'SEVERE' || code === '重度' || code === '严重') return 'danger'
  if (code === 'MODERATE' || code === 'MILD' || code === '中度' || code === '中等' || code === '轻度') return 'warning'
  return 'success'
}
</script>

<style lang="scss" scoped>
.pagination-wrap { display: flex; justify-content: center; }
</style>
