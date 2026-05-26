<template>
  <el-header class="app-header">
    <div class="header-left">
      <span class="platform-name">MindGuard 智能校园心理平台</span>
    </div>
    <div class="header-right">
      <el-badge v-if="unreadTotal" :value="unreadTotal" class="mr-4">
        <el-button :icon="Bell" circle @click="router.push({ path: `/${roleValue === 'STUDENT' ? 'student' : 'counselor'}/notifications` })" />
      </el-badge>
      <el-button v-else :icon="Bell" circle class="mr-4" @click="router.push({ path: `/${roleValue === 'STUDENT' ? 'student' : 'counselor'}/notifications` })" />
      <el-dropdown @command="handleCommand" trigger="click">
        <span class="user-info">
          <SurnameAvatar :name="userStore.userInfo?.realName || userStore.userInfo?.username" :size="32" />
          <span class="username">{{ userStore.userInfo?.realName || userStore.userInfo?.username || '用户' }}</span>
          <el-icon><ArrowDown /></el-icon>
        </span>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="profile">
              <el-icon><User /></el-icon>个人中心
            </el-dropdown-item>
            <el-dropdown-item command="logout" divided>
              <el-icon><SwitchButton /></el-icon>退出登录
            </el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </el-header>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { useNotificationStore } from '@/store/notification'
import { computed, onMounted } from 'vue'
import SurnameAvatar from '@/components/common/SurnameAvatar.vue'

const router = useRouter()
const userStore = useUserStore()
const notificationStore = useNotificationStore()
const roleValue = computed(() => userStore.role)
const isCounselor = computed(() => roleValue.value === 'COUNSELOR' || roleValue.value === 'ADMIN')
const unreadTotal = computed(() => notificationStore.totalCount)

onMounted(() => {
  notificationStore.fetchUnreadCount()
})

function handleCommand(cmd) {
  if (cmd === 'profile') {
    const prefix = roleValue.value === 'STUDENT' ? '/student' : '/counselor'
    router.push(prefix + '/profile')
  } else if (cmd === 'logout') {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style lang="scss" scoped>
.app-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 56px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 24px;
}
.header-left {
  .platform-name {
    font-size: 15px;
    font-weight: 600;
    color: #303133;
  }
}
.header-right {
  display: flex;
  align-items: center;
  gap: 12px;
}
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background .2s;
  &:hover { background: #f5f7fa; }
  .username { font-size: 14px; color: #303133; }
}
</style>
