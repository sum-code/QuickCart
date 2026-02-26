import axios from 'axios'
import { getPersistedAccessToken, useAuthStore } from '../store/useAuthStore'

const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
})

apiClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken || getPersistedAccessToken()

  // Let browser set multipart boundaries automatically for FormData requests.
  if (config.data instanceof FormData && config.headers) {
    delete config.headers['Content-Type']
  }

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error?.response?.status
    const requestUrl = error?.config?.url || ''
    const isProtectedEndpoint =
      requestUrl.startsWith('/v1/admin') || requestUrl.startsWith('/admin') || requestUrl.startsWith('/user')

    if (status === 401 && isProtectedEndpoint) {
      useAuthStore.getState().clearAuth()
    }

    return Promise.reject(error)
  },
)

export default apiClient
