<template>
  <div class="dashboard">
    <el-row :gutter="16" class="stat-cards">
      <el-col :span="4" v-for="card in statCards" :key="card.label">
        <el-card shadow="hover" :body-style="{padding:'20px'}">
          <div class="stat-item">
            <div class="stat-icon" :style="{backgroundColor:card.color}">
              <el-icon size="28" color="#fff"><component :is="card.icon" /></el-icon>
            </div>
            <div class="stat-text">
              <div class="stat-value">{{ card.value }}</div>
              <div class="stat-label">{{ card.label }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="12">
        <el-card>
          <template #header>出入库趋势（近7天）</template>
          <v-chart :option="trendOption" style="height:350px" autoresize />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>库存预警</template>
          <el-table :data="alerts" stripe size="small" max-height="350px">
            <el-table-column prop="goodsName" label="商品名称" />
            <el-table-column prop="quantity" label="当前库存" />
            <el-table-column prop="alertThreshold" label="预警阈值">
              <template #default="{row}">
                <el-tag type="danger">{{ row.alertThreshold || row.alert_threshold }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getDashboard } from '@/api/statistics'
import { getTrend } from '@/api/statistics'
import { getInventoryAlerts } from '@/api/inventory'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'

use([CanvasRenderer, LineChart, BarChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const dashboard = ref({})
const trend = ref([])
const alerts = ref([])

const statCards = computed(() => [
  { label: '商品总数', value: dashboard.value.totalGoods || 0, icon: 'Goods', color: '#409EFF' },
  { label: '库存总量', value: dashboard.value.totalStock || 0, icon: 'Box', color: '#67C23A' },
  { label: '今日入库', value: dashboard.value.todayStockIn || 0, icon: 'Download', color: '#E6A23C' },
  { label: '今日出库', value: dashboard.value.todayStockOut || 0, icon: 'Upload', color: '#F56C6C' },
  { label: '预警商品', value: dashboard.value.alertCount || 0, icon: 'Bell', color: '#909399' },
  { label: '供应商', value: dashboard.value.totalSuppliers || 0, icon: 'OfficeBuilding', color: '#8E44AD' },
])

const trendOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: { data: ['入库', '出库'] },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: trend.value.map(t => t.date) },
  yAxis: { type: 'value' },
  series: [
    { name: '入库', type: 'bar', data: trend.value.map(t => t.stockIn), itemStyle: { color: '#67C23A' } },
    { name: '出库', type: 'bar', data: trend.value.map(t => t.stockOut), itemStyle: { color: '#F56C6C' } },
  ],
}))

onMounted(async () => {
  try { dashboard.value = await getDashboard() } catch {}
  try {
    const t = await getTrend(7)
    trend.value = t.trend || []
  } catch {}
  try { alerts.value = await getInventoryAlerts() } catch {}
})
</script>

<style scoped>
.stat-item { display: flex; align-items: center; gap: 16px; }
.stat-icon {
  width: 56px; height: 56px; border-radius: 8px;
  display: flex; align-items: center; justify-content: center;
}
.stat-value { font-size: 24px; font-weight: bold; color: #303133; }
.stat-label { font-size: 13px; color: #909399; margin-top: 4px; }
</style>
