import apiClient from '../lib/apiClient'

export async function getCart() {
  const { data } = await apiClient.get('/user/cart')
  return data
}

export async function addToCart(payload) {
  const { data } = await apiClient.post('/user/cart/items', payload)
  return data
}

export async function updateCartItem(productId, quantity) {
  const { data } = await apiClient.put(`/user/cart/items/${productId}`, { quantity })
  return data
}

export async function removeCartItem(productId) {
  const { data } = await apiClient.delete(`/user/cart/items/${productId}`)
  return data
}

export async function clearCart() {
  const { data } = await apiClient.delete('/user/cart')
  return data
}
