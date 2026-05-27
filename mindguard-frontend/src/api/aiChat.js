import request from '@/utils/request'

export function sendChatMessage(message) {
  return request.post('/ai/chat', { message })
}

export function getChatHistory() {
  return request.get('/ai/chat/history')
}
