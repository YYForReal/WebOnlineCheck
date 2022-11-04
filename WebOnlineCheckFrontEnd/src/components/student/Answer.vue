<template>
  <el-form ref="AnswerFrom" :model="form" :rules="rules" class="login-container">
    <h2>实验选择</h2>
    <div class="inline-box">
      <el-form-item prop="questionId">
        <el-select v-model="form.questionId" placeholder="请选择实验题目">
          <el-option v-for="item in questionList" :key="item.questionId" :label="item.title" :value="item.questionId" />
        </el-select>
      </el-form-item>
      <el-form-item class="submit-btn-box">
        <el-button type="primary" @click="handleSumbit" :loading="logining">提交</el-button>
      </el-form-item>
    </div>
    <blockquote class="question-description" v-if="(nowQuestion && nowQuestion.description)">
      {{ nowQuestion.description }}
    </blockquote>
    <div class="question-example-box code-box" v-if="(nowQuestion && nowQuestion.example)" >
      <el-button type="primary" class="copy-btn" @click="copyCode(nowQuestion.example)">复制代码</el-button>
      <pre>
        <code>{{ nowQuestion.example | trim }}</code>
      </pre>
    </div>
    <h2>提交答案</h2>
    <el-form-item prop="content">
      <el-input class="html-text-box" type="textarea" v-model="form.content" auto-complete="off" resize="none"
        placeholder="在此输入html"></el-input>
    </el-form-item>

    <el-badge value="new" class="item">
      <el-button type="info" class="fix-button" icon="el-icon-message" @click="dialogVisible = true" circle></el-button>
    </el-badge>

    <el-dialog title="一些无法展示效果的案例" :visible.sync="dialogVisible" width="80%">
      <el-carousel :interval="4000">
        <!-- v-for="answer in scoreList" :key="answer.answerId" :title="answer.questionTitle" -->
        <!-- :name="answer.answerId" -->
        <el-carousel-item v-for="(tip, index) in dialogTips" :key="index" :title="tip.title">
          <!-- <el-divider content-position="left" >{{}}</el-divider> -->
          <h2>{{ tip.title }}</h2>
          <p>{{ tip.content }}</p>
          <div class="code-box-container">
            <div class="code-box">
              <p v-if="tip.errorCode">冲突代码</p>
              <el-input type="textarea" disabled rows="8" v-if="tip.errorCode" v-model="tip.errorCode"></el-input>
            </div>
            <div class="code-box">
              <p v-if="tip.rightCode">有效代码</p>
              <el-input type="textarea" disabled rows="8" v-if="tip.rightCode" v-model="tip.rightCode"></el-input>
            </div>
          </div>
        </el-carousel-item>
        <!-- <el-divider><i class="el-icon-mobile-phone"></i></el-divider> -->
        <!-- <el-divider content-position="right">阿里云</el-divider> -->
      </el-carousel>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
      </span>
    </el-dialog>

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
      questionList: [],
      // 弹窗
      dialogVisible: false,
      dialogTips: [
        {
          title: 'div选择器的问题',
          content: '因为预览的HTML结构最外层套了两层的div，所以为了避免影响外部div的样式。尽量减少使用div标签选择器',
          errorCode: '<style>\n\tdiv{\n\t\t...\n\t}\n</style>\n\n<div class="box"></div>',
          rightCode: '<style>\n\t.box{\n\t\t...\n\t}\n</style>\n\n<div class="box"></div>'
        },
        {
          title: 'body标签的行内样式',
          content: '不要把body的样式写在body标签内，可以考虑写在内部样式表内。',
          errorCode: '<body bgcolor="#123456"></body>',
          rightCode: `<style>\n\tbody{\n\t\tbackground-color:"#123456";\n\t}\n</style>`
        },
        {
          title: '内部样式表的大小写问题',
          content: '如果内部样式表和HTML标签大小写不匹配，也会没有效果。HTML可以全用小写字母和字符“-”',
          errorCode: '<style>\n\t#hello{\n\t\t...\n\t}\n</style>\n\n<p id="#Hello"></p>',
          rightCode: '<style>\n\t#hello-box{\n\t\t...\n\t}\n</style>\n\n<p id="#hello-box"></p>'
        }
      ]
    }
  },
  components: {

  },
  filters: {
    trim: function (value) {
      console.log('trim:', value)
      console.log(value.trim())
      return value.trim()
    }
  },
  mounted () {
    this.refreshQuestionList()
  },
  methods: {
    copyCode (val) {
      // 模拟 输入框
      var cInput = document.createElement('textarea')
      cInput.value = val
      document.body.appendChild(cInput)
      cInput.select() // 选取文本框内容
      document.execCommand('copy')
      this.$message({
        type: 'success',
        message: '复制成功'
      })
      // 复制成功后再将构造的标签 移除
      document.body.removeChild(cInput)
    },
    refreshQuestionList () {
      this.axios({
        url: this.baseUrl + '/question/getlist',
        method: 'get'
      }).then((res) => {
        this.questionList = res.data
      }).catch((err) => {
        console.log('search question: ', err)
      })
    },
    handleSumbit () {
      this.$refs.AnswerFrom.validate((valid) => {
        if (valid) {
          this.checkUser().then((res) => {
            if (res.data === 'OK') {
              this.form.userId = this.account.userid
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
    },
    // 当前选中的问题
    nowQuestion () {
      for (let i = 0; i < this.questionList.length; i++) {
        if (this.questionList[i].questionId === this.form.questionId) {
          return this.questionList[i]
        }
      }
      return null
    }
  }
}
</script>

<style  scoped>
body {
  background: aquamarine;
}

h2,
h3 {
  margin: 18px 0;
}

.login-container {
  width: 70%;
  min-width: 300px;
  text-align: center;
  margin: 0 auto;
}

.html-text-box {
  width: 100%;
  min-width: 300px;
  min-height: 300px;
}

.html-text-box>>>.el-textarea__inner {
  height: 400px;
}

.inline-box {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;

}

.submit-btn-box {
  margin-left: 30px;
}

/* .fix-button {

} */
.code-box-container {
  display: flex;
  width: 80%;
  margin: 0 auto;
  justify-content: space-between;
}

.code-box {
  width: 45%;
}

.item {
  position: fixed;
  width: 50px;
  height: 50px;
  font-size: 24px;
  right: 50px;
  bottom: 120px;
  /* padding-top: 10px; */
  /* padding-right: 40px; */
}

.item>>>.el-badge__content.is-fixed {
  right: 20px;
}

/* blockquote */

blockquote ul,
blockquote ol {
  margin-left: 0;
}

blockquote {
  border-color: #bababa;
}

blockquote:before,
blockquote:after {
  content: '';
  content: none;
}

/*代码块*/
pre code {
  font-family: Consolas, Inconsolata, Courier, monospace;
  border-radius: 0px;
  text-align: left;
}

blockquote {
  /*引用块*/
  display: block;
  padding: 15px 1rem;
  font-size: 0.9em;
  padding-right: 15px;
  margin: 1em 0;

  /*更改默认值*/
  color: #000000;
  border-left: 5px solid rgb(239, 112, 96);
  background: #EFEBE9;

  overflow: auto;
  word-wrap: normal;
  word-break: normal;
}

blockquote p {
  margin: 0px;
}

/* code */
/* =======================================*/
.question-example-box{
  position: relative;
  width:100%;
  text-align: initial;
}

.copy-btn{
  position: absolute;
  right: 0px;
  top:0px;
}

</style>
