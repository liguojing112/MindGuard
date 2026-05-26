<template>
  <div class="page-container">
    <div class="page-header">
      <h2>心理科普</h2>
      <el-select v-model="severityLevel" placeholder="筛选" clearable style="width:150px" @change="fetchArticles">
        <el-option v-for="(label, key) in severityLabels" :key="key" :label="label" :value="key" />
      </el-select>
    </div>

    <div v-loading="loading">
      <template v-if="articles.length">
        <div v-for="article in articles" :key="article.id" class="article-card" @click="$router.push(`/student/articles/${article.id}`)">
          <h4>{{ article.title }}</h4>
          <p class="article-summary">{{ article.summary || article.content?.slice(0, 120) + '...' }}</p>
          <div class="article-meta">
            <el-tag size="small" effect="plain">{{ severityLabels[article.severityLevel] || article.severityLevel }}</el-tag>
            <span>{{ formatTime(article.createdAt) }}</span>
          </div>
        </div>
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
      <EmptyState v-else-if="!loading" message="暂无科普文章" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getArticles } from '@/api/article'
import { severityLabels } from '@/utils/constants'
import { formatTime } from '@/utils/helpers'
import EmptyState from '@/components/common/EmptyState.vue'

const loading = ref(false)
const articles = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const severityLevel = ref('')

onMounted(() => fetchArticles())

async function fetchArticles() {
  loading.value = true
  try {
    const params = { page: page.value, size: size.value }
    if (severityLevel.value) params.severityLevel = severityLevel.value
    const res = await getArticles(params)
    articles.value = res.data.records || []
    total.value = res.data.total || 0
  } catch { /* */ }
  finally { loading.value = false }
}
</script>

<style lang="scss" scoped>
.article-card {
  background: #fff;
  border-radius: 10px;
  padding: 18px 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,.04);
  margin-bottom: 12px;
  cursor: pointer;
  transition: all .2s;
  &:hover { box-shadow: 0 4px 12px rgba(0,0,0,.08); }
  h4 { font-size: 16px; margin-bottom: 6px; }
  .article-summary { font-size: 13px; color: #909399; margin-bottom: 8px; }
  .article-meta { display: flex; align-items: center; gap: 12px; font-size: 12px; color: #c0c4cc; }
}
.pagination-wrap { display: flex; justify-content: center; }
</style>
