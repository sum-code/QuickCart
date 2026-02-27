import apiClient from '../lib/apiClient'

export async function loginRequest(payload) {
  const { data } = await apiClient.post('/auth/login', payload, {
    headers: { 'Content-Type': 'application/json' },
  })
  return data
}

export async function registerRequest(payload) {
  const { data } = await apiClient.post('/auth/register', payload, {
    headers: { 'Content-Type': 'application/json' },
  })
  return data
}

export async function getCurrentUserRequest() {
  const { data } = await apiClient.get('/user/me')
  return data
}

export async function logoutRequest() {
  const { data } = await apiClient.post('/auth/logout')
  return data
}
