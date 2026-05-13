import request from '@/utils/request'

export function getGoodsList(params) {
  return request.get('/goods', { params })
}

export function getGoods(id) {
  return request.get(`/goods/${id}`)
}

export function createGoods(data) {
  return request.post('/goods', data)
}

export function updateGoods(id, data) {
  return request.put(`/goods/${id}`, data)
}

export function deleteGoods(id) {
  return request.delete(`/goods/${id}`)
}
