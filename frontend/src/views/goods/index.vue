<template>
  <el-card>
    <div class="toolbar">
      <el-input v-model="keyword" placeholder="搜索商品名称/编码" clearable style="width:220px" @keyup.enter="fetchData" />
      <el-button type="primary" @click="showDialog(null)">新增商品</el-button>
    </div>
    <el-table :data="tableData" stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="goodsCode" label="商品编码" width="120" />
      <el-table-column prop="goodsName" label="商品名称" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="spec" label="规格" />
      <el-table-column prop="price" label="单价" width="100">
        <template #default="{row}">¥{{ row.price }}</template>
      </el-table-column>
      <el-table-column label="操作" width="180">
        <template #default="{row}">
          <el-button type="primary" link @click="showDialog(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row.id)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="page" :page-size="size" :total="total"
      layout="total, prev, pager, next" @current-change="fetchData" style="margin-top:16px"
    />
  </el-card>

  <el-dialog v-model="dialogVisible" :title="editId ? '编辑商品' : '新增商品'" width="500px">
    <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
      <el-form-item label="商品编码" prop="goodsCode">
        <el-input v-model="form.goodsCode" />
      </el-form-item>
      <el-form-item label="商品名称" prop="goodsName">
        <el-input v-model="form.goodsName" />
      </el-form-item>
      <el-form-item label="分类" prop="categoryId">
        <el-select v-model="form.categoryId" placeholder="选择分类" style="width:100%">
          <el-option v-for="c in categories" :key="c.id" :label="c.categoryName" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="单位" prop="unit">
        <el-input v-model="form.unit" />
      </el-form-item>
      <el-form-item label="规格" prop="spec">
        <el-input v-model="form.spec" />
      </el-form-item>
      <el-form-item label="单价" prop="price">
        <el-input-number v-model="form.price" :min="0" :precision="2" style="width:100%" />
      </el-form-item>
      <el-form-item label="描述" prop="description">
        <el-input v-model="form.description" type="textarea" />
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
import { getGoodsList, createGoods, updateGoods, deleteGoods } from '@/api/goods'
import { getCategoryList } from '@/api/category'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const keyword = ref('')
const dialogVisible = ref(false)
const editId = ref(null)
const formRef = ref(null)
const categories = ref([])
const form = ref({ goodsCode: '', goodsName: '', categoryId: null, unit: '个', spec: '', price: 0, description: '' })
const rules = {
  goodsCode: [{ required: true, message: '请输入商品编码', trigger: 'blur' }],
  goodsName: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
}

const fetchData = async () => {
  loading.value = true
  try {
    const data = await getGoodsList({ page: page.value, size: size.value, keyword: keyword.value })
    tableData.value = data.records
    total.value = data.total
  } catch {} finally { loading.value = false }
}

const fetchCategories = async () => {
  try { categories.value = await getCategoryList() } catch {}
}

const showDialog = (row) => {
  if (row) {
    editId.value = row.id
    form.value = { ...row }
  } else {
    editId.value = null
    form.value = { goodsCode: '', goodsName: '', categoryId: null, unit: '个', spec: '', price: 0, description: '' }
  }
  dialogVisible.value = true
}

const handleSave = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (editId.value) {
      await updateGoods(editId.value, form.value)
      ElMessage.success('更新成功')
    } else {
      await createGoods(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    fetchData()
  } catch {}
}

const handleDelete = async (id) => {
  await ElMessageBox.confirm('确定删除该商品?', '提示', { type: 'warning' })
  try {
    await deleteGoods(id)
    ElMessage.success('删除成功')
    fetchData()
  } catch {}
}

onMounted(() => { fetchData(); fetchCategories() })
</script>

<style scoped>
.toolbar { display: flex; gap: 12px; margin-bottom: 16px; }
</style>
