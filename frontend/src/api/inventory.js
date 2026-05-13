import request from '@/utils/request'

export function getInventoryList(params) {
  return request.get('/inventory', { params })
}

export function getInventoryByGoods(goodsId) {
  return request.get(`/inventory/goods/${goodsId}`)
}

export function updateInventoryConfig(data) {
  return request.put('/inventory/config', data)
}

export function getInventoryAlerts() {
  return request.get('/inventory/alerts')
}

export function getInventoryOverview() {
  return request.get('/inventory/overview')
}
