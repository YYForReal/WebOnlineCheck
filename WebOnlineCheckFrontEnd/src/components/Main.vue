<template>
  <div calss="container">
    <el-row class="tac">
      <el-col class="side-bar" :span="3">
        <el-menu default-active="list" class="el-menu-vertical" :router="true">
          <el-menu-item index="list" >
            <i class="el-icon-document"></i>
            <span slot="title">已提交列表</span>
          </el-menu-item>
          <el-menu-item index="answer">
            <i class="el-icon-menu"></i>
            <span slot="title">提交答案</span>
          </el-menu-item>
        </el-menu>
      </el-col>
      <el-col :span="21">
        <router-view></router-view>
      </el-col>
    </el-row>
  </div>
</template>

<script>

export default {
  name: 'Main',
  methods: {
  },
  components: {
  }
}
</script>

<style scoped>
.container {
  width: 100%;
}

.container>>>.side-bar {
  height: 100%;
}

/* .side-bar>>>.el-menu-item {
  min-width: 0;
} */

</style>
