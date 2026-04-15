import request from './request'

export function getCart() {
  return request.get('/cart')
}

export function addToCart(productId: number, quantity: number = 1) {
  return request.post('/cart', null, { params: { productId, quantity } })
}

export function updateCartQuantity(id: number, quantity: number) {
  return request.put(`/cart/${id}`, null, { params: { quantity } })
}

export function removeFromCart(id: number) {
  return request.delete(`/cart/${id}`)
}
