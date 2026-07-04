import request from '@/utils/request'
import type { ApiResult, PageResult, ProductQueryParams, SpuItem, SkuItem, InventoryLog, StatusLog, AuditLog } from '@/types/product'

const BASE_URL = '/api/products'

/**
 * 分页查询商品列表
 */
export function getProductList(params: ProductQueryParams) {
  return request.get<any, ApiResult<PageResult<SpuItem>>>(BASE_URL, { params })
}

/**
 * 获取商品详情（含SKU）
 */
export function getProductDetail(id: number) {
  return request.get<any, ApiResult<SpuItem>>(`${BASE_URL}/${id}`)
}

/**
 * 新增商品
 */
export function createProduct(data: Partial<SpuItem>) {
  return request.post<any, ApiResult<SpuItem>>(BASE_URL, data)
}

/**
 * 更新商品
 */
export function updateProduct(id: number, data: Partial<SpuItem>) {
  return request.put<any, ApiResult<SpuItem>>(`${BASE_URL}/${id}`, data)
}

/**
 * 删除商品
 */
export function deleteProduct(id: number) {
  return request.delete<any, ApiResult<null>>(`${BASE_URL}/${id}`)
}

/**
 * 批量删除商品
 */
export function batchDeleteProducts(ids: number[]) {
  return request.post<any, ApiResult<null>>(`${BASE_URL}/batch-delete`, { ids })
}

/**
 * 上架商品
 */
export function onShelfProduct(id: number) {
  return request.put<any, ApiResult<null>>(`${BASE_URL}/${id}/on-shelf`)
}

/**
 * 下架商品
 */
export function offShelfProduct(id: number) {
  return request.put<any, ApiResult<null>>(`${BASE_URL}/${id}/off-shelf`)
}

/**
 * 批量上架
 */
export function batchOnShelf(ids: number[]) {
  return request.post<any, ApiResult<string>>(`${BASE_URL}/batch-on-shelf`, { ids })
}

/**
 * 批量下架
 */
export function batchOffShelf(ids: number[]) {
  return request.post<any, ApiResult<string>>(`${BASE_URL}/batch-off-shelf`, { ids })
}

/**
 * 获取商品SKU列表
 */
export function getSkuList(spuId: number) {
  return request.get<any, ApiResult<SkuItem[]>>(`${BASE_URL}/${spuId}/skus`)
}

/**
 * 更新SKU
 */
export function updateSku(spuId: number, skuId: number, data: Partial<SkuItem>) {
  return request.put<any, ApiResult<SkuItem>>(`${BASE_URL}/${spuId}/skus/${skuId}`, data)
}

/**
 * 新增SKU
 */
export function createSku(spuId: number, data: Partial<SkuItem>) {
  return request.post<any, ApiResult<SkuItem>>(`${BASE_URL}/${spuId}/skus`, data)
}

/**
 * 批量更新SKU
 */
export function batchUpdateSkus(spuId: number, skus: Partial<SkuItem>[]) {
  return request.put<any, ApiResult<null>>(`${BASE_URL}/${spuId}/skus/batch`, { skus })
}

/**
 * 获取库存日志
 */
export function getInventoryLogs(spuId: number, params: { page: number; pageSize: number; changeType?: number }) {
  return request.get<any, ApiResult<{ records: InventoryLog[]; total: number }>>(`${BASE_URL}/${spuId}/inventory-logs`, { params })
}

/**
 * 获取状态流转记录
 */
export function getStatusLogs(spuId: number) {
  return request.get<any, ApiResult<StatusLog[]>>(`${BASE_URL}/${spuId}/status-logs`)
}

/**
 * 获取审核记录
 */
export function getAuditLogs(spuId: number) {
  return request.get<any, ApiResult<AuditLog[]>>(`${BASE_URL}/${spuId}/audit-logs`)
}

/**
 * 按SPU获取所有SKU库存信息
 */
export function getSkuInventory(spuId: number) {
  return request.get<any, ApiResult<SkuItem[]>>(`${BASE_URL}/${spuId}/inventory`)
}

/**
 * 调整SKU库存
 */
export function adjustStock(spuId: number, skuId: number, qty: number, remark?: string) {
  return request.post<any, ApiResult<null>>(`${BASE_URL}/${spuId}/skus/${skuId}/adjust-stock`, { qty, remark })
}

/**
 * 提交审核
 */
export function submitAuditProduct(id: number) {
  return request.put<any, ApiResult<null>>(`${BASE_URL}/${id}/submit-audit`)
}

/**
 * 审核通过
 */
export function auditApproveProduct(id: number) {
  return request.put<any, ApiResult<null>>(`${BASE_URL}/${id}/audit/approve`)
}

/**
 * 审核驳回
 */
export function auditRejectProduct(id: number, reason: string) {
  return request.put<any, ApiResult<null>>(`${BASE_URL}/${id}/audit/reject`, { rejectReason: reason })
}

/**
 * 导出商品列表Excel
 */
export function exportProducts(params: Partial<ProductQueryParams>) {
  return request.get<any, Blob>(`${BASE_URL}/export`, { params, responseType: 'blob' })
}
