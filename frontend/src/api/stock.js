import request from '@/utils/request'

export function getStockInList(params) {
  return request.get('/stock-in', { params })
}

export function createStockIn(data) {
  return request.post('/stock-in', data)
}

export function confirmStockIn(id) {
  return request.put(`/stock-in/${id}/confirm`)
}

export function cancelStockIn(id) {
  return request.put(`/stock-in/${id}/cancel`)
}

export function getStockOutList(params) {
  return request.get('/stock-out', { params })
}

export function createStockOut(data) {
  return request.post('/stock-out', data)
}

export function confirmStockOut(id) {
  return request.put(`/stock-out/${id}/confirm`)
}

export function cancelStockOut(id) {
  return request.put(`/stock-out/${id}/cancel`)
}
