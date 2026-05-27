<template>
  <div class="page-container">
    <div class="page-header">
      <h2>个人中心</h2>
    </div>

    <el-row :gutter="20">
      <el-col :span="8">
        <div class="card-wrapper profile-card text-center">
          <el-upload
            class="avatar-uploader"
            action="/api/auth/avatar"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
            accept="image/*"
          >
            <SurnameAvatar v-if="!avatarUrl" :name="userStore.userInfo?.realName" :size="80" />
            <img v-else :src="avatarUrl" class="avatar-img" />
            <div class="avatar-overlay">更换头像</div>
          </el-upload>
          <h3 class="mt-16">{{ userStore.userInfo?.realName || '用户' }}</h3>
          <p class="role-tag">
            <el-tag size="small" effect="plain">{{ role === 'STUDENT' ? '学生' : '辅导员' }}</el-tag>
          </p>
          <p class="text-secondary">{{ userStore.userInfo?.username }}</p>
        </div>
      </el-col>

      <el-col :span="16">
        <div class="card-wrapper">
          <h4>个人信息</h4>
          <el-form :model="form" label-width="80px" class="mt-16">
            <el-form-item label="用户名">
              <el-input v-model="form.username" disabled />
            </el-form-item>
            <el-form-item label="真实姓名">
              <el-input v-model="form.realName" />
            </el-form-item>
            <el-form-item label="邮箱">
              <el-input v-model="form.email" />
            </el-form-item>
            <el-form-item label="手机号">
              <el-input v-model="form.phone" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button>
            </el-form-item>
          </el-form>
        </div>

        <div v-if="role === 'STUDENT'" class="card-wrapper mt-16">
          <h4>我的统计</h4>
          <div class="stats-row mt-16">
            <div class="stat-item">
              <span class="stat-num">{{ stats.postCount }}</span>
              <span class="stat-label">倾诉次数</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">{{ stats.assessmentCount }}</span>
              <span class="stat-label">测评次数</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">{{ stats.appointmentCount }}</span>
              <span class="stat-label">预约次数</span>
            </div>
          </div>
        </div>

      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import SurnameAvatar from '@/components/common/SurnameAvatar.vue'
import { updateProfile } from '@/api/auth'
import { getMyPosts } from '@/api/emotion'
import { getMyAssessments } from '@/api/assessment'
import { getMyAppointments } from '@/api/appointment'

const userStore = useUserStore()
const role = computed(() => userStore.role)

const uploadHeaders = { Authorization: `Bearer ${userStore.token}` }
const avatarUrl = ref(userStore.userInfo?.avatar || '')

const saving = ref(false)
const form = reactive({
  username: '',
  realName: '',
  email: '',
  phone: ''
})

const stats = reactive({ postCount: 0, assessmentCount: 0, appointmentCount: 0 })

onMounted(async () => {
  const info = userStore.userInfo
  if (info) {
    form.username = info.username || ''
    form.realName = info.realName || ''
    form.email = info.email || ''
    form.phone = info.phone || ''
  }

  if (role.value === 'STUDENT') {
    const [posts, assessments, appointments] = await Promise.allSettled([
      getMyPosts({ page: 1, size: 1 }),
      getMyAssessments({ page: 1, size: 1 }),
      getMyAppointments({ page: 1, size: 1 })
    ])
    if (posts.status === 'fulfilled') stats.postCount = posts.value.data?.total || 0
    if (assessments.status === 'fulfilled') stats.assessmentCount = assessments.value.data?.total || 0
    if (appointments.status === 'fulfilled') stats.appointmentCount = appointments.value.data?.total || 0
  }
})

function handleAvatarSuccess(res) {
  if (res.code === 200) {
    avatarUrl.value = res.data.url
    userStore.fetchInfo()
    ElMessage.success('头像上传成功')
  }
}

function beforeAvatarUpload(file) {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isImage) { ElMessage.error('只能上传图片文件'); return false }
  if (!isLt2M) { ElMessage.error('图片大小不能超过2MB'); return false }
  return true
}

async function handleSave() {
  saving.value = true
  try {
    await updateProfile({
      realName: form.realName,
      email: form.email,
      phone: form.phone
    })
    ElMessage.success('保存成功')
    await userStore.fetchInfo()
  } catch { /* */ }
  finally { saving.value = false }
}
</script>

<style lang="scss" scoped>
.avatar-uploader {
  display: inline-block;
  position: relative;
  cursor: pointer;
  .avatar-img {
    width: 80px; height: 80px; border-radius: 50%; object-fit: cover;
  }
  .avatar-overlay {
    position: absolute;
    top: 0; left: 50%; transform: translateX(-50%);
    width: 80px; height: 80px; border-radius: 50%;
    background: rgba(0,0,0,.4);
    color: #fff; font-size: 12px;
    display: flex; align-items: center; justify-content: center;
    opacity: 0; transition: opacity .2s;
  }
  &:hover .avatar-overlay { opacity: 1; }
}
.profile-card {
  h3 { margin-bottom: 4px; }
  .role-tag { margin-bottom: 4px; }
}
.stats-row {
  display: flex;
  gap: 24px;
}
.stat-item {
  text-align: center;
  .stat-num { font-size: 24px; font-weight: 700; color: #67C23A; display: block; }
  .stat-label { font-size: 13px; color: #909399; }
}
</style>
