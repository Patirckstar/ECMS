<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const isCollapse = ref(false)

const menuItems = [
  {
    path: '/product',
    icon: 'Goods',
    label: '商品管理',
    children: [
      { path: '/product', icon: 'List', label: '商品列表' },
      { path: '/product/add', icon: 'Plus', label: '新增商品' },
    ],
  },
]

const breadcrumbMap: Record<string, string> = {
  '/product': '商品列表',
  '/product/add': '新增商品',
}

const breadcrumb = computed(() => {
  const path = route.path
  // 编辑页面显示为"编辑商品"
  if (path.startsWith('/product/edit')) return '编辑商品'
  if (path.startsWith('/product/sku')) return 'SKU规格配置'
  if (path.startsWith('/product/inventory')) return '库存管理'
  return breadcrumbMap[path] || '商品管理'
})

function handleMenuSelect(index: string) {
  router.push(index)
}
</script>

<template>
  <el-container class="main-container">
    <!-- 左侧菜单 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="main-aside">
      <div class="logo-area" @click="router.push('/')">
        <span v-if="!isCollapse" class="logo-text">ECMS 管理系统</span>
        <span v-else class="logo-text-mini">EC</span>
      </div>

      <el-menu
        :default-active="route.path"
        :collapse="isCollapse"
        :collapse-transition="false"
        router
        class="main-menu"
        @select="handleMenuSelect"
      >
        <template v-for="item in menuItems" :key="item.path">
          <el-sub-menu v-if="item.children" :index="item.path">
            <template #title>
              <el-icon><component :is="item.icon" /></el-icon>
              <span>{{ item.label }}</span>
            </template>
            <el-menu-item v-for="child in item.children" :key="child.path" :index="child.path">
              <el-icon><component :is="child.icon" /></el-icon>
              <span>{{ child.label }}</span>
            </el-menu-item>
          </el-sub-menu>
          <el-menu-item v-else :index="item.path">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.label }}</span>
          </el-menu-item>
        </template>
      </el-menu>
    </el-aside>

    <el-container>
      <!-- 顶部导航 -->
      <el-header class="main-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse" style="cursor: pointer; font-size: 20px">
            <component :is="isCollapse ? 'Expand' : 'Fold'" />
          </el-icon>
          <el-breadcrumb separator="/" class="breadcrumb">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="breadcrumb !== '商品管理'" :to="{ path: '/product' }">商品管理</el-breadcrumb-item>
            <el-breadcrumb-item>{{ breadcrumb }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown trigger="click">
            <span class="user-info">
              <el-avatar :size="32" icon="UserFilled" />
              <span class="username">管理员</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>个人中心</el-dropdown-item>
                <el-dropdown-item divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 主体内容 -->
      <el-main class="main-content">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.main-container {
  height: 100vh;
  overflow: hidden;
}

.main-aside {
  background-color: #304156;
  transition: width 0.3s;
  overflow: hidden;
}

.logo-area {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-text {
  color: #fff;
  font-size: 18px;
  font-weight: 600;
  letter-spacing: 2px;
}

.logo-text-mini {
  color: #fff;
  font-size: 16px;
  font-weight: 600;
}

.main-menu {
  border-right: none;
  background-color: #304156;
}

.main-menu:not(.el-menu--collapse) {
  width: 220px;
}

.main-menu .el-sub-menu,
.main-menu .el-menu-item {
  background-color: #304156 !important;
}

.main-menu .el-sub-menu__title {
  color: #ffffff !important;
}

.main-menu .el-sub-menu__title span,
.main-menu .el-sub-menu__title .el-icon {
  color: #ffffff !important;
}

.main-menu .el-menu-item span,
.main-menu .el-menu-item .el-icon {
  color: #ffffff !important;
}

.main-menu .el-menu-item:hover,
.main-menu .el-sub-menu__title:hover {
  background-color: #409EFF !important;
}

.main-menu .el-menu-item.is-active {
  background-color: #409EFF !important;
  color: #fff;
}

.main-header {
  height: 60px;
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.breadcrumb {
  font-size: 14px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.username {
  font-size: 14px;
  color: #333;
}

.main-content {
  background-color: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
  height: calc(100vh - 60px);
}
</style>
