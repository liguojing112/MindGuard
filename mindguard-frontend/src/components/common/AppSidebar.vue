<template>
  <el-aside :width="isCollapse ? '64px' : '220px'" class="app-sidebar">
    <div class="logo-area" @click="goHome">
      <span class="logo-icon">🧠</span>
      <span v-show="!isCollapse" class="logo-text">MindGuard</span>
    </div>

    <el-menu
      :default-active="activeMenu"
      :collapse="isCollapse"
      router
      background-color="#ffffff"
      text-color="#606266"
      active-text-color="#67C23A"
    >
      <!-- Student menus -->
      <template v-if="role === 'STUDENT'">
        <el-menu-item index="/student/home">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/student/emotion-square">
          <el-icon><ChatDotRound /></el-icon>
          <span>情绪广场</span>
        </el-menu-item>
        <el-menu-item index="/student/posts">
          <el-icon><Edit /></el-icon>
          <span>我的倾诉</span>
        </el-menu-item>
        <el-menu-item index="/student/checkin">
          <el-icon><Calendar /></el-icon>
          <span>心情打卡</span>
        </el-menu-item>
        <el-menu-item index="/student/assessments">
          <el-icon><Document /></el-icon>
          <span>心理测评</span>
        </el-menu-item>
        <el-menu-item index="/student/counselors">
          <el-icon><UserFilled /></el-icon>
          <span>心理咨询</span>
        </el-menu-item>
        <el-menu-item index="/student/appointments">
          <el-icon><Tickets /></el-icon>
          <span>我的预约</span>
        </el-menu-item>
        <el-menu-item index="/student/articles">
          <el-icon><Reading /></el-icon>
          <span>心理科普</span>
        </el-menu-item>
      </template>

      <!-- Counselor menus -->
      <template v-if="role === 'COUNSELOR' || role === 'ADMIN'">
        <el-menu-item index="/counselor/dashboard">
          <el-icon><Odometer /></el-icon>
          <span>数据大屏</span>
        </el-menu-item>
        <el-menu-item index="/counselor/alerts">
          <el-icon><Warning /></el-icon>
          <span>预警管理</span>
        </el-menu-item>
        <el-menu-item index="/counselor/appointments">
          <el-icon><Tickets /></el-icon>
          <span>预约管理</span>
        </el-menu-item>
        <el-menu-item index="/counselor/articles">
          <el-icon><Reading /></el-icon>
          <span>文章管理</span>
        </el-menu-item>
      </template>
    </el-menu>

    <div class="collapse-btn" @click="isCollapse = !isCollapse">
      <el-icon><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
    </div>
  </el-aside>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)
const role = computed(() => userStore.role)

const activeMenu = computed(() => route.path)

function goHome() {
  if (role.value === 'STUDENT') router.push('/student/home')
  else router.push('/counselor/dashboard')
}
</script>

<style lang="scss" scoped>
.app-sidebar {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  box-shadow: 2px 0 8px rgba(0,0,0,.04);
  transition: width .3s;
  overflow: hidden;
}
.logo-area {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  cursor: pointer;
  border-bottom: 1px solid #f0f0f0;
}
.logo-icon {
  font-size: 26px;
  margin-right: 8px;
}
.logo-text {
  font-size: 18px;
  font-weight: 700;
  color: #67C23A;
  white-space: nowrap;
}
.el-menu {
  flex: 1;
  border-right: none;
  overflow-y: auto;
  overflow-x: hidden;
}
.el-menu-item {
  margin: 2px 8px;
  border-radius: 6px;
}
.el-menu-item.is-active {
  background-color: #F0F9EB !important;
}
.collapse-btn {
  display: flex;
  justify-content: center;
  padding: 12px;
  border-top: 1px solid #f0f0f0;
  cursor: pointer;
  color: #909399;
  font-size: 18px;
  &:hover { color: #67C23A; }
}
</style>
