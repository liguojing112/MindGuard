<template>
  <div
    class="surname-avatar"
    :style="{ width: size + 'px', height: size + 'px', fontSize: (size * 0.44) + 'px' }"
  >
    {{ surname }}
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  name: { type: String, default: '' },
  size: { type: Number, default: 36 }
})

const surname = computed(() => {
  if (!props.name) return '用'
  return props.name.charAt(0)
})
</script>

<style lang="scss" scoped>
.surname-avatar {
  border-radius: 50%;
  background: #95D475;
  color: #fff;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  flex-shrink: 0;
  user-select: none;
}
</style>
