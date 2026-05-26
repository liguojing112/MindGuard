import request from '@/utils/request'

export function createPost(data) {
  return request.post('/emotion/posts', data)
}

export function getMyPosts(params) {
  return request.get('/emotion/posts', { params })
}

export function getPublicPosts(params) {
  return request.get('/emotion/posts/public', { params })
}

export function getPostDetail(id) {
  return request.get(`/emotion/posts/${id}`)
}

export function deletePost(id) {
  return request.delete(`/emotion/posts/${id}`)
}

export function concernPost(id) {
  return request.post(`/emotion/posts/${id}/concern`)
}

export function createCheckin(data) {
  return request.post('/emotion/checkin', data)
}

export function getCheckins() {
  return request.get('/emotion/checkin')
}

export function getCheckinCalendar(month) {
  return request.get('/emotion/checkin/calendar', { params: { month } })
}
