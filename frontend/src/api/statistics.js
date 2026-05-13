import request from '@/utils/request'

export function getDashboard() {
  return request.get('/statistics/dashboard')
}

export function getInventoryStats() {
  return request.get('/statistics/inventory')
}

export function getTrend(days) {
  return request.get('/statistics/trend', { params: { days } })
}
