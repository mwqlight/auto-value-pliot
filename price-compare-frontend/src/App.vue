<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup lang="ts">
// 应用主组件
</script>

<style lang="scss">
@import '@/styles/index.scss';

#app {
  height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  overflow: hidden;
}
</style>