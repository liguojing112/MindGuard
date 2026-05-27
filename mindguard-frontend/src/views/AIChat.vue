<template>
  <div class="page-container chat-page">
    <div class="page-header">
      <h2>AI 聊一聊</h2>
      <p class="subtitle">我在这里倾听你的一切，没有评判，只有陪伴</p>
    </div>

    <div class="chat-card card-wrapper">
      <div class="chat-messages" ref="msgContainer">
        <div v-if="messages.length === 0" class="chat-empty">
          <p>有什么想和我聊聊的吗？</p>
          <p class="text-secondary">你可以分享开心的事、烦恼的事，或者任何你想说的话</p>
        </div>
        <div v-for="msg in messages" :key="msg.id || msg._id" :class="['chat-bubble', msg.role]">
          <div class="bubble-avatar">
            <span v-if="msg.role === 'user'">{{ userName.charAt(0) }}</span>
            <span v-else class="ai-avatar">AI</span>
          </div>
          <div class="bubble-body">
            <div class="bubble-text">{{ msg.content || msg.reply }}</div>
            <div v-if="msg.role === 'assistant' && (msg.emotionScore !== undefined || msg.emotionScore !== null)" class="bubble-tag">
              <el-tag :type="tagType(msg.emotionScore)" size="small" effect="plain">
                {{ msg.emotionLabel || '正常情绪' }} ({{ msg.emotionScore }}分)
              </el-tag>
            </div>
          </div>
        </div>
        <div v-if="sending" class="chat-bubble assistant">
          <div class="bubble-avatar"><span class="ai-avatar">AI</span></div>
          <div class="bubble-body">
            <div class="bubble-text typing">正在思考中...</div>
          </div>
        </div>
      </div>

      <div class="chat-input">
        <el-input
          v-model="inputText"
          type="textarea"
          :rows="3"
          placeholder="输入你想说的话..."
          maxlength="500"
          show-word-limit
          @keyup.enter.exact="handleSend"
        />
        <el-button type="primary" class="mt-8" :loading="sending" :disabled="!inputText.trim()" @click="handleSend">
          发送
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, computed } from 'vue'
import { sendChatMessage, getChatHistory } from '@/api/aiChat'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const userName = computed(() => userStore.userInfo?.realName || userStore.userInfo?.username || '用户')

const messages = ref([])
const inputText = ref('')
const sending = ref(false)
const msgContainer = ref(null)

onMounted(async () => {
  try {
    const res = await getChatHistory()
    if (res.data) messages.value = res.data
  } catch { /* */ }
})

function tagType(score) {
  if (score < 40) return 'danger'
  if (score < 70) return 'warning'
  return 'success'
}

async function handleSend() {
  const text = inputText.value.trim()
  if (!text || sending.value) return

  // 添加用户消息到界面
  messages.value.push({ _id: Date.now(), role: 'user', content: text })
  inputText.value = ''
  sending.value = true
  scrollToBottom()

  try {
    const res = await sendChatMessage(text)
    const data = res.data
    messages.value.push({
      _id: Date.now() + 1,
      role: 'assistant',
      content: data.reply,
      emotionScore: data.emotionScore,
      emotionLabel: data.emotionLabel
    })
    scrollToBottom()
  } catch {
    messages.value.push({
      _id: Date.now() + 1,
      role: 'assistant',
      content: '抱歉，我暂时无法回复，请稍后再试。',
      emotionScore: 50,
      emotionLabel: '未知'
    })
  } finally {
    sending.value = false
  }
}

function scrollToBottom() {
  nextTick(() => {
    if (msgContainer.value) {
      msgContainer.value.scrollTop = msgContainer.value.scrollHeight
    }
  })
}
</script>

<style lang="scss" scoped>
.chat-page {
  height: calc(100vh - 96px);
  display: flex;
  flex-direction: column;
}
.chat-card {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px 0;
}
.chat-empty {
  text-align: center;
  padding: 60px 20px;
  color: #606266;
  font-size: 15px;
}
.chat-bubble {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  padding: 0 16px;
  &.user {
    flex-direction: row-reverse;
    .bubble-avatar {
      background: #67C23A;
    }
    .bubble-text {
      background: #e1f3d8;
      border-radius: 12px 4px 12px 12px;
    }
    .bubble-body {
      align-items: flex-end;
    }
  }
  &.assistant {
    .bubble-avatar {
      background: #8b5cf6;
    }
    .bubble-text {
      background: #f5f3ff;
      border-radius: 4px 12px 12px 12px;
    }
  }
}
.bubble-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}
.bubble-body {
  display: flex;
  flex-direction: column;
  max-width: 70%;
}
.bubble-text {
  padding: 10px 14px;
  font-size: 14px;
  line-height: 1.6;
  color: #303133;
  word-break: break-word;
  &.typing {
    color: #909399;
    font-style: italic;
  }
}
.bubble-tag {
  margin-top: 6px;
}
.chat-input {
  padding: 16px 16px 0;
  border-top: 1px solid #f0f0f0;
  :deep(.el-textarea__inner) {
    resize: none;
  }
}
.subtitle {
  color: #909399;
  font-size: 14px;
  margin-top: 4px;
}
</style>
