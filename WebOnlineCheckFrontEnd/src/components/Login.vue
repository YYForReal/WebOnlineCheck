<template>
  <el-form ref="AccountForm" :model="account" :rules="rules" label-width="0px" class="login-container">
    <h2>系统登录</h2>
    <el-form-item prop="username">
      <el-input type="text" v-model="account.username" auto-complete="off" placeholder="姓名"></el-input>
    </el-form-item>
    <el-form-item prop="userid">
      <el-input type="password" v-model="account.userid" auto-complete="off" placeholder="学号"
        @keyup.enter.native="handleLogin"></el-input>
    </el-form-item>
    <el-checkbox v-model="remember">记住我</el-checkbox>
    <el-form-item class="btn-box">
      <el-button type="primary" @click="handleLogin" >登录</el-button>
    </el-form-item>
    <div class="fix-reading-box">
      <video src="http://www.yywebsite.cn/video/reading.mp4" autoplay loop muted width="200px"></video>
    </div>
  </el-form>
</template>

<script>
export default {
  name: 'Login',
  data () {
    // 表单校验,默认的用户名，密码
    return {
      account: {
        username: '',
        userid: ''
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
      checked: true,
      remember: false
    }
  },
  mounted () {
    let webAccount = localStorage.getItem('web-account')
    if (webAccount != null) {
      this.account = JSON.parse(window.decodeURIComponent(window.atob(webAccount)))
      this.remember = true
    }
  },
  methods: {
    handleLogin () {
      this.$refs.AccountForm.validate((valid) => {
        if (valid) {
          this.checkUser()
            .then((response) => {
              // console.log(response)
              if (response.data === 'OK') {
                this.checkUserType().then((res) => {
                  // console.log(res)
                  if (res.data == null) {
                    console.error('不可能出现的事情 发生了！')
                  } else if (res.data.type === 1) {
                    // 老师登录
                    sessionStorage.setItem('web-token', this.$md5('1'))
                    sessionStorage.setItem('web-account', window.btoa(window.encodeURIComponent(JSON.stringify(this.account))))
                    if (this.remember) {
                      localStorage.setItem('web-account', window.btoa(window.encodeURIComponent(JSON.stringify(this.account))))
                    } else {
                      localStorage.removeItem('web-account')
                    }
                    this.recordLogin()
                    this.teacherLogin()
                  } else if (res.data.type === 0) {
                    // 学生登录
                    sessionStorage.setItem('web-token', this.$md5('0'))
                    sessionStorage.setItem('web-account', window.btoa(window.encodeURIComponent(JSON.stringify(this.account))))
                    if (this.remember) {
                      localStorage.setItem('web-account', window.btoa(window.encodeURIComponent(JSON.stringify(this.account))))
                    } else {
                      localStorage.removeItem('web-account')
                    }
                    this.studentLogin()
                  }
                }).catch((e) => {
                  console.log(e)
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
    recordLogin () {
      let agent = navigator.userAgent
      let xhr = new XMLHttpRequest()
      let account = this.account
      xhr.open('GET', 'https://sc6zpg.lafyun.com/teacher-login-record?userId=' + account.userid +
        '&userName=' + account.username + '&agent=' + agent)
      xhr.send()
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
        this.$router.push('/list')
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

h2,
h3 {
  text-align: center;
  margin-bottom: 30px;
  margin-top: 18px;
}

.login-container {
  width: 350px;
  margin: 0 auto;
}

.btn-box {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.fix-reading-box {
  position: fixed;
  right: 0;
  bottom: 0;
}

.form-footer {
  display: flex;
  justify-content: center;
}
</style>
