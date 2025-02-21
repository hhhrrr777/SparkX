<template>
  <a-modal
      :visible="visible"
      width="600px"
      ok-text="立即创建"
      cancel-text="取消"
      title-align="start"
      :mask-closable="false"
      modal-class="my-modal"
      @ok="handleOk"
      @cancel="handleCancel">
    <template #title>
      <icon-robot-add class="title-icon"/> 创建属于自己的AI应用
    </template>
    <a-form :model="form">
      <a-form-item field="name" label="应用名称">
        <a-input
            v-model="form.title"
            placeholder="取个名字吧"
        />
      </a-form-item>
    </a-form>
    <a-row :gutter="24">
      <a-col :span="12">
        <div class="app-item">
          <div>简易模板</div>
          <div>一个极其简单的 AI 应用，你可以绑定知识库或工具。</div>
        </div>
      </a-col>
      <a-col :span="12">
        <div class="app-item">
          <div></div>
          <div>可以在对话开始发送一段提示，或者让用户填写一些内容，作为本次对话的变量。</div>
        </div>
      </a-col>
    </a-row>
    <a-row :gutter="24" style="margin-top: 15px">
      <a-col :span="12">
        <div class="app-item">
          <div></div>
          <div>每次提问时进行一次知识库搜索，将搜索结果注入 LLM 模型进行参考回答。</div>
        </div>
      </a-col>
    </a-row>
  </a-modal>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const emit = defineEmits(['cancel', 'ok'])

defineProps({
  visible: Boolean,
})

const handleOk = () => {
  emit("ok")
}

const handleCancel = () => {
  emit("cancel")
}

const form = ref({
  title: "",

})
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