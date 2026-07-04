<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createProduct, updateProduct, getProductDetail, getSkuList } from '@/api/product'
import { getCategoryTree } from '@/api/category'
import { getBrandList } from '@/api/brand'
import { getTagList } from '@/api/tag'
import type { Category, Brand, Tag, SpuItem } from '@/types/product'
import { ProductType } from '@/types/product'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const productId = computed(() => Number(route.params.id))

const activeStep = ref(0)
const formRef = ref()
const loading = ref(false)
const uploading = ref(false)

const formData = reactive({
  spuName: '',
  subTitle: '',
  description: '',
  productType: ProductType.PHYSICAL,
  brandId: undefined as number | undefined,
  categoryId: undefined as number | undefined,
  tagIds: [] as number[],
  marketPrice: 0,
  salePrice: 0,
  memberPrice: 0,
  isFreeShipping: 0 as 0 | 1,
  shipFrom: '',
  shipHours: undefined as number | undefined,
  sevenDayReturn: 0 as 0 | 1,
  warrantyDesc: '',
  returnPolicy: '',
  autoOffline: 0 as 0 | 1,
})

const uploadImages = ref<{ name: string; url: string }[]>([])
const uploadAction = `${import.meta.env.VITE_API_BASE_URL || ''}/api/upload/image`

async function handleFileChange(event: Event) {
  const input = event.target as HTMLInputElement
  if (!input.files || input.files.length === 0) return

  uploading.value = true
  try {
    const files = input.files
    if (!files) return
    for (let i = 0; i < files.length; i++) {
      const file = files[i]
      if (!file) continue
      const formData = new FormData()
      formData.append('file', file)

      const res: any = await axios.post(uploadAction, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })

      const respData = res.data
      const url = respData?.data?.url || respData?.url
      if (url) {
        uploadImages.value.push({ name: file.name, url })
      } else {
        ElMessage.error(`上传 ${file.name} 失败：返回数据异常`)
      }
    }
    ElMessage.success(`成功上传 ${input.files.length} 张图片`)
  } catch (e: any) {
    ElMessage.error('图片上传失败：' + (e.message || '未知错误'))
  } finally {
    uploading.value = false
    input.value = '' // 重置 input，允许重复选择相同文件
  }
}

function handleRemoveImage(index: number) {
  uploadImages.value.splice(index, 1)
}

function getImageUrl(url: string) {
  if (url.startsWith('http')) return url
  return `${import.meta.env.VITE_API_BASE_URL || ''}${url}`
}

const categoryTree = ref<Category[]>([])
const brandList = ref<Brand[]>([])
const tagList = ref<Tag[]>([])

onMounted(async () => {
  try {
    const [catRes, brandRes, tagRes] = await Promise.all([
      getCategoryTree(),
      getBrandList(),
      getTagList(),
    ])
    categoryTree.value = catRes.data
    brandList.value = brandRes.data
    tagList.value = tagRes.data
  } catch {
    // 接口未就绪
  }

  if (isEdit.value && productId.value) {
    await loadProductDetail()
  }
})

async function loadProductDetail() {
  loading.value = true
  try {
    const res = await getProductDetail(productId.value)
    const data = res.data
    Object.assign(formData, {
      spuName: data.spuName,
      subTitle: data.subTitle || '',
      description: data.description || '',
      productType: data.productType,
      brandId: data.brandId,
      categoryId: data.categoryId,
      tagIds: data.tags?.map((t: any) => t.id) || data.tagIds || [],
      isFreeShipping: data.isFreeShipping,
      shipFrom: data.shipFrom || '',
      shipHours: data.shipHours,
      sevenDayReturn: data.sevenDayReturn,
      warrantyDesc: data.warrantyDesc || '',
      returnPolicy: data.returnPolicy || '',
      autoOffline: data.autoOffline,
    })

    if (data.images && data.images.length > 0) {
      uploadImages.value = data.images.map((img: any) => ({
        name: img.imageUrl?.split('/').pop() || img.imageUrl,
        url: img.imageUrl,
      }))
    }

    const skuRes = await getSkuList(productId.value)
    const skus = skuRes.data || []
    if (skus.length > 0) {
      formData.marketPrice = skus[0].marketPrice || 0
      formData.salePrice = skus[0].salePrice || 0
      formData.memberPrice = skus[0].memberPrice || 0
    }
  } catch {
    ElMessage.error('加载商品信息失败')
  } finally {
    loading.value = false
  }
}

const rules = {
  spuName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
}

const steps = ['基础信息', '素材信息', '价格物流', '售后配置']

async function handleNext() {
  if (activeStep.value === 0) {
    const valid = await formRef.value.validate().catch(() => false)
    if (!valid) return
  }
  if (activeStep.value < steps.length - 1) {
    activeStep.value++
  }
}

function handlePrev() {
  if (activeStep.value > 0) {
    activeStep.value--
  }
}

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const submitData: any = {
      ...formData,
      images: uploadImages.value.map((img, index) => ({
        imageUrl: img.url,
        imageType: 1,
        isCover: index === 0 ? 1 : 0,
        sortOrder: index,
      })),
    }

    if (isEdit.value) {
      await updateProduct(productId.value, submitData)
      ElMessage.success('保存成功')
    } else {
      const res = await createProduct(submitData)
      if (res.code !== 200) {
        ElMessage.error(res.message || '创建失败')
        return
      }
      if (!res.data || !res.data.id) {
        ElMessage.error('创建失败：未返回商品ID')
        return
      }
      ElMessage.success('创建成功')
      router.push(`/product/edit/${res.data.id}`)
    }
  } catch {
    ElMessage.error('操作失败')
  } finally {
    loading.value = false
  }
}

function goToSkuConfig() {
  if (productId.value) {
    router.push(`/product/${productId.value}/sku`)
  }
}
</script>

<template>
  <div class="page-container">
    <el-steps :active="activeStep" align-center class="form-steps">
      <el-step v-for="(step, idx) in steps" :key="idx" :title="step" />
    </el-steps>

    <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" class="form-body">
      <div v-show="activeStep === 0" class="step-content">
        <el-form-item label="商品名称" prop="spuName">
          <el-input v-model="formData.spuName" placeholder="请输入商品名称（2-60字符）" maxlength="60" show-word-limit style="width: 500px" />
        </el-form-item>
        <el-form-item label="副标题" prop="subTitle">
          <el-input v-model="formData.subTitle" placeholder="请输入副标题" maxlength="200" show-word-limit style="width: 500px" />
        </el-form-item>
        <el-form-item label="商品简介" prop="description">
          <el-input v-model="formData.description" type="textarea" :rows="3" placeholder="请输入商品简介" maxlength="500" show-word-limit style="width: 500px" />
        </el-form-item>
        <el-form-item label="商品类型" prop="productType">
          <el-radio-group v-model="formData.productType">
            <el-radio :value="ProductType.PHYSICAL">实物商品</el-radio>
            <el-radio :value="ProductType.VIRTUAL">虚拟商品</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="品牌" prop="brandId">
          <el-select v-model="formData.brandId" placeholder="请选择品牌" clearable filterable style="width: 300px">
            <el-option v-for="brand in brandList" :key="brand.id" :label="brand.brandName" :value="brand.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="三级分类" prop="categoryId">
          <el-cascader
            v-model="formData.categoryId"
            :options="categoryTree"
            :props="{ value: 'id', label: 'catName', children: 'children', checkStrictly: true, emitPath: false }"
            placeholder="请选择商品分类"
            clearable
            filterable
            style="width: 300px"
          />
        </el-form-item>
        <el-form-item label="商品标签" prop="tagIds">
          <el-select v-model="formData.tagIds" multiple placeholder="请选择标签" style="width: 300px">
            <el-option v-for="tag in tagList" :key="tag.id" :label="tag.tagName" :value="tag.id">
              <span>
                <el-tag :color="tag.tagColor" style="color: #fff; border: none; margin-right: 6px; padding: 0 8px; height: 22px; line-height: 22px">
                  {{ tag.tagName }}
                </el-tag>
              </span>
            </el-option>
          </el-select>
        </el-form-item>
      </div>

      <div v-show="activeStep === 1" class="step-content">
        <h4 class="section-title">商品主图</h4>

        <div class="upload-area">
          <label class="upload-label" :class="{ 'is-uploading': uploading }">
            <input type="file" accept="image/png,image/jpeg,image/gif,image/webp" multiple @change="handleFileChange" />
            <div class="upload-placeholder">
              <el-icon :size="28"><Plus /></el-icon>
              <span>{{ uploading ? '上传中...' : '点击上传图片' }}</span>
            </div>
          </label>

          <div v-for="(img, idx) in uploadImages" :key="idx" class="upload-preview">
            <img :src="getImageUrl(img.url)" :alt="img.name" />
            <div class="upload-preview-actions">
              <el-icon @click="handleRemoveImage(idx)"><Delete /></el-icon>
            </div>
          </div>
        </div>
        <p class="upload-tip">支持 PNG、JPG、GIF、WebP 格式，最多 5 张，建议尺寸 800×800</p>
      </div>

      <div v-show="activeStep === 2" class="step-content">
        <h4 class="section-title">价格信息</h4>
        <el-form-item label="市场价" prop="marketPrice">
          <el-input-number v-model="formData.marketPrice" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="销售价" prop="salePrice">
          <el-input-number v-model="formData.salePrice" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>
        <el-form-item label="会员价" prop="memberPrice">
          <el-input-number v-model="formData.memberPrice" :min="0" :precision="2" style="width: 200px" />
        </el-form-item>

        <template v-if="formData.productType === ProductType.PHYSICAL">
          <h4 class="section-title" style="margin-top: 24px">物流信息</h4>
          <el-form-item label="是否包邮" prop="isFreeShipping">
            <el-switch v-model="formData.isFreeShipping" :active-value="1" :inactive-value="0" />
          </el-form-item>
          <el-form-item label="发货地" prop="shipFrom">
            <el-input v-model="formData.shipFrom" placeholder="如：广东省深圳市" style="width: 300px" />
          </el-form-item>
          <el-form-item label="发货时效" prop="shipHours">
            <el-input-number v-model="formData.shipHours" :min="0" style="width: 200px">
              <template #suffix><span style="font-size: 12px; color: #999">小时</span></template>
            </el-input-number>
          </el-form-item>
        </template>
      </div>

      <div v-show="activeStep === 3" class="step-content">
        <el-form-item label="七天无理由" prop="sevenDayReturn">
          <el-switch v-model="formData.sevenDayReturn" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="质保说明" prop="warrantyDesc">
          <el-input v-model="formData.warrantyDesc" type="textarea" :rows="3" placeholder="请输入质保说明" style="width: 500px" />
        </el-form-item>
        <el-form-item label="退换货规则" prop="returnPolicy">
          <el-input v-model="formData.returnPolicy" type="textarea" :rows="4" placeholder="请输入退换货规则" style="width: 500px" />
        </el-form-item>
        <el-form-item label="缺货自动下架" prop="autoOffline">
          <el-switch v-model="formData.autoOffline" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </div>
    </el-form>

    <div class="form-footer">
      <el-button v-if="activeStep > 0" @click="handlePrev">上一步</el-button>
      <el-button v-if="activeStep < steps.length - 1" type="primary" @click="handleNext">下一步</el-button>
      <el-button v-if="activeStep === steps.length - 1" type="primary" :loading="loading" @click="handleSubmit">
        {{ isEdit ? '保存修改' : '提交审核' }}
      </el-button>
      <el-button v-if="isEdit" @click="goToSkuConfig">配置SKU规格</el-button>
    </div>
  </div>
</template>

<style scoped>
.form-steps {
  margin-bottom: 32px;
  max-width: 700px;
  margin-left: auto;
  margin-right: auto;
}

.form-body {
  max-width: 800px;
  margin: 0 auto;
  min-height: 400px;
}

.step-content {
  padding: 8px 0;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.form-footer {
  margin-top: 32px;
  padding-top: 16px;
  border-top: 1px solid #ebeef5;
  display: flex;
  gap: 12px;
  justify-content: center;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
}

.upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.upload-label {
  width: 148px;
  height: 148px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: border-color 0.2s;
  background: #fafafa;
}

.upload-label:hover {
  border-color: #409eff;
}

.upload-label.is-uploading {
  cursor: not-allowed;
  opacity: 0.6;
}

.upload-label input {
  display: none;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  color: #909399;
  font-size: 12px;
}

.upload-preview {
  width: 148px;
  height: 148px;
  border-radius: 6px;
  overflow: hidden;
  position: relative;
  border: 1px solid #e4e7ed;
}

.upload-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.upload-preview-actions {
  position: absolute;
  top: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  padding: 4px;
  border-radius: 0 6px 0 6px;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s;
}

.upload-preview:hover .upload-preview-actions {
  opacity: 1;
}
</style>
