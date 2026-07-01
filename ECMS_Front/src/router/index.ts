import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layout/MainLayout.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      redirect: '/product',
    },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/Login.vue'),
      meta: { title: '登录' },
    },
    {
      path: '/product',
      component: MainLayout,
      children: [
        {
          path: '',
          name: 'product-list',
          component: () => import('@/views/product/ProductList.vue'),
          meta: { title: '商品列表' },
        },
        {
          path: 'add',
          name: 'product-add',
          component: () => import('@/views/product/ProductForm.vue'),
          meta: { title: '新增商品' },
        },
        {
          path: 'edit/:id',
          name: 'product-edit',
          component: () => import('@/views/product/ProductForm.vue'),
          meta: { title: '编辑商品' },
        },
        {
          path: ':id/sku',
          name: 'product-sku',
          component: () => import('@/views/product/SkuConfig.vue'),
          meta: { title: 'SKU规格配置' },
        },
        {
          path: ':id/inventory',
          name: 'product-inventory',
          component: () => import('@/views/product/InventoryManage.vue'),
          meta: { title: '库存管理' },
        },
      ],
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/views/NotFound.vue'),
      meta: { title: '页面不存在' },
    },
  ],
})

// 导航守卫 - 检查登录状态
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
