import { clearAuth, getToken } from './auth'

const BASE_URL = import.meta.env.VITE_API_BASE || 'http://localhost:8080'

export const request = async (path, options = {}) => {
  const headers = options.headers ? { ...options.headers } : {}
  const token = getToken()
  if (token) {
    headers.Authorization = `Bearer ${token}`
  }
  if (!headers['Content-Type'] && options.body) {
    headers['Content-Type'] = 'application/json'
  }
  const resp = await fetch(`${BASE_URL}${path}`, {
    ...options,
    headers
  })
  const data = await resp.json().catch(() => null)
  if (!data) {
    throw new Error('服务响应异常')
  }
  if (data.code === 401) {
    clearAuth()
  }
  if (data.code !== 0) {
    throw new Error(data.message || '请求失败')
  }
  return data.data
}
