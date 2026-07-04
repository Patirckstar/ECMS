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
    marketPrice?: number
    salePrice?: number
    memberPrice?: number
    images?: SpuImage[]
    tags?: Tag[]
    tagsIds?: number[]
    minPrice?: number
    totalStock?: number
    totalSales?: number
}

export interface SpuImage {
    id?: number
    spuId?: number
    imageUrl: string
    sortOrder?: number
    createdAt?: string
}

export interface SkuItem {
    id?: number
    skuCode?: string
    spuId?: number
    specInfo: string
    specInfoObj?: Record<string, string>
    specName?: string
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

export interface Category {
    id: number
    catName: string
    parentId: number
    level: number
    sortOrder: number
    children?: Category[]
}

export interface Brand {
    id: number
    brandName: string
    brandLogo?: string
    brandDesc?: string
    status?: number
}

export interface Tag {
    id: number
    tagName: string
    tagColor?: string
    sortOrder?: number
    status?: number
}

export interface ProductQueryParams {
    page: number
    pageSize: number
    keyword?: string
    status?: number | string
    categoryId?: number
    tagId?: number
    priceMin?: number
    priceMax?: number
    stockMin?: number
    stockMax?: number
    createTimeStart?: string
    createTimeEnd?: string
    sortField?: string
    sortOrder?: string
}

export enum ProductType {
    PHYSICAL = 1,
    VIRTUAL = 2,
}

export enum ProductStatus {
    DRAFT = 0,
    PENDING = 1,
    ON_SHELF = 2,
    OFF_SHELF = 3,
    REJECTED = 4,
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

export interface ApiResult<T> {
    code: number
    message: string
    data: T
}

export interface PageResult<T> {
    records: T[]
    total: number
    page?: number
    pageSize?: number
}

export const ChangeTypeLabel: Record<number, string> = {
    1: '入库',
    2: '出库',
}

export interface InventoryLog {
    id?: number
    skuId?: number
    spuId?: number
    changeType?: number
    changeQty?: number
    beforeStock?: number
    afterStock?: number
    operatorId?: number
    remark?: string
    createdAt?: string
}

export interface StatusLog {
    id?: number
    spuId?: number
    fromStatus?: number
    toStatus?: number
    operatorId?: number
    operatorName?: string
    remark?: string
    createdAt?: string
}

export interface AuditLog {
    id?: number
    spuId?: number
    auditResult?: number
    auditorId?: number
    auditorName?: string
    rejectReason?: string
    auditTime?: string
}
