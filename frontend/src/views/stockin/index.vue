<template>
  <el-card>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索入库单号" clearable style="width:220px" @keyup.enter="fetchData" />
      <el-button type="primary" @click="dialogVisible = true">新增入库</el-button>
    </div>
    <el-table :data="tableData" stripe v-loading="loading">
      <el-table-column prop="stockInNo" label="入库单号" width="180" />
      <el-table-column prop="goodsId" label="商品ID" width="80" />
      <el-table-column prop="quantity" label="数量" width="80" />
      <el-table-column prop="unitPrice" label="单价" width="100">
        <template #default="{row}">¥{{ row.unitPrice }}</template>
      </el-table-column>
      <el-table-column prop="totalPrice" label="总价" width="100">
        <template #default="{row}">¥{{ row.totalPrice }}</template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{row}">
          <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="180" />
      <el-table-column label="操作" width="120">
        <template #default="{row}">
          <el-button v-if="row.status === 0" type="success" link @click="handleConfirm(row.id)">确认</el-button>
          <el-button v-if="row.status !== 2" type="danger" link @click="handleCancel(row.id)">取消</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="page" :page-size="size" :total="total"
      layout="total, prev, pager, next" @current-change="fetchData" style="margin-top:16px"
    />
  </el-card>

  <el-dialog v-model="dialogVisible" title="新增入库" width="450px">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
      <el-form-item label="商品" prop="goodsId">
        <el-select v-model="form.goodsId" placeholder="选择商品" filterable style="width:100%">
          <el-option v-for="g in goodsList" :key="g.id" :label="g.goodsName" :value="g.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="数量" prop="quantity">
        <el-input-number v-model="form.quantity" :min="1" style="width:100%" />
      </el-form-item>
      <el-form-item label="单价">
        <el-input-number v-model="form.unitPrice" :min="0" :precision="2" style="width:100%" />
      </el-form-item>
      <el-form-item label="总价">
        <el-input-number v-model="form.totalPrice" :min="0" :precision="2" style="width:100%" />
      </el-form-item>
      <el-form-item label="供应商">
        <el-select v-model="form.supplierId" placeholder="选择供应商" filterable style="width:100%">
          <el-option v-for="s in suppliers" :key="s.id" :label="s.supplierName" :value="s.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" @click="handleSave">保存</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getStockInList, createStockIn, confirmStockIn, cancelStockIn } from '@/api/stock'
import { getGoodsList } from '@/api/goods'
import { getSupplierList } from '@/api/supplier'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const keyword = ref('')
const dialogVisible = ref(false)
const formRef = ref(null)
const goodsList = ref([])
const suppliers = ref([])
const form = ref({ goodsId: null, quantity: 1, unitPrice: null, totalPrice: null, supplierId: null, remark: '' })
const rules = {
  goodsId: [{ required: true, message: '请选择商品', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
}

const statusText = (s) => ({ 0: '草稿', 1: '已入库', 2: '已取消' }[s] || s)
const statusType = (s) => ({ 0: 'info', 1: 'success', 2: 'danger' }[s] || 'info')

const fetchData = async () => {
  loading.value = true
  try {
    const data = await getStockInList({ page: page.value, size: size.value, keyword: keyword.value })
    tableData.value = data.records
    total.value = data.total
  } catch {} finally { loading.value = false }
}

const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    await createStockIn(form.value)
    ElMessage.success('入库成功')
    dialogVisible.value = false
    fetchData()
  } catch {}
}

const handleConfirm = async (id) => {
  await ElMessageBox.confirm('确认入库?', '提示', { type: 'info' })
  try { await confirmStockIn(id); ElMessage.success('确认成功'); fetchData() } catch {}
}

const handleCancel = async (id) => {
  await ElMessageBox.confirm('确定取消该入库单?', '提示', { type: 'warning' })
  try { await cancelStockIn(id); ElMessage.success('已取消'); fetchData() } catch {}
}

onMounted(async () => {
  fetchData()
  try { const g = await getGoodsList({ page: 1, size: 100 }); goodsList.value = g.records } catch {}
  try { const s = await getSupplierList({ page: 1, size: 100 }); suppliers.value = s.records } catch {}
})
</script>

<style scoped>
.toolbar { display: flex; gap: 12px; margin-bottom: 16px; }
</style>
