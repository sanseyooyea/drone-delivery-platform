import request from './request'

export function createChatSession(orderId?: number) {
  return request.post('/chat/sessions', null, { params: orderId ? { orderId } : {} })
}

export function getChatSessions() {
  return request.get('/chat/sessions')
}

export function getChatMessages(sessionId: number) {
  return request.get(`/chat/sessions/${sessionId}/messages`)
}
