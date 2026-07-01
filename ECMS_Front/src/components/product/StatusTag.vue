<script setup lang="ts">
import { computed } from 'vue'
import { ProductStatus, ProductStatusLabel, ProductStatusColor } from '@/types/product'

const props = defineProps<{
  status: number
}>()

const tagType = computed(() => {
  switch (props.status) {
    case ProductStatus.ON_SHELF:
      return 'success'
    case ProductStatus.PENDING:
      return 'warning'
    case ProductStatus.REJECTED:
      return 'danger'
    case ProductStatus.DRAFT:
      return 'info'
    case ProductStatus.OFF_SHELF:
      return 'info'
    default:
      return 'info'
  }
})

const label = computed(() => ProductStatusLabel[props.status] || '未知')
const color = computed(() => ProductStatusColor[props.status] || '#909399')
</script>

<template>
  <el-tag :type="tagType" :color="status === ProductStatus.OFF_SHELF ? '#C0C4CC' : undefined" effect="dark" size="small">
    {{ label }}
  </el-tag>
</template>
