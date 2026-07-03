import axios from 'axios'
import router from '@/router'

const request = axios.create({
  // !!! 可通过环境变量 VITE_API_BASE_URL 覆盖，或修改下方默认地址 !!!
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://你的服务器IP:8080',
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json',
  },
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    // 从 localStorage 获取 token 并添加到请求头
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  },
)

// 响应拦截器
request.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    // 统一错误处理
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // token 过期，跳转登录页
          localStorage.removeItem('token')
          router.push('/login')
          break
        case 500:
          console.error('服务器错误')
          break
        default:
          console.error(error.response.data?.message || '请求失败')
      }
    } else {
      if (error.code === 'ECONNABORTED') {
        console.error('请求超时')
      } else {
        console.error('网络错误')
      }
    }
    return Promise.reject(error)
  },
)

export default request
