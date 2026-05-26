<template>
  <el-timeline>
    <el-timeline-item
      v-for="item in items"
      :key="item.status"
      :timestamp="item.time"
      :color="getColor(item.status)"
      placement="top"
    >
      <div class="timeline-content">
        <StatusTag :status="item.status" size="small" />
        <span v-if="item.remark" class="timeline-remark">{{ item.remark }}</span>
        <span v-if="item.operator" class="timeline-operator">{{ item.operator }}</span>
      </div>
    </el-timeline-item>
  </el-timeline>
</template>

<script setup>
import { computed } from 'vue'
import StatusTag from './StatusTag.vue'

const props = defineProps({
  history: { type: Array, default: () => [] }
})

const items = computed(() => {
  if (props.history && props.history.length) return props.history
  return []
})

function getColor(status) {
  const map = { PENDING: '#E6A23C', APPROVED: '#67C23A', REJECTED: '#F56C6C', COMPLETED: '#409EFF' }
  return map[status] || '#909399'
}
</script>

<style lang="scss" scoped>
.timeline-content {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}
.timeline-remark { font-size: 13px; color: #606266; }
.timeline-operator { font-size: 12px; color: #c0c4cc; }
</style>
