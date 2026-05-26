<template>
  <div class="page-container">
    <div class="page-header">
      <h2>我的倾诉</h2>
    </div>

    <PostForm ref="postFormRef" :submitting="submitting" @submit="handleCreatePost" />

    <div class="mt-24" v-loading="loading">
      <template v-if="posts.length">
        <PostCard
          v-for="post in posts"
          :key="post.id"
          :post="post"
          clickable
          show-actions
        >
          <template #actions>
            <el-popconfirm title="确定删除这条倾诉吗？" @confirm="handleDelete(post.id)">
              <template #reference>
                <el-button link type="danger" size="small">删除</el-button>
              </template>
            </el-popconfirm>
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
      <EmptyState v-else-if="!loading" message="还没有倾诉，写下你的第一条心情吧">
        <el-button type="primary" @click="scrollToForm">现在写一条</el-button>
      </EmptyState>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { createPost, getMyPosts, deletePost } from '@/api/emotion'
import PostForm from '@/components/common/PostForm.vue'
import PostCard from '@/components/common/PostCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const loading = ref(false)
const submitting = ref(false)
const posts = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const postFormRef = ref(null)

onMounted(() => fetchPosts())

async function fetchPosts() {
  loading.value = true
  try {
    const res = await getMyPosts({ page: page.value, size: size.value })
    posts.value = res.data.records || []
    total.value = res.data.total || 0
  } catch { /* */ }
  finally { loading.value = false }
}

async function handleCreatePost(data) {
  submitting.value = true
  try {
    await createPost(data)
    ElMessage.success('倾诉发布成功')
    postFormRef.value?.reset()
    page.value = 1
    await fetchPosts()
  } catch { /* */ }
  finally { submitting.value = false }
}

async function handleDelete(id) {
  try {
    await deletePost(id)
    ElMessage.success('删除成功')
    await fetchPosts()
  } catch { /* */ }
}

function scrollToForm() {
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
</script>

<style lang="scss" scoped>
.pagination-wrap { display: flex; justify-content: center; margin-top: 20px; }
</style>
