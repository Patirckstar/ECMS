import request from '@/utils/request'
import type { ApiResult, Category } from '@/types/product'

const BASE_URL = '/api/categories'

/**
 * 获取全部分类（树形结构）
 */
export function getCategoryTree() {
  return request.get<any, ApiResult<Category[]>>(`${BASE_URL}/tree`)
}

/**
 * 获取分类列表（平铺，可传level筛选）
 */
export function getCategoryList(level?: number) {
  return request.get<any, ApiResult<Category[]>>(BASE_URL, { params: { level } })
}
