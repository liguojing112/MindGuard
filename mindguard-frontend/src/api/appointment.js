import request from '@/utils/request'

export function getCounselors() {
  return request.get('/appointments/counselors')
}

export function getCounselorDetail(id) {
  return request.get(`/appointments/counselors/${id}`)
}

export function getSlots(counselorId, date) {
  return request.get(`/appointments/counselors/${counselorId}/slots`, { params: { date } })
}

export function createAppointment(data) {
  return request.post('/appointments', data)
}

export function getMyAppointments(params) {
  return request.get('/appointments/my', { params })
}

export function getCounselorAppointments(params) {
  return request.get('/appointments/counselor', { params })
}

export function getAppointmentDetail(id) {
  return request.get(`/appointments/${id}`)
}

export function approveAppointment(id) {
  return request.put(`/appointments/${id}/approve`)
}

export function rejectAppointment(id, reason) {
  return request.put(`/appointments/${id}/reject`, { reason })
}

export function startAppointment(id) {
  return request.put(`/appointments/${id}/start`)
}

export function completeAppointment(id, data) {
  return request.post(`/appointments/${id}/complete`, data)
}

export function evaluateAppointment(id, data) {
  return request.post(`/appointments/${id}/evaluate`, data)
}

export function getStudentArchive(studentId) {
  return request.get(`/students/${studentId}/archive`)
}
