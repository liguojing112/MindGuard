import request from '@/utils/request'

export function getAISettings() {
  return request.get('/settings/ai')
}

export function saveAISettings(data) {
  return request.put('/settings/ai', data)
}
