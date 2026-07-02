<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getSkuInventory, adjustStock, getInventoryLogs, getStatusLogs, getAuditLogs, getProductDetail } from '@/api/product'
import type { SpuItem, SkuItem, InventoryLog, StatusLog, AuditLog } from '@/types/product'
import { ChangeTypeLabel, ProductStatusLabel } from '@/types/product'
import StatusTag from '@/components/product/StatusTag.vue'

const route = useRoute()
const router = useRouter()
const spuId = computed(() => Number(route.params.id))
const loading = ref(false)

// ========== 数据 ==========
const productInfo = ref<SpuItem | null>(null)
const skuInventory = ref<SkuItem[]>([])
const inventoryLogs = ref<InventoryLog[]>([])
const statusLogs = ref<StatusLog[]>([])
const auditLogs = ref<AuditLog[]>([])

const logTotal = ref(0)
const logPage = ref(1)
const logPageSize = ref(20)

const activeTab = ref('inventory')

// 库存调整弹窗
const adjustDialogVisible = ref(false)
const adjustSku = ref<SkuItem | null>(null)
const adjustQty = ref(0)
const adjustRemark = ref('')

// 日志筛选
const logFilterType = ref<number | undefined>(undefined)
const changeTypeOptions = Object.entries(ChangeTypeLabel).map(([value, label]) => ({
  value: Number(value),
  label,
}))

// ========== 计算属性 ==========
const totalStock = computed(() => skuInventory.value.reduce((sum, sku) => sum + sku.stock, 0))
const onSaleSkuCount = computed(() => skuInventory.value.filter((s) => s.status === 1).length)
const outOfStockCount = computed(() => skuInventory.value.filter((s) => s.stock <= 0).length)

// ========== 生命周期 ==========
onMounted(async () => {
  await loadData()
})

async function loadData() {
  loading.value = true
  try {
    const [productRes, inventoryRes] = await Promise.all([
      getProductDetail(spuId.value),
      getSkuInventory(spuId.value),
    ])
    productInfo.value = productRes.data
    skuInventory.value = inventoryRes.data || []
  } catch {
    ElMessage.warning('数据加载失败')
  } finally {
    loading.value = false
  }
}

// ========== 库存调整 ==========
function handleOpenAdjust(sku: SkuItem) {
  adjustSku.value = { ...sku }
  adjustQty.value = 0
  adjustRemark.value = ''
  adjustDialogVisible.value = true
}

async function handleConfirmAdjust() {
  if (!adjustSku.value || !adjustQty.value) {
    ElMessage.warning('请输入调整数量')
    return
  }
  try {
    await adjustStock(spuId.value, adjustSku.value.id!, adjustQty.value, adjustRemark.value)
    ElMessage.success('库存调整成功')
    adjustDialogVisible.value = false
    await loadData()
  } catch {
    ElMessage.error('调整失败')
  }
}

// ========== Tab切换 ==========
async function handleTabChange(tab: string) {
  activeTab.value = tab
  if (tab === 'log' && !inventoryLogs.value.length) {
    await loadLogs()
  } else if (tab === 'status' && !statusLogs.value.length) {
    await loadStatusLogs()
  }
}

async function loadLogs() {
  try {
    const res = await getInventoryLogs(spuId.value, {
      page: logPage.value,
      pageSize: logPageSize.value,
      changeType: logFilterType.value,
    })
    inventoryLogs.value = (res.data as any).records || []
    logTotal.value = (res.data as any).total || 0
  } catch {
    inventoryLogs.value = []
  }
}

async function loadStatusLogs() {
  try {
    const [statusRes, auditRes] = await Promise.all([
      getStatusLogs(spuId.value),
      getAuditLogs(spuId.value),
    ])
    statusLogs.value = statusRes.data || []
    auditLogs.value = auditRes.data || []
  } catch {
    statusLogs.value = []
    auditLogs.value = []
  }
}

function getSkuRowClass({ row }: { row: any }) {
  if (row.status === 0) return 'disabled-row'
  return ''
}

function goBack() {
  router.push('/product')
}

function getStockStatus(sku: SkuItem) {
  if (sku.stock <= 0) return { label: '缺货', color: 'danger' }
  if (sku.warnThreshold > 0 && sku.stock <= sku.warnThreshold) return { label: '预警', color: 'warning' }
  return { label: '正常', color: 'success' }
}
</script>

<template>
  <div class="page-container">
    <!-- 顶部信息卡片 -->
    <div class="info-card" v-if="productInfo">
      <el-row :gutter="24">
        <el-col :span="8">
          <div class="info-item">
            <span class="info-label">商品名称：</span>
            <span class="info-value">{{ productInfo.spuName }}</span>
          </div>
        </el-col>
        <el-col :span="4">
          <div class="info-item">
            <span class="info-label">SPU编码：</span>
            <span class="info-value">{{ productInfo.spuCode }}</span>
          </div>
        </el-col>
        <el-col :span="3">
          <div class="info-item">
            <span class="info-label">商品状态：</span>
            <StatusTag :status="productInfo.status" />
          </div>
        </el-col>
        <el-col :span="3">
          <div class="info-item">
            <span class="info-label">总库存：</span>
            <span class="info-value">{{ totalStock }}</span>
          </div>
        </el-col>
        <el-col :span="3">
          <div class="info-item">
            <span class="info-label">在售SKU：</span>
            <span class="info-value">{{ onSaleSkuCount }}</span>
          </div>
        </el-col>
        <el-col :span="3">
          <div class="info-item">
            <span class="info-label">缺货SKU：</span>
            <span class="info-value" style="color: #f56c6c">{{ outOfStockCount }}</span>
          </div>
        </el-col>
      </el-row>
    </div>

    <div class="page-header">
      <el-button @click="goBack">返回列表</el-button>
    </div>

    <!-- Tabs -->
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- Tab 1: 库存概况 -->
      <el-tab-pane label="库存概况" name="inventory">
        <el-table :data="skuInventory" v-loading="loading" stripe border style="width: 100%" :row-class-name="getSkuRowClass">
          <el-table-column label="SKU规格" min-width="180">
            <template #default="{ row }">
              <span v-html="row.specName || row.specInfo"></span>
            </template>
          </el-table-column>
          <el-table-column prop="skuCode" label="SKU编码" width="150" />
          <el-table-column label="可用库存" width="100" align="center">
            <template #default="{ row }">{{ row.stock }}</template>
          </el-table-column>
          <el-table-column label="锁定库存" width="100" align="center">
            <template #default="{ row }">
              <span style="color: #909399">{{ row.lockedStock }}</span>
            </template>
          </el-table-column>
          <el-table-column label="预警阈值" width="100" align="center">
            <template #default="{ row }">{{ row.warnThreshold }}</template>
          </el-table-column>
          <el-table-column label="库存状态" width="100" align="center">
            <template #default="{ row }">
              <el-tag :type="getStockStatus(row).color" size="small">
                {{ getStockStatus(row).label }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120" align="center">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleOpenAdjust(row)">调整库存</el-button>
            </template>
          </el-table-column>
        </el-table>

        <el-empty v-if="!loading && !skuInventory.length" description="暂无库存数据" />
      </el-tab-pane>

      <!-- Tab 2: 库存日志 -->
      <el-tab-pane label="库存日志" name="log">
        <div class="log-filter">
          <el-select v-model="logFilterType" placeholder="变动类型" clearable style="width: 150px" @change="loadLogs">
            <el-option v-for="opt in changeTypeOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
          </el-select>
        </div>

        <el-table :data="inventoryLogs" v-loading="loading" stripe border style="width: 100%">
          <el-table-column label="变动时间" prop="createdAt" width="170" />
          <el-table-column label="操作人" width="120">
            <template #default="{ row }">{{ row.operatorName || '-' }}</template>
          </el-table-column>
          <el-table-column label="SKU信息" min-width="180">
            <template #default="{ row }">{{ row.specName || row.skuCode }}</template>
          </el-table-column>
          <el-table-column label="变动数量" width="100" align="center">
            <template #default="{ row }">
              <span :style="{ color: row.changeQty > 0 ? '#67C23A' : '#F56C6C', fontWeight: 600 }">
                {{ row.changeQty > 0 ? '+' : '' }}{{ row.changeQty }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="变动类型" width="120" align="center">
            <template #default="{ row }">
              <el-tag size="small">{{ ChangeTypeLabel[row.changeType] || '未知' }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="变动前/后" width="120" align="center">
            <template #default="{ row }">
              {{ row.beforeStock }} → {{ row.afterStock }}
            </template>
          </el-table-column>
          <el-table-column label="备注" prop="remark" min-width="150" show-overflow-tooltip />
        </el-table>

        <div v-if="logTotal > 0" class="pagination-wrapper">
          <el-pagination
            v-model:current-page="logPage"
            v-model:page-size="logPageSize"
            :total="logTotal"
            layout="total, prev, pager, next"
            background
            @current-change="loadLogs"
          />
        </div>
      </el-tab-pane>

      <!-- Tab 3: 状态流转 -->
      <el-tab-pane label="状态流转" name="status">
        <div class="timeline-container">
          <!-- 状态流转 -->
          <h4 class="timeline-title">状态变更记录</h4>
          <el-timeline v-if="statusLogs.length">
            <el-timeline-item
              v-for="log in statusLogs"
              :key="log.id"
              :timestamp="log.createdAt"
              placement="top"
            >
              <div class="timeline-content">
                <StatusTag :status="log.fromStatus" />
                <el-icon style="margin: 0 8px"><Right /></el-icon>
                <StatusTag :status="log.toStatus" />
                <span style="margin-left: 12px; color: #909399; font-size: 13px">
                  操作人：{{ log.operatorName || '-' }}
                </span>
                <span v-if="log.remark" style="margin-left: 12px; color: #606266">
                  （{{ log.remark }}）
                </span>
              </div>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无状态变更记录" />

          <!-- 审核记录 -->
          <h4 class="timeline-title" style="margin-top: 32px">审核记录</h4>
          <el-timeline v-if="auditLogs.length">
            <el-timeline-item
              v-for="log in auditLogs"
              :key="log.id"
              :timestamp="log.auditTime"
              placement="top"
              :color="log.auditResult === 1 ? '#67C23A' : '#F56C6C'"
            >
              <div class="timeline-content">
                <el-tag :type="log.auditResult === 1 ? 'success' : 'danger'" size="small">
                  {{ log.auditResult === 1 ? '审核通过' : '审核驳回' }}
                </el-tag>
                <span style="margin-left: 12px; color: #909399; font-size: 13px">
                  审核人：{{ log.auditorName || '-' }}
                </span>
                <span v-if="log.rejectReason" style="margin-left: 12px; color: #F56C6C">
                  原因：{{ log.rejectReason }}
                </span>
              </div>
            </el-timeline-item>
          </el-timeline>
          <el-empty v-else description="暂无审核记录" />
        </div>
      </el-tab-pane>
    </el-tabs>

    <!-- 库存调整弹窗 -->
    <el-dialog v-model="adjustDialogVisible" title="调整库存" width="420px">
      <el-form v-if="adjustSku" label-width="100px">
        <el-form-item label="SKU规格">
          <span>{{ adjustSku.specName || adjustSku.specInfo }}</span>
        </el-form-item>
        <el-form-item label="当前库存">
          <span style="font-weight: 600">{{ adjustSku.stock }}</span>
        </el-form-item>
        <el-form-item label="调整数量">
          <el-input-number v-model="adjustQty" placeholder="正数入库，负数出库" style="width: 100%" />
          <div style="font-size: 12px; color: #909399; margin-top: 4px">正数 = 增加库存，负数 = 减少库存</div>
        </el-form-item>
        <el-form-item label="调整原因">
          <el-input v-model="adjustRemark" placeholder="选填" maxlength="200" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmAdjust">确认调整</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.info-card {
  background: #f5f7fa;
  border-radius: 8px;
  padding: 16px 20px;
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 4px;
  line-height: 32px;
}

.info-label {
  font-size: 13px;
  color: #909399;
  white-space: nowrap;
}

.info-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.page-header {
  display: flex;
  justify-content: flex-end;
  margin-bottom: 16px;
}

.log-filter {
  margin-bottom: 16px;
}

.pagination-wrapper {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}

.timeline-container {
  padding: 8px 0;
}

.timeline-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.timeline-content {
  display: flex;
  align-items: center;
}

:deep(.disabled-row) {
  opacity: 0.5;
  background-color: #fafafa;
}

:deep(.disabled-row:hover) {
  cursor: not-allowed;
}
</style>
