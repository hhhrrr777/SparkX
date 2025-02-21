<template>
  <a-modal
      :visible="visible"
      width="700px"
      ok-text="确认保存"
      cancel-text="取消"
      title-align="start"
      :mask-closable="false"
      modal-class="my-modal"
      @ok="handleOk"
      @cancel="handleCancel">
    <template #title>
      <icon-robot-add class="title-icon"/> 编辑模型
    </template>
    <a-form :model="form">
      <a-form-item field="name" label="AI名称" required>
        <a-input
            v-model="form.name"
            placeholder="AI名称"
            allow-clear
        />
      </a-form-item>
      <a-form-item field="logo" label="图标" required>
        <a-upload
            list-type="picture-card"
            action="/"
            :default-file-list="[{uid: 1, name: 'logo.png', url: form.logo}]"
            image-preview
        />
      </a-form-item>
      <a-form-item field="desc" label="描述">
        <a-textarea v-model="form.desc" allow-clear/>
      </a-form-item>
      <a-form-item field="desc" label="模型秘钥" required>
        <a-textarea v-model="form.config" allow-clear/>
      </a-form-item>
      <a-form-item field="temperature" label="温度" tooltip="范围0~1值越大回答越发散" required>
        <a-slider :default-value="form.temperature" :style="{ width: '200px' }" :step="0.1" :min="0" :max="1"/>
      </a-form-item>
      <a-form-item field="status" label="状态" required>
        <a-switch v-model="form.status" :checked-value="1" :unchecked-value="2">
          <template #checked>
            开启
          </template>
          <template #unchecked>
            关闭
          </template>
        </a-switch>
      </a-form-item>

      <a-card title="模型配置">
        <template #extra>
          <a-link @click="addModelDetail">新增模型</a-link>
        </template>
        <a-table :columns="columns" :data="tableData" :pagination="false">
          <template #name="{ rowIndex }">
            <a-input v-model="tableData[rowIndex].name"></a-input>
          </template>
          <template #max_tokens="{ rowIndex }">
            <a-input v-model="tableData[rowIndex].max_tokens"></a-input>
          </template>
          <template #context="{ rowIndex }">
            <a-input v-model="tableData[rowIndex].context"></a-input>
          </template>
          <template #operate="{ rowIndex }">
            <a-button @click="delTable(rowIndex)">删除</a-button>
          </template>
        </a-table>
      </a-card>
    </a-form>
  </a-modal>
</template>

<script setup lang="ts">
import {ref} from 'vue'

const emit = defineEmits(['cancel', 'ok'])

const form = ref({
  id: 0,
  logo: "https://ai-demo.chatmoney.cn/resource/image/models/api2d.png",
  name: "",
  desc: "",
  temperature: 0,
  config: JSON.stringify({}),
  status: 1
})

const columns = ref([
  {
    title: '模型名称',
    dataIndex: 'name',
    slotName: 'name'
  },
  {
    title: '最大token数',
    dataIndex: 'max_tokens',
    slotName: 'max_tokens'
  },
  {
    title: '上下文数',
    dataIndex: 'context',
    slotName: 'context'
  },
  {
    title: '操作',
    slotName: 'operate'
  }
])

const tableData = ref([
  {name: 'ERNIE-Speed-128K', max_tokens: 180000, context: 0}
])

// 删除模型
const delTable = (index: number) => {
  tableData.value.splice(index, 1)
}

defineProps({
  visible: Boolean,
})

const handleOk = () => {
  emit("ok")
}

const handleCancel = () => {
  emit("cancel")
}

// 新增模型
const addModelDetail = () => {
  tableData.value[tableData.value.length] = {name: '', max_tokens: 1000, context: 4}
}
</script>

<style lang="less">
.title-icon {
  margin-right: 5px;font-size: 24px;color: rgb(var(--primary-6))
}
.my-modal {
  .arco-modal-header {
    border-bottom: none !important;
  }
  .arco-modal-footer {
    border-top: none !important;
  }
}
.app-item {
  height: 96px;
  border-radius: 5px;
  border: 1px solid var(--color-border-2);
  padding: 10px;
  font-size: 13px;
  cursor: pointer;
  display: flex;
  flex-direction: column;
}
</style>

<style scoped lang="less">

</style>