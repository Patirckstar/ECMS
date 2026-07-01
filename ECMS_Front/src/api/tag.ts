import request from '@/utils/request'
import type { ApiResult, Tag } from '@/types/product'

const BASE_URL = '/api/tags'

/**
 * 获取标签列表
 */
export function getTagList() {
  return request.get<any, ApiResult<Tag[]>>(BASE_URL)
}
