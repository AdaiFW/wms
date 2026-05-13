<template>
  <el-card>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索商品" clearable style="width:220px" @keyup.enter="fetchData" />
      <el-tag type="danger" v-if="alerts.length > 0">
        {{ alerts.length }} 个商品库存不足
      </el-tag>
    </div>
    <el-table :data="tableData" stripe v-loading="loading">
      <el-table-column prop="goodsCode" label="商品编码" width="120" />
      <el-table-column prop="goodsName" label="商品名称" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="quantity" label="当前库存" width="100">
        <template #default="{row}">
          <span :class="{ 'alert-text': row.quantity <= row.alertThreshold }">
            {{ row.quantity }}
          </span>
        </template>
      </el-table-column>
      <el-table-column prop="minStock" label="最小库存" width="100" />
      <el-table-column prop="maxStock" label="最大库存" width="100" />
      <el-table-column prop="alertThreshold" label="预警阈值" width="100">
        <template #default="{row}">
          <el-tag v-if="row.quantity <= row.alertThreshold" type="danger">
            {{ row.alertThreshold || row.alert_threshold }}
          </el-tag>
          <span v-else>{{ row.alertThreshold || row.alert_threshold }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="120">
        <template #default="{row}">
          <el-button type="primary" link @click="showConfig(row)">配置</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="configVisible" title="库存配置" width="400px">
      <el-form :model="configForm" ref="configFormRef" label-width="100px">
        <el-form-item label="商品">
          <span>{{ selectedGoodsName }}</span>
        </el-form-item>
        <el-form-item label="最小库存">
          <el-input-number v-model="configForm.minStock" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="最大库存">
          <el-input-number v-model="configForm.maxStock" :min="1" style="width:100%" />
        </el-form-item>
        <el-form-item label="预警阈值" prop="alertThreshold">
          <el-input-number v-model="configForm.alertThreshold" :min="1" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="configVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveConfig">保存</el-button>
      </template>
    </el-dialog>
  </el-card>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getInventoryList, getInventoryAlerts, updateInventoryConfig } from '@/api/inventory'
import { ElMessage } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(20)
const total = ref(0)
const keyword = ref('')
const alerts = ref([])
const configVisible = ref(false)
const configFormRef = ref(null)
const configForm = ref({ goodsId: null, minStock: 0, maxStock: 99999, alertThreshold: 10 })
const selectedGoodsName = ref('')

const fetchData = async () => {
  loading.value = true
  try {
    const data = await getInventoryList({ page: page.value, size: size.value, keyword: keyword.value })
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch {} finally { loading.value = false }
}

const fetchAlerts = async () => {
  try { alerts.value = await getInventoryAlerts() } catch {}
}

const showConfig = (row) => {
  configForm.value = {
    goodsId: row.goodsId,
    minStock: row.minStock || row.min_stock || 0,
    maxStock: row.maxStock || row.max_stock || 99999,
    alertThreshold: row.alertThreshold || row.alert_threshold || 10,
  }
  selectedGoodsName.value = row.goodsName
  configVisible.value = true
}

const handleSaveConfig = async () => {
  try {
    await updateInventoryConfig(configForm.value)
    ElMessage.success('配置更新成功')
    configVisible.value = false
    fetchData()
  } catch {}
}

onMounted(() => { fetchData(); fetchAlerts() })
</script>

<style scoped>
.toolbar { display: flex; justify-content: space-between; margin-bottom: 16px; }
.alert-text { color: #F56C6C; font-weight: bold; }
</style>
