<template>
  <div class="statistics">
    <el-row :gutter="16">
      <el-col :span="8" v-for="item in overviews" :key="item.label">
        <el-card shadow="hover">
          <div class="overview-item">
            <span class="overview-label">{{ item.label }}</span>
            <span class="overview-value" :style="{color:item.color}">{{ item.value }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="12">
        <el-card>
          <template #header>
            出入库趋势
            <el-radio-group v-model="trendDays" size="small" style="float:right" @change="fetchTrend">
              <el-radio-button :value="7">近7天</el-radio-button>
              <el-radio-button :value="30">近30天</el-radio-button>
            </el-radio-group>
          </template>
          <v-chart :option="trendOption" style="height:350px" autoresize />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>库存预警分布</template>
          <v-chart :option="alertPieOption" style="height:350px" autoresize />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getDashboard, getTrend } from '@/api/statistics'
import { getInventoryAlerts } from '@/api/inventory'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, BarChart, PieChart } from 'echarts/charts'
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'

use([CanvasRenderer, LineChart, BarChart, PieChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent])

const dashboard = ref({})
const trend = ref([])
const alerts = ref([])
const trendDays = ref(7)

const overviews = computed(() => [
  { label: '商品总数', value: dashboard.value.totalGoods || 0, color: '#409EFF' },
  { label: '库存总量', value: dashboard.value.totalStock || 0, color: '#67C23A' },
  { label: '今日入库', value: dashboard.value.todayStockIn || 0, color: '#E6A23C' },
  { label: '今日出库', value: dashboard.value.todayStockOut || 0, color: '#F56C6C' },
  { label: '预警商品', value: dashboard.value.alertCount || 0, color: '#E6A23C' },
  { label: '供应商', value: dashboard.value.totalSuppliers || 0, color: '#8E44AD' },
])

const trendOption = computed(() => ({
  tooltip: { trigger: 'axis' },
  legend: { data: ['入库', '出库'] },
  grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
  xAxis: { type: 'category', data: trend.value.map(t => t.date) },
  yAxis: { type: 'value' },
  series: [
    { name: '入库', type: 'line', data: trend.value.map(t => t.stockIn), smooth: true, itemStyle: { color: '#67C23A' } },
    { name: '出库', type: 'line', data: trend.value.map(t => t.stockOut), smooth: true, itemStyle: { color: '#F56C6C' } },
  ],
}))

const alertPieOption = computed(() => ({
  tooltip: { trigger: 'item' },
  series: [{
    type: 'pie',
    radius: ['40%', '70%'],
    data: alerts.value.slice(0, 10).map(a => ({
      name: a.goodsName || a.goods_name,
      value: a.quantity || 0,
    })),
  }],
}))

const fetchTrend = async () => {
  try {
    const t = await getTrend(trendDays.value)
    trend.value = t.trend || []
  } catch {}
}

onMounted(async () => {
  try { dashboard.value = await getDashboard() } catch {}
  fetchTrend()
  try { alerts.value = await getInventoryAlerts() } catch {}
})
</script>

<style scoped>
.overview-item { display: flex; flex-direction: column; align-items: center; gap: 8px; }
.overview-label { font-size: 14px; color: #909399; }
.overview-value { font-size: 28px; font-weight: bold; }
</style>
