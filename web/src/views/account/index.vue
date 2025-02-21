<template>
  <div class="container">
    <div class="user-menu">
      <div class="menu-item" :class="{'active': nowIndex == 1}" @click="checkMenu(1)">个人信息</div>
      <div class="menu-item" :class="{'active': nowIndex == 2}" @click="checkMenu(2)">AI模型</div>
    </div>
    <div class="content">
      <div v-if="nowIndex == 1" class="user-data">
        <a-form :model="form" :style="{ width: '400px' }" @submit="handleSubmit">
          <a-form-item field="password" label="原密码">
            <a-input
                v-model="form.password"
                placeholder="请输入原密码"
            />
          </a-form-item>
          <a-form-item field="rePassword" label="新密码">
            <a-input
                v-model="form.rePassword"
                placeholder="请输入新密码"
            />
          </a-form-item>
          <a-form-item>
            <a-button html-type="submit" type="primary" :loading="loading">确认修改</a-button>
          </a-form-item>
        </a-form>
      </div>
      <div v-else class="model-list">
        <div v-for="item in modelDataList" :key="item.id" class="model-item">
          <div class="logo">
            <img :src="item.logo" style="width: 44px;height: 44px">
          </div>
          <div class="name">
            <div class="name-title">{{ item.name }}</div>
            <div v-if="item.status == 1" class="status"><em class="success"></em>已启用</div>
            <div v-else class="status"><em class="danger"></em>已禁用</div>
          </div>
          <div class="operate" @click="edit(item.id)">编辑</div>
        </div>
      </div>
    </div>

    <!-- 编辑模型 -->
    <save-dialog :visible="visible" @cancel="cancel" @ok="ok"></save-dialog>
  </div>
</template>

<script setup lang="ts">
  import { ref } from 'vue';
  import useLoading from '@/hooks/loading';
  import {
    resetPwd
  } from '@/api/user';
  import type { UserPwd } from '@/api/user';
  import { Message } from "@arco-design/web-vue";
  import {list} from '@/api/models'
  import saveDialog from './save.vue'

  const { loading, setLoading } = useLoading();

  const nowIndex = ref(1);
  const visible = ref(false)
  const checkMenu = (tab: number) => {
    nowIndex.value = tab;
  }

  const form = ref({
    password: "",
    rePassword: ""
  });

  const modelDataList = ref([]);
  // 模型列表
  const modelList = async () => {
    const res: any = await list();
    modelDataList.value = res.data
  }

  modelList();

  // 编辑模型
  const edit = (id: number) => {
    visible.value = true
    console.log('xx', id)
  }

  const cancel = () => {
    visible.value = false
  }

  const ok = () => {
    console.log(222)
  }

  // 修改密码
  const handleSubmit = async({
    values
  }: {
    values: Record<string, any>;
  }) => {
    try {

      setLoading(true)
      const res: any = await resetPwd(values as UserPwd)
      setLoading(false)
      if (res.code === 200) {
        Message.success({
          content: res.msg,
          duration: 3 * 1000,
        });

        form.value.password = ""
        form.value.rePassword = ""
      }
    } catch (error) {
      setLoading(false)
    }
  }
</script>

<style scoped lang="less">
.container {
  background-color: var(--color-fill-2);
  padding: 16px 20px 0;
  display: flex;
  height: 100%;
  width: 100%;
}
.user-menu {
  height: calc(100% - 20px);
  background: #fff;
  border-radius: 10px;
  display: flex;
  flex-direction: column;
  padding: 20px 10px;
  width: 200px;

  .menu-item {
    width: 100%;
    height: 40px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }
  .menu-item.active {
    color: rgb(var(--primary-6));
    font-weight: 500;
    background-color: var(--color-fill-2)
  }
}
.content {
  width: calc(100% - 550px);
  height: calc(100% - 20px);
  background: #fff;
  margin-left: 20px;
  border-radius: 10px;
}
.user-data {
  display: flex;
  align-items: center;
  justify-content: center;
  padding-top: 100px;
}
.model-list {
  display: flex;
  flex-wrap: wrap;
  padding: 20px;
  .model-item {
    width: 300px;
    height: 100px;
    margin-right: 20px;
    margin-bottom: 20px;
    background: #f8f8f8;
    cursor: pointer;
    padding: 25px 15px;
    display: flex;
    align-items: center;
    border: 1px solid #f8f8f8;
    border-radius: 5px;
    .name {
      width: 132px;
      height: 49px;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      .name-title {
        font-weight: 700;
      }
    }
    .status {
      display: flex;
      align-items: center;
      color: #999999;
      margin-top: 10px;
    }
    .success {
      width: 10px;
      height: 10px;
      border-radius: 50%;
      display: block;
      background: #67c23A;
      margin-right: 10px;
    }
  }
  .model-item:hover {
    border: 1px solid #4A5DFF !important;
  }
  .operate {
    width: 60px;
    height: 32px;
    background: #EDEFFF;
    padding: 8px 15px;
    color: #4A5DFF;
    margin-left: 20px;
    border-radius: 5px;
    border: 1px solid #A5AEFF;
    font-size: 14px;
  }
  .operate:hover {
    background: #4A5DFF !important;
    border: 1px solid #4A5DFF !important;
    color: #fff !important;
  }
}
</style>