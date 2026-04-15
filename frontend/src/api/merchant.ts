import request from './request'

export function applyMerchant(data: { shopName: string; contact?: string; category?: string; description?: string }) {
  return request.post('/merchant/apply', data)
}

export function getApplyStatus() {
  return request.get('/merchant/apply')
}
