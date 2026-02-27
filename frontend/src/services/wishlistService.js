import apiClient from '../lib/apiClient'

export async function listWishlistItems() {
  const { data } = await apiClient.get('/v1/wishlist')
  return data
}

export async function addWishlistItem(productId) {
  const { data } = await apiClient.post(`/v1/wishlist/${productId}`)
  return data
}

export async function removeWishlistItem(productId) {
  await apiClient.delete(`/v1/wishlist/${productId}`)
}
