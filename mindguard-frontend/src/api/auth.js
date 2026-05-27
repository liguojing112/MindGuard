import request from '@/utils/request'

export function login(data) {
  return request.post('/auth/login', data)
}

export function register(data) {
  return request.post('/auth/register', data)
}

export function getInfo() {
  return request.get('/auth/info')
}

export function getLoginUsers() {
  return request.get('/auth/users')
}

export function updateProfile(data) {
  return request.put('/auth/profile', data)
}
