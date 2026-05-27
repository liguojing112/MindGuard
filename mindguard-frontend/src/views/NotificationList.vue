<template>
  <div class="page-container">
    <div class="page-header">
      <h2>消息通知</h2>
    </div>

    <div v-loading="loading" class="notify-list">
      <template v-if="list.length">
        <div
          v-for="item in list"
          :key="item.id"
          class="notify-card"
          :class="{ unread: item.isRead === 0 }"
          @click="handleRead(item)"
        >
          <div class="notify-header">
            <el-tag :type="typeTag(item.type)" size="small">{{ typeLabel(item.type) }}</el-tag>
            <span class="notify-time">{{ item.createdAt }}</span>
          </div>
          <h4 class="notify-title">{{ item.title }}</h4>
          <p class="notify-content">{{ item.content }}</p>
        </div>
        <div class="pagination-wrap">
          <el-pagination
            v-model:current-page="page"
            :page-size="size"
            :total="total"
            layout="prev, pager, next"
            @current-change="fetchList"
          />
        </div>
      </template>
      <EmptyState v-else-if="!loading" message="暂无通知消息" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getNotifications, markAsRead } from '@/api/notification'
import { useNotificationStore } from '@/store/notification'
import { useUserStore } from '@/store/user'
import EmptyState from '@/components/common/EmptyState.vue'

const router = useRouter()
const store = useNotificationStore()
const userStore = useUserStore()
const isStudent = computed(() => userStore.role === 'STUDENT')

const loading = ref(false)
const list = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)

const typeMap = { ALERT: '预警', CONCERN: '关注', APPOINTMENT: '预约', APPOINTMENT_REMINDER: '预约提醒', SYSTEM: '系统' }
const typeTagMap = { ALERT: 'danger', CONCERN: 'warning', APPOINTMENT: 'primary', APPOINTMENT_REMINDER: 'warning', SYSTEM: 'info' }

function typeLabel(type) { return typeMap[type] || type }
function typeTag(type) { return typeTagMap[type] || 'info' }

onMounted(() => fetchList())

async function fetchList() {
  loading.value = true
  try {
    const res = await getNotifications({ page: page.value, size: size.value })
    list.value = res.data.records || []
    total.value = res.data.total || 0
  } catch { /* handled by interceptor */ }
  finally { loading.value = false }
}

async function handleRead(item) {
  if (item.isRead === 0) {
    try {
      await markAsRead(item.id)
      item.isRead = 1
      store.fetchUnreadCount()
    } catch { /* ignore */ }
  }
  if (item.relatedId) {
    if (item.type === 'ALERT' || item.type === 'CONCERN') {
      if (isStudent.value) {
        router.push(`/student/posts`)
      } else {
        router.push(`/counselor/alerts/${item.relatedId}`)
      }
    } else if (item.type === 'APPOINTMENT' || item.type === 'APPOINTMENT_REMINDER') {
      if (isStudent.value) {
        router.push(`/student/appointments`)
      } else {
        router.push(`/counselor/appointments/${item.relatedId}`)
      }
    }
  }
}
</script>

<style lang="scss" scoped>
.notify-list { max-width: 720px; }
.notify-card {
  background: #fff;
  border-radius: 8px;
  padding: 16px 20px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: box-shadow .2s;
  border-left: 3px solid transparent;
  &:hover { box-shadow: 0 2px 12px rgba(0,0,0,.08); }
  &.unread { border-left-color: #409eff; background: #f0f7ff; }
  &.unread .notify-title { font-weight: 600; }
}
.notify-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 8px; }
.notify-time { font-size: 12px; color: #909399; }
.notify-title { font-size: 15px; margin: 0 0 6px; color: #303133; }
.notify-content { font-size: 13px; color: #606266; margin: 0; line-height: 1.6; }
.pagination-wrap { display: flex; justify-content: center; margin-top: 20px; }
</style>
