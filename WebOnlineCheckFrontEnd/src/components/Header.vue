<template>
    <el-container>
        <el-header class="header" height="70px">
            <div class="left">
                <h3>Web 编程实验 校验网站</h3>
            </div>
            <div class="right">
                <span v-if="account">
                  <i class="el-icon-user">{{account.username}}</i>
                    <el-button class="button" type="warning" @click="logout">注销</el-button>
                </span>
                <el-button class="button" type="success" v-else @click="login">点击登录</el-button>
            </div>
        </el-header>
    </el-container>
</template>
<script>
export default {
  name: 'Header',
  data () {
    return {
    }
  },
  computed: {
    account () {
      return this.$store.state.account
    }
  },
  methods: {
    login () {
      this.$router.replace('/login')
    },
    logout () {
      this.$store.dispatch('logout').then(() => {
        this.$router.replace('/login')
      })
    }
  }

}
</script>
<style scoped >
.header {
  background-color: darkturquoise;
  color: aliceblue;
  height: 70px;
}
.left{
    float: left;
    height: 70px;
    line-height: 70px;
}
.right{
  height: 70px;
    float:right;
    line-height: 70px;
}

.button{
    margin-left: 10px;
}
</style>
