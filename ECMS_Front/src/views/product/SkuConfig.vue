<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSkuList, updateSku, batchUpdateSkus, generateSkuCombinations, getProductDetail } from '@/api/product'
import type { SkuItem, SpuItem, SpecTemplate, SpecValue } from '@/types/product'

const route = useRoute()
const router = useRouter()
const spuId = computed(() => Number(route.params.id))
const loading = ref(false)
const skuList = ref<SkuItem[]>([])
const productInfo = ref<SpuItem | null>(null)

// 规格定义（模拟数据，实际应由后端提供）
const specTemplates = ref<SpecTemplate[]>([])

// 编辑中的SKU
const editingSku = ref<SkuItem | null>(null)
const editDialogVisible = ref(false)

// 批量操作
const selectedSkus = ref<number[]>([])

onMounted(async () => {
  loading.value = true
  try {
    const [productRes, skuRes] = await Promise.all([
      getProductDetail(spuId.value),
      getSkuList(spuId.value),
    ])
    productInfo.value = productRes.data
    skuList.value = skuRes.data || []
  } catch {
    ElMessage.warning('数据加载失败，请确认后端服务已启动')
  } finally {
    loading.value = false
  }
})

// 解析specInfo为可读名称
function parseSkuName(specInfo: string): string {
  try {
    const obj = JSON.parse(specInfo)
    return Object.values(obj).join(' / ')
  } catch {
    return specInfo
  }
}

// 编辑单个SKU
function handleEditSku(sku: SkuItem) {
  editingSku.value = { ...sku }
  editDialogVisible.value = true
}

async function handleSaveSku() {
  if (!editingSku.value) return
  try {
    await updateSku(spuId.value, editingSku.value.id!, {
      marketPrice: editingSku.value.marketPrice,
      salePrice: editingSku.value.salePrice,
      memberPrice: editingSku.value.memberPrice,
      stock: editingSku.value.stock,
      warnThreshold: editingSku.value.warnThreshold,
      status: editingSku.value.status,
    })
    ElMessage.success('SKU更新成功')
    editDialogVisible.value = false
    // 刷新列表
    const res = await getSkuList(spuId.value)
    skuList.value = res.data || []
  } catch {
    ElMessage.error('更新失败')
  }
}

// 批量设置价格
async function handleBatchSetPrice() {
  try {
    const { value } = await ElMessageBox.prompt('请输入统一销售价', '批量设置价格', {
      inputType: 'number',
      inputPattern: /^\d+(\.\d{1,2})?$/,
      inputErrorMessage: '请输入有效的价格',
    })
    const price = Number(value)
    const updates = skuList.value.map((sku) => ({
      id: sku.id,
      salePrice: price,
    }))
    await batchUpdateSkus(spuId.value, updates)
    ElMessage.success('批量改价成功')
    const res = await getSkuList(spuId.value)
    skuList.value = res.data || []
  } catch {
    // 取消
  }
}

// 批量设置库存
async function handleBatchSetStock() {
  try {
    const { value } = await ElMessageBox.prompt('请输入统一库存数量', '批量设置库存', {
      inputType: 'number',
      inputPattern: /^\d+$/,
      inputErrorMessage: '请输入有效的整数',
    })
    const stock = Number(value)
    const updates = skuList.value.map((sku) => ({
      id: sku.id,
      stock,
    }))
    await batchUpdateSkus(spuId.value, updates)
    ElMessage.success('批量设置库存成功')
    const res = await getSkuList(spuId.value)
    skuList.value = res.data || []
  } catch {
    // 取消
  }
}

// 批量启用/禁用
async function handleBatchStatus(status: 0 | 1) {
  const label = status === 1 ? '启用' : '禁用'
  try {
    const ids = selectedSkus.value.length ? selectedSkus.value : skuList.value.map((s) => s.id!)
    await ElMessageBox.confirm(`确定批量${label} ${ids.length} 个SKU吗？`, `${label}确认`)
    const updates = ids.map((id) => ({ id, status }))
    await batchUpdateSkus(spuId.value, updates)
    ElMessage.success(`批量${label}成功`)
    const res = await getSkuList(spuId.value)
    skuList.value = res.data || []
  } catch {
    // 取消
  }
}

function goBack() {
  router.push('/product')
}
</script>

<template>
  <div class="page-container">
    <!-- 头部信息 -->
    <div class="page-header">
      <div>
        <h3 v-if="productInfo" class="page-title">
          SKU规格配置 —— {{ productInfo.spuName }}
          <span class="spu-code">{{ productInfo.spuCode }}</span>
        </h3>
        <h3 v-else class="page-title">SKU规格配置</h3>
      </div>
      <el-button @click="goBack">返回列表</el-button>
    </div>

    <!-- 批量操作栏 -->
    <div class="batch-bar">
      <el-button @click="handleBatchSetPrice">批量设置价格</el-button>
      <el-button @click="handleBatchSetStock">批量设置库存</el-button>
      <el-button @click="handleBatchStatus(1)">批量启用</el-button>
      <el-button @click="handleBatchStatus(0)">批量禁用</el-button>
    </div>

    <!-- SKU表格 -->
    <el-table
      :data="skuList"
      v-loading="loading"
      stripe
      border
      style="width: 100%"
      @selection-change="(val: any) => selectedSkus = val.map((v: any) => v.id)"
    >
      <el-table-column type="selection" width="45" align="center" />
      <el-table-column label="序号" width="55" align="center" type="index" />
      <el-table-column label="SKU编码" prop="skuCode" width="150" />
      <el-table-column label="规格名称" min-width="160">
        <template #default="{ row }">
          {{ parseSkuName(row.specInfo) }}
        </template>
      </el-table-column>
      <el-table-column label="市场价" width="120" align="right">
        <template #default="{ row }">
          <span>¥{{ row.marketPrice?.toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="销售价" width="120" align="right">
        <template #default="{ row }">
          <span>¥{{ row.salePrice?.toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="会员价" width="120" align="right">
        <template #default="{ row }">
          <span>¥{{ row.memberPrice?.toFixed(2) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="库存" width="80" align="center">
        <template #default="{ row }">
          <span :class="{ 'stock-warning': row.stock <= 0 }">{{ row.stock }}</span>
        </template>
      </el-table-column>
      <el-table-column label="预警阈值" width="80" align="center">
        <template #default="{ row }">{{ row.warnThreshold }}</template>
      </el-table-column>
      <el-table-column label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleEditSku(row)">编辑</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 空数据提示 -->
    <el-empty v-if="!loading && !skuList.length" description="暂无SKU数据，请在商品编辑页保存后在此配置" />

    <!-- SKU编辑弹窗 -->
    <el-dialog v-model="editDialogVisible" title="编辑SKU" width="500px">
      <el-form v-if="editingSku" :model="editingSku" label-width="100px">
        <el-form-item label="SKU编码">
          <el-input v-model="editingSku.skuCode" disabled />
        </el-form-item>
        <el-form-item label="规格">
          <el-input :model-value="parseSkuName(editingSku.specInfo)" disabled />
        </el-form-item>
        <el-form-item label="市场价">
          <el-input-number v-model="editingSku.marketPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="销售价">
          <el-input-number v-model="editingSku.salePrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="会员价">
          <el-input-number v-model="editingSku.memberPrice" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="editingSku.stock" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="预警阈值">
          <el-input-number v-model="editingSku.warnThreshold" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="editingSku.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveSku">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.spu-code {
  font-size: 12px;
  color: #909399;
  font-weight: 400;
  margin-left: 8px;
}

.batch-bar {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.stock-warning {
  color: #f56c6c;
  font-weight: 600;
}
</style>
