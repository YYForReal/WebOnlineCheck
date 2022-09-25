<template>
  <div calss="container">
    <el-row class="tac">
      <el-col class="side-bar" :span="3">
        <el-menu default-active="2" class="el-menu-vertical-demo" @open="handleOpen" @close="handleClose">
          <el-submenu index="1" disabled>
            <template slot="title">
              <i class="el-icon-location"></i>
              <span>提交记录（TODO）</span>
            </template>
            <el-menu-item-group>
              <template slot="title">分组一</template>
              <el-menu-item index="1-1">选项1</el-menu-item>
              <el-menu-item index="1-2">选项2</el-menu-item>
            </el-menu-item-group>
            <el-menu-item-group title="分组2">
              <el-menu-item index="1-3">选项3</el-menu-item>
            </el-menu-item-group>
            <el-submenu index="1-4">
              <template slot="title">选项4</template>
              <el-menu-item index="1-4-1">选项1</el-menu-item>
            </el-submenu>
          </el-submenu>
          <el-menu-item index="2">
            <i class="el-icon-menu"></i>
            <span slot="title">提交答案</span>
          </el-menu-item>
          <!-- <el-menu-item index="3" disabled>
            <i class="el-icon-document"></i>
            <span slot="title">提交记录（TODO）</span>
          </el-menu-item> -->
          <!-- <el-menu-item index="4">
            <i class="el-icon-setting"></i>
            <span slot="title">导航四</span>
          </el-menu-item> -->
        </el-menu>
      </el-col>
      <el-col :span="21">
        <router-view></router-view>
        <div class="fix-reading-box">
          <video src="http://www.yywebsite.cn/video/reading.mp4" autoplay loop muted width="200px"></video>
        </div>
      </el-col>

    </el-row>

  </div>
</template>

<script>

export default {
  name: 'Main',
  methods: {
    handleOpen (key, keyPath) {
      console.log(key, keyPath)
    },
    handleClose (key, keyPath) {
      console.log(key, keyPath)
    }
  },
  components: {
  }
}
</script>

<style scoped>
.container{
  width:100%;
}

.container >>> .side-bar{
  height: 100%;
}

.fix-reading-box{
  position: fixed;
  right: 0;
  bottom: 0;
}

</style>
