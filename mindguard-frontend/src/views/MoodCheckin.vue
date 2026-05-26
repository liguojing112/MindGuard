<template>
  <div class="page-container">
    <div class="page-header">
      <h2>心情打卡</h2>
    </div>

    <div class="checkin-card">
      <h4>今天的心情如何？</h4>
      <MoodEmojiPicker v-model="selectedMood" />
      <el-input
        v-model="note"
        class="mt-16"
        type="textarea"
        :rows="2"
        placeholder="记录一下今天的心情变化..."
        maxlength="200"
        show-word-limit
      />
      <el-button type="primary" class="mt-16" :loading="checkingIn" @click="handleCheckin">
        {{ checkingIn ? '打卡中...' : '打卡记录' }}
      </el-button>
    </div>

    <div class="calendar-section mt-24">
      <h4>本月打卡记录</h4>
      <el-date-picker
        v-model="selectedMonth"
        type="month"
        placeholder="选择月份"
        format="YYYY-MM"
        value-format="YYYY-MM"
        @change="fetchCalendar"
        class="mb-16"
      />
      <div v-loading="loadingCalendar" class="calendar-grid">
        <div v-for="day in monthDays" :key="day.date" class="calendar-day" :class="{ hasCheckin: day.checked }">
          <div class="day-num">{{ day.dayNum }}</div>
          <div v-if="day.checked" class="day-emoji">{{ day.emoji }}</div>
          <div v-else class="day-empty">-</div>
        </div>
      </div>
      <EmptyState v-if="!monthDays.length && !loadingCalendar" message="本月暂无打卡记录" />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { createCheckin, getCheckinCalendar } from '@/api/emotion'
import { moodEmojis } from '@/utils/constants'
import MoodEmojiPicker from '@/components/common/MoodEmojiPicker.vue'
import EmptyState from '@/components/common/EmptyState.vue'

const selectedMood = ref('😊')
const note = ref('')
const checkingIn = ref(false)
const loadingCalendar = ref(false)

const selectedMonth = ref(new Date().toISOString().slice(0, 7))
const checkinData = ref([])

const emojiMap = computed(() => {
  const map = {}
  moodEmojis.forEach(m => {
    map[m.emoji] = m.emoji
    map[m.value] = m.emoji
    map[m.value.toUpperCase()] = m.emoji
    map[m.value.toLowerCase()] = m.emoji
  })
  return map
})

function getDisplayEmoji(raw) {
  if (!raw) return ''
  const emoji = emojiMap.value[raw]
  return emoji || raw
}

const monthDays = computed(() => {
  if (!checkinData.value.length) return []
  const [y, m] = selectedMonth.value.split('-').map(Number)
  const daysInMonth = new Date(y, m, 0).getDate()
  const result = []
  for (let d = 1; d <= daysInMonth; d++) {
    const dateStr = `${selectedMonth.value}-${String(d).padStart(2, '0')}`
    const found = checkinData.value.find(c => c.date === dateStr || c.createdAt?.startsWith(dateStr))
    result.push({
      date: dateStr,
      dayNum: d,
      checked: !!found,
      emoji: getDisplayEmoji(found?.moodEmoji || found?.emoji)
    })
  }
  return result
})

onMounted(() => fetchCalendar())

async function handleCheckin() {
  if (!selectedMood.value) { ElMessage.warning('请选择心情'); return }
  checkingIn.value = true
  try {
    await createCheckin({ moodEmoji: selectedMood.value, note: note.value })
    ElMessage.success('打卡成功')
    note.value = ''
    await fetchCalendar()
  } catch { /* */ }
  finally { checkingIn.value = false }
}

async function fetchCalendar() {
  loadingCalendar.value = true
  try {
    const res = await getCheckinCalendar(selectedMonth.value)
    checkinData.value = res.data || []
  } catch { checkinData.value = [] }
  finally { loadingCalendar.value = false }
}
</script>

<style lang="scss" scoped>
.checkin-card {
  background: #fff;
  border-radius: 10px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,.04);
  h4 { margin-bottom: 14px; }
}
.calendar-section h4 { margin-bottom: 12px; }
.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 6px;
}
.calendar-day {
  background: #fff;
  border-radius: 8px;
  padding: 8px;
  text-align: center;
  min-height: 56px;
  box-shadow: 0 1px 4px rgba(0,0,0,.04);
  .day-num { font-size: 12px; color: #c0c4cc; }
  .day-emoji { font-size: 20px; margin-top: 2px; }
  .day-empty { color: #e4e7ed; font-size: 14px; margin-top: 2px; }
  &.hasCheckin { background: #f0f9eb; }
}
</style>
