<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { createProduct, updateProduct, getProductDetail } from '@/api/product'
import { getCategoryTree } from '@/api/category'
import { getBrandList } from '@/api/brand'
import { getTagList } from '@/api/tag'
import type { Category, Brand, Tag, SpuItem } from '@/types/product'
import { ProductType } from '@/types/product'

const route = useRoute()
const router = useRouter()
const isEdit = computed(() => !!route.params.id)
const productId = computed(() => Number(route.params.id))

// ========== 表单数据 ==========
const activeStep = ref(0)
const formRef = ref()
const loading = ref(false)

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

// 下拉数据
const categoryTree = ref<Category[]>([])
const brandList = ref<Brand[]>([])
const tagList = ref<Tag[]>([])

// ========== 生命周期 ==========
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
      tagIds: data.tagsIds || [],
      isFreeShipping: data.isFreeShipping,
      shipFrom: data.shipFrom || '',
      shipHours: data.shipHours,
      sevenDayReturn: data.sevenDayReturn,
      warrantyDesc: data.warrantyDesc || '',
      returnPolicy: data.returnPolicy || '',
      autoOffline: data.autoOffline,
    })
  } catch {
    ElMessage.error('加载商品信息失败')
  } finally {
    loading.value = false
  }
}

// ========== 表单校验 ==========
const rules = {
  spuName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择商品分类', trigger: 'change' }],
}

// ========== 步骤 ==========
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
    if (isEdit.value) {
      await updateProduct(productId.value, { ...formData } as any)
      ElMessage.success('保存成功')
    } else {
      const res = await createProduct({ ...formData } as any)
      ElMessage.success('创建成功')
      router.push(`/product/edit/${res.data.id}`)
    }
  } catch {
    ElMessage.error('操作失败')
  } finally {
    loading.value = false
  }
}

// ========== 跳转SKU配置 ==========
function goToSkuConfig() {
  if (productId.value) {
    router.push(`/product/${productId.value}/sku`)
  }
}
</script>

<template>
  <div class="page-container">
    <!-- 步骤条 -->
    <el-steps :active="activeStep" align-center class="form-steps">
      <el-step v-for="(step, idx) in steps" :key="idx" :title="step" />
    </el-steps>

    <el-form ref="formRef" :model="formData" :rules="rules" label-width="120px" class="form-body">
      <!-- Step 1: 基础信息 -->
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

      <!-- Step 2: 素材信息 -->
      <div v-show="activeStep === 1" class="step-content">
        <el-empty description="素材上传功能请参考完整版实现（图片上传组件）" />
      </div>

      <!-- Step 3: 价格物流 -->
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

      <!-- Step 4: 售后配置 -->
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

    <!-- 底部操作栏 -->
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
</style>
