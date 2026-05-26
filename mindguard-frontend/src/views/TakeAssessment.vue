<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ scaleDetail?.name || '心理测评' }}</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <!-- Loading scale -->
    <div v-if="loadingScale" v-loading="true" style="min-height:300px" />

    <!-- Error -->
    <el-result v-else-if="errorMsg" icon="error" :title="errorMsg" :sub-title="errorMsg">
      <template #extra>
        <el-button type="primary" @click="fetchScale">重试</el-button>
        <el-button @click="$router.back()">返回</el-button>
      </template>
    </el-result>

    <!-- Questions -->
    <template v-else-if="questions.length && !showResult">
      <el-progress :percentage="progress" :color="'#67C23A'" class="mb-24" />
      <QuestionItem
        v-for="(q, idx) in questions"
        v-show="idx === currentIndex"
        :key="q.id"
        :question="q"
        :index="idx"
        :model-value="answers[idx]"
        @change="handleAnswer"
      />
      <div class="nav-buttons mt-24">
        <el-button :disabled="currentIndex === 0" @click="currentIndex--">上一题</el-button>
        <el-button v-if="currentIndex < questions.length - 1" type="primary" @click="currentIndex++" :disabled="answers[currentIndex] === undefined">下一题</el-button>
        <el-button v-else type="success" :loading="submitting" @click="handleSubmit" :disabled="answers.some(a => a === undefined)">
          {{ submitting ? '提交中...' : '提交测评' }}
        </el-button>
      </div>
    </template>

    <!-- Result dialog -->
    <el-dialog v-model="resultVisible" title="测评报告" width="680px" :close-on-click-modal="false">
      <ReportView v-if="result" :result="result" />
      <el-empty v-else description="加载中..." />
      <template #footer>
        <el-button @click="resultVisible = false">关 闭</el-button>
        <el-button type="primary" @click="$router.push('/student/assessments/my')">查看历史记录</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getScaleDetail, startAssessment, submitAnswers } from '@/api/assessment'
import QuestionItem from '@/components/common/QuestionItem.vue'
import ReportView from '@/components/common/ReportView.vue'

const route = useRoute()
const loadingScale = ref(true)
const errorMsg = ref('')
const scaleDetail = ref(null)
const questions = ref([])
const currentIndex = ref(0)
const answers = ref([])
const assessmentId = ref(null)
const submitting = ref(false)
const showResult = ref(false)
const resultVisible = ref(false)
const result = ref(null)

const progress = computed(() => {
  if (!questions.value.length) return 0
  return Math.round(((answers.value.filter(a => a !== undefined).length) / questions.value.length) * 100)
})

onMounted(() => fetchScale())

async function fetchScale() {
  loadingScale.value = true
  errorMsg.value = ''
  try {
    const res = await getScaleDetail(route.params.id)
    scaleDetail.value = res.data
    questions.value = res.data.questions || res.data.questionList || []
    answers.value = new Array(questions.value.length)
  } catch (e) {
    errorMsg.value = e.message || '加载失败'
  } finally {
    loadingScale.value = false
  }
}

function handleAnswer({ questionId, optionId }) {
  const idx = questions.value.findIndex(q => q.id === questionId)
  if (idx >= 0) {
    answers.value[idx] = optionId
  }
}

async function handleSubmit() {
  submitting.value = true
  try {
    const startRes = await startAssessment(route.params.id)
    assessmentId.value = startRes.data.assessmentId

    const answerList = questions.value.map((q, i) => ({
      questionId: q.id,
      optionId: answers.value[i]
    }))

    const res = await submitAnswers(assessmentId.value, answerList)
    result.value = res.data
    showResult.value = true
    resultVisible.value = true
  } catch { /* */ }
  finally { submitting.value = false }
}
</script>

<style lang="scss" scoped>
.nav-buttons {
  display: flex;
  justify-content: space-between;
}
</style>
