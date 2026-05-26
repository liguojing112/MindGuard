import request from '@/utils/request'

export function getScales() {
  return request.get('/assessments/scales')
}

export function getScaleDetail(id) {
  return request.get(`/assessments/scales/${id}`)
}

export function startAssessment(scaleId) {
  return request.post('/assessments/start', { scaleId })
}

export function submitAnswers(assessmentId, answers) {
  return request.post(`/assessments/${assessmentId}/submit`, { answers })
}

export function getMyAssessments(params) {
  return request.get('/assessments/my', { params })
}

export function getResult(assessmentId) {
  return request.get(`/assessments/${assessmentId}/result`)
}

export function getStats() {
  return request.get('/assessments/stats')
}

export function getAllAssessments(params) {
  return request.get('/assessments/all', { params })
}

export function exportAssessments() {
  return request.get('/assessments/export', { responseType: 'blob' })
}
