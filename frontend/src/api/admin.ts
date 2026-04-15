import request from './request'

export function getUsers(params: { page?: number; size?: number }) {
  return request.get('/admin/users', { params })
}

export function toggleUserStatus(id: number, status: number) {
  return request.put(`/admin/users/${id}/status`, null, { params: { status } })
}

export function getMerchants(params: { page?: number; size?: number }) {
  return request.get('/admin/merchants', { params })
}

export function auditMerchant(id: number, data: { auditStatus: number; auditRemark?: string }) {
  return request.put(`/admin/merchants/${id}/audit`, data)
}

export function getDrones(params: { page?: number; size?: number }) {
  return request.get('/admin/drones', { params })
}

export function createDrone(data: { code: string; model?: string }) {
  return request.post('/admin/drones', data)
}

export function updateDrone(id: number, data: any) {
  return request.put(`/admin/drones/${id}`, data)
}

export function getDroneLogs(id: number, params: { page?: number; size?: number }) {
  return request.get(`/admin/drones/${id}/logs`, { params })
}

export function getAdminOrders(params: { status?: number; merchantId?: number; page?: number; size?: number }) {
  return request.get('/admin/orders', { params })
}

export function getActiveChatSessions() {
  return request.get('/admin/chat/sessions')
}

export function closeChatSession(id: number) {
  return request.put(`/admin/chat/sessions/${id}/close`)
}

export function getStats() {
  return request.get('/admin/stats')
}
