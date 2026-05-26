import request from '@/utils/request'

export function getNotifications(params) {
  return request.get('/notifications', { params })
}

export function getUnreadCount() {
  return request.get('/notifications/unread-count')
}

export function markAsRead(id) {
  return request.put(`/notifications/${id}/read`)
}
