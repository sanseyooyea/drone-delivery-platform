import request from './request'

export function getAddresses() {
  return request.get('/addresses')
}

export function createAddress(data: { contactName: string; contactPhone: string; address: string; isDefault?: number }) {
  return request.post('/addresses', data)
}

export function updateAddress(id: number, data: any) {
  return request.put(`/addresses/${id}`, data)
}

export function deleteAddress(id: number) {
  return request.delete(`/addresses/${id}`)
}

export function setDefaultAddress(id: number) {
  return request.put(`/addresses/${id}/default`)
}
