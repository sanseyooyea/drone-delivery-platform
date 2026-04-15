import request from './request'

export function getProducts(params: { page?: number; size?: number; keyword?: string; categoryId?: number }) {
  return request.get('/products', { params })
}

export function getProduct(id: number) {
  return request.get(`/products/${id}`)
}

export function getCategories() {
  return request.get('/categories')
}

// 商家端
export function getMerchantProducts(params: { page?: number; size?: number }) {
  return request.get('/merchant/products', { params })
}

export function createProduct(data: any) {
  return request.post('/merchant/products', data)
}

export function updateProduct(id: number, data: any) {
  return request.put(`/merchant/products/${id}`, data)
}

export function deleteProduct(id: number) {
  return request.delete(`/merchant/products/${id}`)
}

export function toggleProductStatus(id: number, status: number) {
  return request.put(`/merchant/products/${id}/status`, null, { params: { status } })
}
