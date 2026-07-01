// ============================================================
// 商品管理模块 - TypeScript 类型定义
// ============================================================

// 商品状态枚举
export enum ProductStatus {
    DRAFT = 0,       // 草稿
    PENDING = 1,     // 待审核
    ON_SHELF = 2,    // 已上架
    OFF_SHELF = 3,   // 已下架
    REJECTED = 4,    // 审核驳回
}

export const ProductStatusLabel: Record<number, string> = {
    [ProductStatus.DRAFT]: '草稿',
    [ProductStatus.PENDING]: '待审核',
    [ProductStatus.ON_SHELF]: '已上架',
    [ProductStatus.OFF_SHELF]: '已下架',
    [ProductStatus.REJECTED]: '审核驳回',
}

export const ProductStatusColor: Record<number, string> = {
    [ProductStatus.DRAFT]: '#909399',
    [ProductStatus.PENDING]: '#E6A23C',
    [ProductStatus.ON_SHELF]: '#67C23A',
    [ProductStatus.OFF_SHELF]: '#C0C4CC',
    [ProductStatus.REJECTED]: '#F56C6C',
}

// 商品类型
export enum ProductType {
    PHYSICAL = 1,  // 实物商品
    VIRTUAL = 2,   // 虚拟商品
}

export const ProductTypeLabel: Record<number, string> = {
    [ProductType.PHYSICAL]: '实物商品',
    [ProductType.VIRTUAL]: '虚拟商品',
}

// 标签类型
export interface Tag {
    id: number
    tagName: string
    tagType: 1 | 2  // 1-系统固定, 2-自定义
    tagColor: string
    sortOrder: number
    status: 0 | 1
}

// 品牌
export interface Brand {
    id: number
    brandName: string
    brandLogo?: string
    brandDesc?: string
    sortOrder: number
    status: 0 | 1
}

// 分类
export interface Category {
    id: number
    parentId: number
    level: 1 | 2 | 3
    catName: string
    catIcon?: string
    sortOrder: number
    status: 0 | 1
    children?: Category[]
}

// 规格模板
export interface SpecTemplate {
    id: number
    specName: string
    sortOrder: number
    values: SpecValue[]
}

export interface SpecValue {
    id: number
    specId: number
    valueName: string
    sortOrder: number
    selected?: boolean  // 前端临时标记是否选中
}

// SPU 商品
export interface SpuItem {
    id?: number
    spuCode?: string
    spuName: string
    subTitle?: string
    description?: string
    brandId?: number
    brandName?: string
    categoryId: number
    categoryName?: string
    productType: ProductType
    status: ProductStatus
    isDeleted?: 0 | 1
    deletedAt?: string
    sevenDayReturn: 0 | 1
    warrantyDesc?: string
    returnPolicy?: string
    freightTemplateId?: number
    isFreeShipping: 0 | 1
    shipFrom?: string
    shipHours?: number
    auditStatus?: number | null
    auditorId?: number
    auditTime?: string
    rejectReason?: string
    autoOffline: 0 | 1
    createdBy?: number
    createdAt?: string
    updatedAt?: string
    // 扩展字段（表格展示用）
    images?: SpuImage[]
    tags?: Tag[]
    tagsIds?: number[]
    minPrice?: number
    totalStock?: number
    totalSales?: number
}

// SPU 图片
export interface SpuImage {
    id?: number
    spuId?: number
    imageType: 1 | 2 | 3  // 1-主图, 2-详情图, 3-视频封面
    imageUrl: string
    videoUrl?: string
    sortOrder: number
    isCover: 0 | 1
}

// SKU
export interface SkuItem {
    id?: number
    skuCode?: string
    spuId?: number
    specInfo: string  // JSON string: {"颜色":"红色","尺寸":"M"}
    specInfoObj?: Record<string, string>  // 解析后的对象
    specName?: string  // 拼接显示名
    marketPrice: number
    salePrice: number
    memberPrice: number
    activityPrice?: number
    priceStartTime?: string
    priceEndTime?: string
    stock: number
    lockedStock: number
    warnThreshold: number
    status: 0 | 1
    createdAt?: string
    updatedAt?: string
}

// 库存日志
export interface InventoryLog {
    id: number
    skuId: number
    skuCode?: string
    specName?: string
    spuId: number
    changeType: 1 | 2 | 3 | 4 | 5 | 6
    changeTypeLabel?: string
    changeQty: number
    beforeStock: number
    afterStock: number
    operatorId?: number
    operatorName?: string
    remark?: string
    createdAt: string
}

export const ChangeTypeLabel: Record<number, string> = {
    1: '新增入库',
    2: '手动调整',
    3: '下单锁定',
    4: '订单释放',
    5: '订单扣减',
    6: '批量调整',
}

// 状态流转记录
export interface StatusLog {
    id: number
    spuId: number
    fromStatus: number
    toStatus: number
    fromStatusLabel?: string
    toStatusLabel?: string
    operatorId: number
    operatorName?: string
    remark?: string
    createdAt: string
}

// 审核记录
export interface AuditLog {
    id: number
    spuId: number
    auditResult: 1 | 2
    auditorId: number
    auditorName?: string
    rejectReason?: string
    auditTime: string
}

// 价格变动记录
export interface PriceLog {
    id: number
    skuId: number
    priceType: string
    beforePrice: number
    afterPrice: number
    operatorId: number
    operatorName?: string
    createdAt: string
}

// 分页结果
export interface PageResult<T> {
    records: T[]
    total: number
    page: number
    pageSize: number
}

// 分页查询参数
export interface PageParams {
    page: number
    pageSize: number
}

// 商品列表查询参数
export interface ProductQueryParams extends PageParams {
    keyword?: string
    status?: ProductStatus | ''
    categoryId?: number
    tagId?: number
    priceMin?: number
    priceMax?: number
    stockMin?: number
    stockMax?: number
    createTimeStart?: string
    createTimeEnd?: string
    sortField?: string
    sortOrder?: 'asc' | 'desc'
}

// API 响应格式
export interface ApiResult<T> {
    code: number
    message: string
    data: T
}
