<template>
  <el-form ref="AccountFrom" :model="account" :rules="rules" label-width="0px" class="login-container">
    <h2>系统登录</h2>
    <el-form-item prop="username">
      <el-input type="text" v-model="account.username" auto-complete="off" placeholder="姓名"></el-input>
    </el-form-item>
    <el-form-item prop="userid">
      <el-input type="text" v-model="account.userid" auto-complete="off" placeholder="学号"
        @keyup.enter.native="handleLogin"></el-input>
    </el-form-item>
    <el-form-item class="btn-box">
      <el-button type="primary" @click="handleLogin" :loading="logining">登录</el-button>
      <!-- <el-button type="primary">
        <router-link to="/" style="text-decoration:none; color:white">返回首页</router-link>
      </el-button> -->
    </el-form-item>
  </el-form>
</template>

<script>

export default {
  name: 'Login',
  data () {
    // 表单校验,默认的用户名，密码
    return {
      logining: false,
      account: {
        username: 'web123',
        userid: 'web123'
      },
      /* 判断：如果用户没有输入用户名，提示“请输入账号”
        判断：如果用户没有输入密码，提示“请输入密码”用于表单验证 */
      rules: {
        username: [{
          required: true, message: '请输入姓名', trigger: 'blur'
        }],
        userid: [{
          required: true, message: '请输入学号', trigger: 'blur'
        }]
      },
      checked: true
    }
  },
  methods: {
    handleLogin () {
      this.$refs.AccountFrom.validate((valid) => {
        if (valid) {
          this.checkUser()
            .then((response) => {
              console.log('res:', response)
              if (response.data === 'OK') {
                this.checkUserType().then((res) => {
                  if (res.data == null) {
                    console.error('不可能出现的事情 发生了！')
                  } else if (res.data.type === 1) {
                    // 老师登录
                    this.teacherLogin()
                  } else if (res.data.type === 0) {
                    // 学生登录
                    this.studentLogin()
                  }
                })
              } else {
                this.$message({
                  type: 'error',
                  message: '学号或姓名错误',
                  showClose: true
                })
              }
            })
            .catch((error) => {
              console.log('err:', error)
              this.$message({
                type: 'error',
                message: '网络异常',
                showClose: true
              })
            })
        } else {
          return false
        }
      })
    },
    checkUser () {
      return this.axios({
        url: this.baseUrl + '/user/check',
        method: 'post',
        transformRequest: [(data) => {
          var oMyForm = new FormData()
          oMyForm.append('userId', this.account.userid)
          oMyForm.append('username', this.account.username)

          return oMyForm
        }]
      })
    },
    checkUserType () {
      return this.axios({
        url: this.baseUrl + '/user/check-token',
        method: 'post',
        transformRequest: [(data) => {
          var oMyForm = new FormData()
          oMyForm.append('userId', this.account.userid)
          oMyForm.append('username', this.account.username)
          return oMyForm
        }]
      })
    },
    studentLogin () {
      this.$store.dispatch('handleLogin', this.account).then(() => {
        this.$notify({
          type: 'success',
          message: '欢迎你，' + this.account.username + '!',
          duration: 3000
        })
        this.$router.push('/answer')
      })
    },
    teacherLogin () {
      this.$store.dispatch('handleTeacherLogin', this.account).then(() => {
        this.$notify({
          type: 'success',
          message: '欢迎你，' + this.account.username + '!',
          duration: 3000
        })
        this.$router.push('/teacher/')
      })
    }
  }
}
</script>

<style scoped>
body {
  background: aquamarine;
}

h2,h3 {
  text-align: center;
  margin-bottom: 30px;
  margin-top:18px;
}

.login-container {
  width:350px;
  margin: 0 auto;
}

.btn-box{
  width:100%;
  display: flex;
  justify-content: center;
  align-items:center;

}
</style>
