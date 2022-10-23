<template>
  <el-row class="tac">
    <el-col class="side-bar" :span="3">
      <el-menu default-active="home" class="el-menu-vertical" :router="true">
        <el-menu-item index="home">
          <i class="el-icon-menu"></i>
          <span slot="title">提交列表</span>
        </el-menu-item>
        <el-menu-item index="list" disabled="true">
          <i class="el-icon-document"></i>
          <span slot="title">分数总览</span>
        </el-menu-item>
        <el-menu-item index="img-setting">
          <i class="el-icon-setting"></i>
          <span slot="title">图片设置</span>
        </el-menu-item>
        <el-menu-item index="setting">
          <i class="el-icon-setting"></i>
          <span slot="title">实验设置</span>
        </el-menu-item>
        <!-- <el-menu-item index="login-record">
          <i class="el-icon-user-solid"></i>
          <span slot="title">登录日志</span>
        </el-menu-item> -->
      </el-menu>
    </el-col>
    <el-col :span="21">
      <router-view></router-view>
    </el-col>
  </el-row>
</template>
<script>
// import md5 from 'js-md5'
export default {
  name: 'Home',
  beforeRouteEnter (to, from, next) {
    // console.log('beforeRouteEnter', to, from, next)
    const token = sessionStorage.getItem('web-token')
    if (token == null) {
      next('/login')
    } else if (window.decodeURIComponent(window.atob(token)) === '0') {
      let xhr = new XMLHttpRequest()

      let account = JSON.parse(window.decodeURIComponent(window.atob(sessionStorage.getItem('web-account'))))
      xhr.open('GET', 'https://sc6zpg.lafyun.com/web-suspect-user?userId=' + account.userid + '&userName=' + account.username)
      xhr.send()
      alert('Big 胆，居然想溜进来!我要告诉助教听，不对~我就是助教')
      next('/list')
    } else if (window.decodeURIComponent(window.atob(token)) !== '1') {
      next('/login')
    }
    next()
  },
  computed: {
    account () {
      return this.$store.state.account
    }
  },
  mounted () {
    // 如果用户刷新了页面
    if (this.account === null || this.account === undefined) {
      let storage = sessionStorage.getItem('web-account')
      let obj = JSON.parse(window.decodeURIComponent(window.atob(storage)))
      if (storage == null || obj.username == null || obj.userid == null) {
        this.$router.push('/login')
      } else {
        console.log('dispatch', obj)
        this.$store.dispatch('handleTeacherLogin', obj)
      }
    }
  }
}
</script>
<style scoped>
.tac {
  width: 100%;
}
</style>
