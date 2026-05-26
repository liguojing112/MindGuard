<template>
  <div class="page-container">
    <div class="page-header">
      <h2>测评数据</h2>
      <el-button type="primary" @click="handleExport">导出Excel</el-button>
    </div>

    <div v-loading="loading">
      <template v-if="records.length">
        <el-table :data="records" stripe>
          <el-table-column prop="studentName" label="学生" width="120" />
          <el-table-column prop="scaleName" label="量表" width="200" />
          <el-table-column label="得分" width="80">
            <template #default="{ row }">{{ row.totalScore }}</template>
          </el-table-column>
          <el-table-column label="程度" width="100">
            <template #default="{ row }">
              <el-tag :type="severityColors[row.severityLevel]" size="small" effect="plain">
                {{ severityLabels[row.severityLevel] || row.severityLevel }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="建议" min-width="200" show-overflow-tooltip>
            <template #default="{ row }">{{ row.recommendations || '-' }}</template>
          </el-table-column>
          <el-table-column label="测评时间" width="160">
            <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
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
      <EmptyState v-else-if="!loading" message="暂无测评数据" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAllAssessments, exportAssessments } from '@/api/assessment'
import { severityLabels, severityColors } from '@/utils/constants'
import { formatTime } from '@/utils/helpers'
import EmptyState from '@/components/common/EmptyState.vue'

const loading = ref(false)
const records = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getAllAssessments({ page: page.value, size: size.value })
    records.value = res.data.records || []
    total.value = res.data.total || 0
  } catch { /* */ }
  finally { loading.value = false }
}

async function handleExport() {
  try {
    const res = await exportAssessments()
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = '测评统计报表.xlsx'
    a.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch {
    ElMessage.error('导出失败')
  }
}
</script>

<style lang="scss" scoped>
.pagination-wrap { display: flex; justify-content: center; }
</style>
