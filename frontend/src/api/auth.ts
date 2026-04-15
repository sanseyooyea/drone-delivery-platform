import request from './request'

export function login(data: { username: string; password: string }) {
  return request.post('/auth/login', data)
}

export function register(data: { username: string; password: string; phone?: string; email?: string }) {
  return request.post('/auth/register', data)
}

export function getProfile() {
  return request.get('/auth/profile')
}

export function updateProfile(data: { phone?: string; email?: string; avatar?: string }) {
  return request.put('/auth/profile', data)
}

export function updatePassword(data: { oldPassword: string; newPassword: string }) {
  return request.put('/auth/password', data)
}
