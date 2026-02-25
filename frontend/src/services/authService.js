import apiClient from '../lib/apiClient'

export async function loginRequest(payload) {
  const { data } = await apiClient.post('/auth/login', payload)
  return data
}

export async function registerRequest(payload) {
  const { data } = await apiClient.post('/auth/register', payload)
  return data
}

export async function getCurrentUserRequest() {
  const { data } = await apiClient.get('/user/me')
  return data
}
