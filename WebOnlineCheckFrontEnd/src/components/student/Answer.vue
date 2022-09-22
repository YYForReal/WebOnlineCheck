<template>
  <el-form ref="AnswerFrom" :model="form" :rules="rules" label-position="left" label-width="0px" class="login-container">
    <h3>提交答案</h3>

    <el-form-item prop="questionId">
      <el-select v-model="form.questionId" placeholder="请选择实验题目">
        <el-option v-for="item in questionList" :key="item.questionId" :label="item.questionTitle"
          :value="item.questionId" />
      </el-select>
    </el-form-item>

    <el-form-item prop="content">
      <el-input class="html-text-box" type="textarea" v-model="form.content" auto-complete="off" resize="none"
        placeholder="在此输入html文本"></el-input>
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="handleSumbit" :loading="logining">提交</el-button>
    </el-form-item>
  </el-form>
</template>

<script>

export default {
  name: 'Answer',
  data () {
    // 表单校验,默认的用户名，密码
    return {
      logining: false,
      rules: {
        content: [{
          required: true, message: '请输入html文本', trigger: 'blur'
        }],
        questionId: [{
          required: true, message: '请选择实验题目', trigger: 'blur'
        }]
      },
      form: {
        questionId: null,
        userId: null,
        content: null
      },
      questionList: [
        {
          questionId: 1,
          questionTitle: 'AAAA'
        },
        {
          questionId: 2,
          questionTitle: 'AffA'
        }
      ]
    }
  },
  methods: {
    handleSumbit () {
      this.$refs.AnswerFrom.validate((valid) => {
        if (valid) {
          this.checkUser().then((res) => {
            if (res.data === 'OK') {
              this.form.userId = this.account.userid
              console.log('success', this.form)
              this.post()
            } else {
              this.$message({
                type: 'error',
                message: '登录状态异常',
                showClose: true
              })
            }
          }).catch(() => {
            this.$message({
              type: 'error',
              message: '未处于正确的登录状态，请重新登录',
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
          console.info(oMyForm)
          return oMyForm
        }]
      })
    },
    post () {
      this.axios({
        url: this.baseUrl + '/answer/post',
        method: 'post',
        transformRequest: [(data) => {
          var oMyForm = new FormData()
          oMyForm.append('userId', this.account.userid)
          oMyForm.append('questionId', this.form.questionId)
          oMyForm.append('content', this.form.content)
          console.info(oMyForm)
          return oMyForm
        }]
      }).then((res) => {
        if (res.data === 'OK') {
          this.$message({
            type: 'success',
            message: '提交成功',
            showClose: true
          })
        } else {
          this.$message({
            type: 'error',
            message: '提交失败',
            showClose: true
          })
        }
      }).catch(() => {
        this.$message({
          type: 'error',
          message: '网络异常',
          showClose: true
        })
      })
    }
  },
  computed: {
    account () {
      return this.$store.state.account
    }
  }
}
</script>

<style  scoped>
body {
  background: aquamarine;
}

.login-container {
  width: 350px;
  margin-left: 20%;
}

.html-text-box {
  width: 500px;
  height: 300px;
}

.html-text-box >>> .el-textarea__inner{
  height:300px;
}
</style>
