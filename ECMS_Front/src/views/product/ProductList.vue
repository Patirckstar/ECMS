<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getProductList, deleteProduct, onShelfProduct, offShelfProduct, batchOnShelf, batchOffShelf, batchDeleteProducts, exportProducts } from '@/api/product'
import { getCategoryTree } from '@/api/category'
import { getTagList } from '@/api/tag'
import StatusTag from '@/components/product/StatusTag.vue'
import type { ProductQueryParams, SpuItem, Category, Tag } from '@/types/product'
import { ProductStatus, ProductStatusLabel } from '@/types/product'

const router = useRouter()

// ========== 筛选条件 ==========
const queryParams = reactive<ProductQueryParams>({
  page: 1,
  pageSize: 20,
  keyword: '',
  status: '',
  categoryId: undefined,
  tagId: undefined,
  priceMin: undefined,
  priceMax: undefined,
  stockMin: undefined,
  stockMax: undefined,
  createTimeStart: undefined,
  createTimeEnd: undefined,
})

// ========== 数据 ==========
const tableData = ref<SpuItem[]>([])
const total = ref(0)
const loading = ref(false)
const selectedIds = ref<number[]>([])
const categoryTree = ref<Category[]>([])
const tagList = ref<Tag[]>([])

const statusOptions = [
  { value: '', label: '全部' },
  ...Object.entries(ProductStatusLabel).map(([value, label]) => ({
    value: Number(value),
    label,
  })),
]

// ========== 生命周期 ==========
onMounted(async () => {
  try {
    const [catRes, tagRes] = await Promise.all([getCategoryTree(), getTagList()])
    categoryTree.value = catRes.data
    tagList.value = tagRes.data
  } catch {
    // 接口未就绪时静默处理
  }
  await fetchData()
})

// ========== 方法 ==========
async function fetchData() {
  loading.value = true
  try {
    const params: ProductQueryParams = {
      page: queryParams.page,
      pageSize: queryParams.pageSize,
    }
    if (queryParams.keyword) params.keyword = queryParams.keyword
    if (queryParams.status !== '') params.status = queryParams.status
    if (queryParams.categoryId) params.categoryId = queryParams.categoryId
    if (queryParams.tagId) params.tagId = queryParams.tagId
    if (queryParams.priceMin) params.priceMin = queryParams.priceMin
    if (queryParams.priceMax) params.priceMax = queryParams.priceMax
    if (queryParams.stockMin) params.stockMin = queryParams.stockMin
    if (queryParams.stockMax) params.stockMax = queryParams.stockMax
    if (queryParams.createTimeStart) params.createTimeStart = queryParams.createTimeStart
    if (queryParams.createTimeEnd) params.createTimeEnd = queryParams.createTimeEnd

    const res = await getProductList(params)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch {
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  queryParams.page = 1
  fetchData()
}

function handleReset() {
  queryParams.keyword = ''
  queryParams.status = ''
  queryParams.categoryId = undefined
  queryParams.tagId = undefined
  queryParams.priceMin = undefined
  queryParams.priceMax = undefined
  queryParams.stockMin = undefined
  queryParams.stockMax = undefined
  queryParams.createTimeStart = undefined
  queryParams.createTimeEnd = undefined
  queryParams.page = 1
  fetchData()
}

function handlePageChange(page: number) {
  queryParams.page = page
  fetchData()
}

function handleSizeChange(size: number) {
  queryParams.pageSize = size
  queryParams.page = 1
  fetchData()
}

function handleSelectionChange(selection: SpuItem[]) {
  selectedIds.value = selection.map((item) => item.id!).filter(Boolean)
}

// ========== 操作 ==========
function handleAdd() {
  router.push('/product/add')
}

function handleEdit(row: SpuItem) {
  router.push(`/product/edit/${row.id}`)
}

function handleView(row: SpuItem) {
  router.push(`/product/edit/${row.id}`)
}

function handleSkuConfig(row: SpuItem) {
  router.push(`/product/${row.id}/sku`)
}

function handleInventory(row: SpuItem) {
  router.push(`/product/${row.id}/inventory`)
}

async function handleOnShelf(row: SpuItem) {
  try {
    await ElMessageBox.confirm(`确定上架商品"${row.spuName}"？`, '上架确认', { type: 'info' })
    await onShelfProduct(row.id!)
    ElMessage.success('上架成功')
    await fetchData()
  } catch {
    // 取消操作
  }
}

async function handleOffShelf(row: SpuItem) {
  try {
    await ElMessageBox.confirm(`确定下架商品"${row.spuName}"？`, '下架确认', { type: 'warning' })
    await offShelfProduct(row.id!)
    ElMessage.success('下架成功')
    await fetchData()
  } catch {
    // 取消操作
  }
}

async function handleDelete(row: SpuItem) {
  try {
    await ElMessageBox.confirm(
      `确定要永久删除商品"${row.spuName}"吗？该操作不可恢复！`,
      '删除确认',
      { type: 'warning', confirmButtonText: '确认删除', confirmButtonClass: 'el-button--danger' },
    )
    await deleteProduct(row.id!)
    ElMessage.success('删除成功')
    await fetchData()
  } catch {
    // 取消操作
  }
}

async function handleBatchOnShelf() {
  if (!selectedIds.value.length) {
    ElMessage.warning('请选择要上架的商品')
    return
  }
  try {
    await ElMessageBox.confirm(`确定批量上架 ${selectedIds.value.length} 件商品？`, '批量上架确认')
    await batchOnShelf(selectedIds.value)
    ElMessage.success('批量上架成功')
    await fetchData()
  } catch {
    // 取消操作
  }
}

async function handleBatchOffShelf() {
  if (!selectedIds.value.length) {
    ElMessage.warning('请选择要下架的商品')
    return
  }
  try {
    await ElMessageBox.confirm(`确定批量下架 ${selectedIds.value.length} 件商品？`, '批量下架确认')
    await batchOffShelf(selectedIds.value)
    ElMessage.success('批量下架成功')
    await fetchData()
  } catch {
    // 取消操作
  }
}

async function handleBatchDelete() {
  if (!selectedIds.value.length) {
    ElMessage.warning('请选择要删除的商品')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定批量删除 ${selectedIds.value.length} 件商品？该操作不可恢复！`,
      '批量删除确认',
      { type: 'warning', confirmButtonText: '确认删除', confirmButtonClass: 'el-button--danger' },
    )
    await batchDeleteProducts(selectedIds.value)
    ElMessage.success('批量删除成功')
    await fetchData()
  } catch {
    // 取消操作
  }
}

async function handleExport() {
  try {
    await exportProducts({})
    ElMessage.success('导出成功')
  } catch {
    ElMessage.error('导出失败')
  }
}

function canShowAction(row: SpuItem, action: string): boolean {
  switch (action) {
    case 'edit':
      return row.status === ProductStatus.DRAFT || row.status === ProductStatus.OFF_SHELF
    case 'view':
      return row.status === ProductStatus.ON_SHELF || row.status === ProductStatus.PENDING || row.status === ProductStatus.REJECTED
    case 'on-shelf':
      return row.status === ProductStatus.OFF_SHELF
    case 'off-shelf':
      return row.status === ProductStatus.ON_SHELF
    case 'audit':
      return row.status === ProductStatus.PENDING || row.status === ProductStatus.REJECTED
    case 'delete':
      return row.status === ProductStatus.OFF_SHELF
    default:
      return true
  }
}
</script>

<template>
  <div class="page-container">
    <!-- 筛选栏 -->
    <div class="filter-bar">
      <el-form :model="queryParams" inline label-width="0">
        <el-row :gutter="12" align="middle">
          <el-col :span="5">
            <el-input v-model="queryParams.keyword" placeholder="商品名称/SPU编码/SKU编码" clearable @keyup.enter="handleQuery" />
          </el-col>
          <el-col :span="3">
            <el-select v-model="queryParams.status" placeholder="商品状态" clearable style="width: 100%">
              <el-option v-for="opt in statusOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
            </el-select>
          </el-col>
          <el-col :span="3">
            <el-cascader
              v-model="queryParams.categoryId"
              :options="categoryTree"
              :props="{ value: 'id', label: 'catName', children: 'children', checkStrictly: true, emitPath: false }"
              placeholder="商品分类"
              clearable
              style="width: 100%"
            />
          </el-col>
          <el-col :span="3">
            <el-select v-model="queryParams.tagId" placeholder="商品标签" clearable style="width: 100%">
              <el-option v-for="tag in tagList" :key="tag.id" :label="tag.tagName" :value="tag.id" />
            </el-select>
          </el-col>
          <el-col :span="3">
            <el-input-number v-model="queryParams.priceMin" :min="0" placeholder="最低价" style="width: 100%" :controls="false" />
          </el-col>
          <el-col :span="3">
            <el-input-number v-model="queryParams.priceMax" :min="0" placeholder="最高价" style="width: 100%" :controls="false" />
          </el-col>
          <el-col :span="4">
            <el-date-picker
              v-model="queryParams.createTimeStart"
              type="daterange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              value-format="YYYY-MM-DD"
              style="width: 100%"
            />
          </el-col>
        </el-row>
        <el-row :gutter="12" class="mt-2">
          <el-col :span="12">
            <el-button type="primary" @click="handleQuery">查询</el-button>
            <el-button @click="handleReset">重置</el-button>
          </el-col>
        </el-row>
      </el-form>
    </div>

    <!-- 操作按钮栏 -->
    <div class="action-bar">
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        新增商品
      </el-button>
      <el-button :disabled="!selectedIds.length" @click="handleBatchOnShelf">批量上架</el-button>
      <el-button :disabled="!selectedIds.length" @click="handleBatchOffShelf">批量下架</el-button>
      <el-button :disabled="!selectedIds.length" @click="handleExport">批量导出</el-button>
      <el-button type="danger" :disabled="!selectedIds.length" @click="handleBatchDelete">批量删除</el-button>
    </div>

    <!-- 表格 -->
    <el-table
      :data="tableData"
      v-loading="loading"
      stripe
      style="width: 100%"
      @selection-change="handleSelectionChange"
      @sort-change="(sort: any) => { handleQuery() }"
    >
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="商品缩略图" width="80" align="center">
        <template #default="{ row }">
          <el-image
            :src="row.images?.[0]?.imageUrl || ''"
            style="width: 48px; height: 48px; border-radius: 4px"
            fit="cover"
          >
            <template #error>
              <div style="width: 48px; height: 48px; background: #f5f7fa; display: flex; align-items: center; justify-content: center; border-radius: 4px">
                <el-icon :size="20"><Picture /></el-icon>
              </div>
            </template>
          </el-image>
        </template>
      </el-table-column>
      <el-table-column prop="spuName" label="商品名称" min-width="180" show-overflow-tooltip />
      <el-table-column prop="spuCode" label="SPU编码" width="150" />
      <el-table-column prop="categoryName" label="商品分类" width="120" />
      <el-table-column prop="minPrice" label="售价" width="100" align="right">
        <template #default="{ row }">
          <span>¥{{ row.minPrice?.toFixed(2) || '0.00' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="totalStock" label="总库存" width="80" align="right" sortable="custom" />
      <el-table-column prop="totalSales" label="销量" width="80" align="right" sortable="custom" />
      <el-table-column label="商品状态" width="100" align="center">
        <template #default="{ row }">
          <StatusTag :status="row.status" />
        </template>
      </el-table-column>
      <el-table-column prop="createdAt" label="创建时间" width="160" sortable="custom" />
      <el-table-column label="操作" width="280" fixed="right">
        <template #default="{ row }">
          <el-button v-if="canShowAction(row, 'edit')" type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button v-if="canShowAction(row, 'view')" type="primary" link size="small" @click="handleView(row)">查看</el-button>
          <el-button v-if="canShowAction(row, 'on-shelf')" type="success" link size="small" @click="handleOnShelf(row)">上架</el-button>
          <el-button v-if="canShowAction(row, 'off-shelf')" type="warning" link size="small" @click="handleOffShelf(row)">下架</el-button>
          <el-button type="primary" link size="small" @click="handleSkuConfig(row)">规格</el-button>
          <el-button type="primary" link size="small" @click="handleInventory(row)">库存</el-button>
          <el-button v-if="canShowAction(row, 'delete')" type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <div class="pagination-wrapper">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </div>
  </div>
</template>

<style scoped>
.filter-bar {
  margin-bottom: 16px;
}

.mt-2 {
  margin-top: 12px;
}

.action-bar {
  margin-bottom: 16px;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
