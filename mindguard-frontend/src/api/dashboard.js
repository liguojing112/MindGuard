import request from '@/utils/request'

export function getOverview() {
  return request.get('/dashboard/overview')
}

export function getEmotionTrend(days = 30) {
  return request.get('/dashboard/emotion-trend', { params: { days } })
}

export function getEmotionDistribution() {
  return request.get('/dashboard/emotion-distribution')
}

export function getAssessmentDist() {
  return request.get('/dashboard/assessment-distribution')
}

export function getAppointmentStats() {
  return request.get('/dashboard/appointment-stats')
}

export function getCrisisTrend(days = 30) {
  return request.get('/dashboard/crisis-trend', { params: { days } })
}
