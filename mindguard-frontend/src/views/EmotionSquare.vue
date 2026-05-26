<template>
  <div class="page-container">
    <div class="page-header">
      <h2>情绪广场</h2>
      <p class="subtitle">这里没有评判，只有倾听与陪伴</p>
    </div>

    <div v-loading="loading" class="posts-list">
      <template v-if="posts.length">
        <PostCard
          v-for="post in posts"
          :key="post.id"
          :post="post"
          clickable
          show-actions
        >
          <template #actions>
            <el-button link type="primary" size="small" @click="handleConcern(post.id)">
              <el-icon><component :is="concernedPosts.has(post.id) ? StarFilled : Star" /></el-icon> {{ concernedPosts.has(post.id) ? '已关注' : '关注' }}
            </el-button>
          </template>
        </PostCard>
        <div class="pagination-wrap">
          <el-pagination
            v-model:current-page="page"
            :page-size="size"
            :total="total"
            layout="prev, pager, next"
            @current-change="fetchPosts"
          />
        </div>
      </template>
      <EmptyState v-else-if="!loading" message="还没有人倾诉，来做第一个吧" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Star, StarFilled } from '@element-plus/icons-vue'
import { getPublicPosts, concernPost } from '@/api/emotion'
import { ElMessage } from 'element-plus'
import PostCard from '@/components/common/PostCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const loading = ref(false)
const posts = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const concernedPosts = ref(new Set())

onMounted(() => fetchPosts())

async function fetchPosts() {
  loading.value = true
  try {
    const res = await getPublicPosts({ page: page.value, size: size.value })
    posts.value = res.data.records || []
    total.value = res.data.total || 0
  } catch { /* handled by interceptor */ }
  finally { loading.value = false }
}

async function handleConcern(postId) {
  try {
    await concernPost(postId)
    concernedPosts.value.add(postId)
    ElMessage.success('已关注，辅导员将收到提醒')
  } catch { /* handled by interceptor */ }
}
</script>

<style lang="scss" scoped>
.subtitle { color: #909399; font-size: 14px; margin-top: 4px; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 20px; }
</style>
