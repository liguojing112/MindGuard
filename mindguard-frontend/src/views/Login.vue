<template>
  <div class="auth-page">
    <div class="login-card">
      <div class="card-header">
        <span class="logo">🧠</span>
        <h1>MindGuard</h1>
        <p>智能校园心理情绪监测与倾诉平台</p>
      </div>

      <el-form ref="formRef" :model="form" size="large" @submit.prevent="handleLogin">
        <el-form-item>
          <el-radio-group v-model="form.role" class="role-group" @change="onRoleChange">
            <el-radio-button value="STUDENT">学生</el-radio-button>
            <el-radio-button value="COUNSELOR">辅导员</el-radio-button>
            <el-radio-button value="ADMIN">管理员</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <el-form-item>
          <el-select v-model="form.preset" placeholder="请选择登录账号" @change="onPresetChange" style="width: 100%">
            <el-option v-for="u in filteredUsers" :key="u.username" :value="u.username"
              :label="`${u.realName} (${u.username})`" />
          </el-select>
        </el-form-item>

        <el-form-item v-if="form.preset" prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" :prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="login-btn" @click="handleLogin" :loading="loading" :disabled="!form.preset">
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="card-footer">
        还没有账号？<router-link to="/register">立即注册</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import { getLoginUsers } from '@/api/auth'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const formRef = ref(null)

const defaultPasswords = {
  admin: '123456',
  counselor1: '123456',
  counselor2: '123456',
  student1: '123456',
  student2: '123456',
  student3: '123456'
}

const allUsers = ref([])
const form = reactive({
  preset: '',
  username: '',
  password: '',
  role: 'STUDENT'
})

const filteredUsers = computed(() => allUsers.value.filter(u => u.role === form.role))

onMounted(async () => {
  try {
    const res = await getLoginUsers()
    if (res.data) allUsers.value = res.data
  } catch { /* */ }
})

function onRoleChange() {
  form.preset = ''
  form.username = ''
  form.password = ''
}

function onPresetChange(val) {
  const user = allUsers.value.find(u => u.username === val)
  if (user) {
    form.username = user.username
    form.password = defaultPasswords[user.username] || ''
    form.role = user.role
  }
}

async function handleLogin() {
  if (!form.preset) return
  // trigger password validation only when field is visible
  if (form.password === '') {
    ElMessage.warning('请输入密码')
    return
  }

  loading.value = true
  try {
    const data = await userStore.login(form)
    ElMessage.success('登录成功')
    if (data.role === 'STUDENT') router.push('/student/home')
    else if (data.role === 'ADMIN') router.push('/admin/dashboard')
    else router.push('/counselor/dashboard')
  } catch {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-card {
  width: 420px;
  background: #fff;
  border-radius: 16px;
  padding: 48px 40px 32px;
  box-shadow: 0 8px 32px rgba(0,0,0,.08);
}
.card-header {
  text-align: center;
  margin-bottom: 36px;
  .logo { font-size: 48px; }
  h1 { font-size: 28px; color: #67C23A; margin: 8px 0; }
  p { color: #909399; font-size: 14px; }
}
.role-group {
  width: 100%;
  :deep(.el-radio-button) { flex: 1; }
  :deep(.el-radio-button__inner) { width: 100%; }
}
.login-btn {
  width: 100%;
}
.card-footer {
  text-align: center;
  font-size: 14px;
  color: #909399;
}
</style>
