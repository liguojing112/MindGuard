<template>
  <div class="page-container">
    <div class="page-header">
      <el-button @click="$router.back()">返回列表</el-button>
    </div>

    <div v-if="loading" v-loading="true" style="min-height:300px" />

    <el-result v-else-if="error" icon="error" title="加载失败" :sub-title="error">
      <template #extra>
        <el-button type="primary" @click="fetchArticle">重试</el-button>
      </template>
    </el-result>

    <template v-else-if="article">
      <div class="article-detail card-wrapper">
        <h2>{{ article.title }}</h2>
        <div class="article-meta">
          <el-tag size="small" effect="plain">{{ severityLabels[article.severityLevel] || article.severityLevel }}</el-tag>
          <span>{{ formatTime(article.createdAt) }}</span>
        </div>
        <div class="article-content">{{ article.content }}</div>
      </div>
    </template>

    <EmptyState v-else message="文章不存在" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getArticleDetail } from '@/api/article'
import { severityLabels } from '@/utils/constants'
import { formatTime } from '@/utils/helpers'
import EmptyState from '@/components/common/EmptyState.vue'

const route = useRoute()
const loading = ref(false)
const error = ref('')
const article = ref(null)

onMounted(() => fetchArticle())

async function fetchArticle() {
  loading.value = true
  error.value = ''
  try {
    const res = await getArticleDetail(route.params.id)
    article.value = res.data
  } catch (e) {
    error.value = e.message || '加载失败'
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.article-detail {
  h2 { font-size: 22px; margin-bottom: 12px; }
  .article-meta { display: flex; align-items: center; gap: 12px; margin-bottom: 20px; font-size: 13px; color: #c0c4cc; }
  .article-content { font-size: 15px; line-height: 1.9; color: #303133; }
  :deep(.article-content p) { margin-bottom: 12px; }
}
</style>
