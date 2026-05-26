<template>
  <div class="page-container">
    <div class="page-header">
      <h2>文章管理</h2>
      <el-button type="primary" @click="openAdd">新增文章</el-button>
    </div>

    <div v-loading="loading">
      <template v-if="articles.length">
        <el-table :data="articles" stripe>
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column label="严重程度" width="100">
            <template #default="{ row }">
              <el-tag size="small" effect="plain">{{ severityLabels[row.severityLevel] || row.severityLevel }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="创建时间" width="160">
            <template #default="{ row }">{{ formatTime(row.createdAt) }}</template>
          </el-table-column>
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <el-button link type="primary" size="small" @click="openEdit(row)">编辑</el-button>
              <el-popconfirm title="确定删除该文章？" @confirm="handleDelete(row.id)">
                <template #reference>
                  <el-button link type="danger" size="small">删除</el-button>
                </template>
              </el-popconfirm>
            </template>
          </el-table-column>
        </el-table>
        <div class="pagination-wrap mt-16">
          <el-pagination
            v-model:current-page="page"
            :page-size="size"
            :total="total"
            layout="prev, pager, next"
            @current-change="fetchArticles"
          />
        </div>
      </template>
      <EmptyState v-else-if="!loading" message="暂无文章" />
    </div>

    <!-- Add/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑文章' : '新增文章'" width="700px" @closed="resetForm">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题" required>
          <el-input v-model="form.title" placeholder="文章标题" />
        </el-form-item>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="文章摘要" />
        </el-form-item>
        <el-form-item label="内容" required>
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder="文章内容（支持HTML）" />
        </el-form-item>
        <el-form-item label="严重程度">
          <el-select v-model="form.severityLevel" placeholder="选择" style="width:100%">
            <el-option v-for="(label, key) in severityLabels" :key="key" :label="label" :value="key" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="handleSave">{{ isEdit ? '保存' : '创建' }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getArticles, createArticle, updateArticle, deleteArticle } from '@/api/article'
import { severityLabels } from '@/utils/constants'
import { formatTime } from '@/utils/helpers'
import EmptyState from '@/components/common/EmptyState.vue'

const loading = ref(false)
const articles = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref(null)
const saving = ref(false)

const form = reactive({
  title: '',
  summary: '',
  content: '',
  severityLevel: 'LOW'
})

onMounted(() => fetchArticles())

async function fetchArticles() {
  loading.value = true
  try {
    const res = await getArticles({ page: page.value, size: size.value })
    articles.value = res.data.records || []
    total.value = res.data.total || 0
  } catch { /* */ }
  finally { loading.value = false }
}

function openAdd() {
  isEdit.value = false
  editingId.value = null
  resetForm()
  dialogVisible.value = true
}

function openEdit(row) {
  isEdit.value = true
  editingId.value = row.id
  form.title = row.title || ''
  form.summary = row.summary || ''
  form.content = row.content || ''
  form.severityLevel = row.severityLevel || 'LOW'
  dialogVisible.value = true
}

function resetForm() {
  form.title = ''
  form.summary = ''
  form.content = ''
  form.severityLevel = 'LOW'
}

async function handleSave() {
  if (!form.title || !form.content) { ElMessage.warning('标题和内容不能为空'); return }
  saving.value = true
  try {
    if (isEdit.value) {
      await updateArticle(editingId.value, { ...form })
      ElMessage.success('修改成功')
    } else {
      await createArticle({ ...form })
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    await fetchArticles()
  } catch { /* */ }
  finally { saving.value = false }
}

async function handleDelete(id) {
  try {
    await deleteArticle(id)
    ElMessage.success('删除成功')
    await fetchArticles()
  } catch { /* */ }
}
</script>

<style lang="scss" scoped>
.pagination-wrap { display: flex; justify-content: center; }
</style>
