<template>
  <el-row class="tac">
    <el-col class="side-bar" :span="3">
      <el-menu default-active="home" class="el-menu-vertical" :router="true">
        <el-menu-item index="home">
          <i class="el-icon-menu"></i>
          <span slot="title">提交列表</span>
        </el-menu-item>
        <el-menu-item index="list">
          <i class="el-icon-document"></i>
          <span slot="title">分数总览</span>
        </el-menu-item>
        <el-menu-item index="setting">
          <i class="el-icon-setting"></i>
          <span slot="title">实验设置</span>
        </el-menu-item>
      </el-menu>
    </el-col>
    <el-col :span="21">
      <router-view></router-view>
    </el-col>
  </el-row>
</template>
<script>

export default {
  name: 'Home',
  data () {
    return {

    }
  }
}
</script>
<style scoped>
.tac {
  width: 100%;
}
</style>
