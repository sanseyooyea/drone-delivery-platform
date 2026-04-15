import request from './request'

export function createOrder(data: { addressId: number; merchantId: number; items: { productId: number; quantity: number }[]; remark?: string }) {
  return request.post('/orders', data)
}

export function getOrders(params: { status?: number; page?: number; size?: number }) {
  return request.get('/orders', { params })
}

export function getOrder(id: number) {
  return request.get(`/orders/${id}`)
}

export function cancelOrder(id: number) {
  return request.put(`/orders/${id}/cancel`)
}

export function confirmReceive(id: number) {
  return request.put(`/orders/${id}/confirm`)
}

// 商家端
export function getMerchantOrders(params: { status?: number; page?: number; size?: number }) {
  return request.get('/merchant/orders', { params })
}

export function acceptOrder(id: number) {
  return request.put(`/merchant/orders/${id}/accept`)
}

export function rejectOrder(id: number, reason: string) {
  return request.put(`/merchant/orders/${id}/reject`, { reason })
}

export function markOrderReady(id: number) {
  return request.put(`/merchant/orders/${id}/ready`)
}
