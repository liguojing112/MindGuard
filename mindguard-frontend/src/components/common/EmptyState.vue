<template>
  <div class="empty-state">
    <el-empty :description="message" :image-size="imageSize">
      <template v-if="$slots.default" #default>
        <slot />
      </template>
    </el-empty>
  </div>
</template>

<script setup>
defineProps({
  message: { type: String, default: '暂无数据' },
  imageSize: { type: Number, default: 120 }
})
</script>

<style lang="scss" scoped>
.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
}
</style>
