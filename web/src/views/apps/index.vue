<template>
  <div class="container">
    <div class="app-menu">
      <a-button type="primary" @click="createApp">
        <template #icon>
          <icon-plus />
        </template>
        创建应用
      </a-button>
    </div>

    <div v-if="hasApp" class="app-list">33333</div>
    <a-empty v-else description="还没有应用，快去创建一个吧！" style="margin-top: 25%"/>
    <!-- 创建应用-->
    <add-app :visible="visible" @cancel="cancel" @ok="ok"></add-app>
  </div>
</template>

<script setup lang="ts">
import {ref} from "vue";
import AddApp from "@/views/apps/componets/addApp.vue";

const hasApp = ref(false)
const visible = ref(false)

const createApp = () => {
  visible.value = true
}

const cancel = () => {
  visible.value = false
}

const ok = () => {
  console.log(111)
}
</script>

<script lang="ts">
export default {
  name: 'Apps',
};
</script>

<style scoped lang="less">
.container {
  background-color: var(--color-fill-2);
  padding: 16px 20px 0;
  display: flex;
  flex-direction: column;
}
.app-list {
  width: 100%;
  height: calc(100vh - 85px);
  margin-top: 20px;
}
.app-menu {
  width: 100%;height: 32px
}

.app-list::-webkit-scrollbar { width: 0 !important }
.app-list { -ms-overflow-style: none; }
.app-list { overflow: -moz-scrollbars-none; }
</style>