import request from '@/utils/request'

export function getAlerts(params) {
  return request.get('/alerts', { params })
}

export function getPendingCount() {
  return request.get('/alerts/pending-count')
}

export function getAlertDetail(id) {
  return request.get(`/alerts/${id}`)
}

export function updateAlertStatus(id, data) {
  return request.put(`/alerts/${id}/status`, data)
}

export function getMyAlerts() {
  return request.get('/alerts/my')
}
