import apiClient from '../lib/apiClient'

export async function listProducts(params = {}) {
  const { data } = await apiClient.get('/v1/products', { params })
  return data
}

export async function getProductById(id) {
  const { data } = await apiClient.get(`/v1/products/${id}`)
  return data
}

export async function createProduct(payload, imageFile) {
  const formData = new FormData()
  formData.append('product', JSON.stringify(payload))
  if (imageFile) {
    formData.append('image', imageFile)
  }

  const { data } = await apiClient.post('/v1/admin/products', formData)

  return data
}

export async function updateProduct(id, payload, imageFile) {
  const formData = new FormData()
  formData.append('product', JSON.stringify(payload))
  if (imageFile) {
    formData.append('image', imageFile)
  }

  const { data } = await apiClient.put(`/v1/admin/products/${id}`, formData)

  return data
}

export async function deleteProduct(id) {
  await apiClient.delete(`/v1/admin/products/${id}`)
}

export async function listProductReviews(productId) {
  const { data } = await apiClient.get(`/v1/products/${productId}/reviews`)
  return data
}

export async function createProductReview(productId, payload) {
  const { data } = await apiClient.post(`/v1/products/${productId}/reviews`, payload)
  return data
}
