import request from '@/utils/request'
import type { ApiResult, Brand } from '@/types/product'

const BASE_URL = '/api/brands'

/**
 * 获取品牌列表
 */
export function getBrandList() {
  return request.get<any, ApiResult<Brand[]>>(BASE_URL)
}
