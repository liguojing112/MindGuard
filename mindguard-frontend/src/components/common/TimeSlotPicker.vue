<template>
  <div class="timeslot-picker">
    <p class="picker-hint" v-if="!slots.length">请先选择日期查看可用时段</p>
    <div class="slot-grid">
      <div
        v-for="slot in slots"
        :key="slot"
        class="slot-item"
        :class="{ active: modelValue === slot, disabled: !isSlotAvailable(slot) }"
        @click="selectSlot(slot)"
      >
        {{ slot }}
      </div>
    </div>
    <el-empty v-if="slots.length && availableSlots.length === 0" description="该日期暂无可用时段" :image-size="60" />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  slots: { type: Array, default: () => [] },
  modelValue: { type: String, default: '' },
  bookedSlots: { type: Array, default: () => [] }
})

const emit = defineEmits(['update:modelValue'])

const availableSlots = computed(() => props.slots.filter(s => !props.bookedSlots.includes(s)))

function isSlotAvailable(slot) {
  return !props.bookedSlots.includes(slot)
}

function selectSlot(slot) {
  if (!isSlotAvailable(slot)) return
  emit('update:modelValue', slot)
}
</script>

<style lang="scss" scoped>
.timeslot-picker {
  .picker-hint { font-size: 13px; color: #909399; margin-bottom: 12px; }
}
.slot-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}
.slot-item {
  padding: 10px;
  text-align: center;
  border-radius: 8px;
  border: 2px solid #e4e7ed;
  cursor: pointer;
  font-size: 14px;
  transition: all .2s;
  &:hover:not(.disabled) { border-color: #67C23A; background: #f0f9eb; }
  &.active { border-color: #67C23A; background: #f0f9eb; color: #67C23A; font-weight: 600; }
  &.disabled { background: #f5f7fa; color: #c0c4cc; cursor: not-allowed; }
}
</style>
