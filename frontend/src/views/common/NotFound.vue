<template>
  <div class="not-found">
    <h1>404</h1>
    <p>页面不存在</p>
    <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
  </div>
</template>

<style scoped>
.not-found {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.not-found h1 { font-size: 80px; color: #409eff; margin: 0; }
.not-found p { font-size: 18px; color: #999; margin: 16px 0 32px; }
</style>
