import apiClient from '../lib/apiClient'

export async function placeOrderFromCart() {
  const { data } = await apiClient.post('/user/orders')
  return data
}

export async function getMyTrackingOrders() {
  const { data } = await apiClient.get('/v1/orders')
  return data
}

export async function getOrderTracking(orderId) {
  const { data } = await apiClient.get(`/v1/orders/${orderId}/tracking`)
  return data
}

export async function requestOrderReturn(orderId) {
  const { data } = await apiClient.post(`/v1/orders/${orderId}/return`)
  return data
}

export async function downloadOrderInvoice(orderId) {
  const { data } = await apiClient.get(`/v1/orders/${orderId}/invoice`, {
    responseType: 'blob',
  })
  return data
}

export async function getAdminOrders() {
  const { data } = await apiClient.get('/v1/admin/orders')
  return data
}

export async function updateAdminOrderStatus(orderId, payload) {
  const { data } = await apiClient.put(`/v1/admin/orders/${orderId}/status`, payload)
  return data
}
