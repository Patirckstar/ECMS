import axios from 'axios'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080',
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
          window.location.href = '/login'
          break
        case 500:
          console.error('服务器错误')
          break
        default:
          console.error(error.response.data?.message || '请求失败')
      }
    } else {
      console.error('网络错误')
    }
    return Promise.reject(error)
  },
)

export default request
