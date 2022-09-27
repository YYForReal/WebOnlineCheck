<template>
  <el-card class="judge-card">
    <div class="student-info-box">
      <el-form :inline="true" :model="formInline">
        <el-form-item label="学号">
          <el-input v-model="userId" disabled="disabled"></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="username" disabled="disabled"></el-input>
        </el-form-item>
        <el-form-item label="原分数">
          <el-input v-model="score" disabled="disabled"></el-input>
        </el-form-item>
        <el-form-item label="分数">
          <el-input v-model="formInline.newScore" type="number"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">评分</el-button>
        </el-form-item>
      </el-form>
    </div>
    <table>
      <tr class="table-header">
        <td>原内容</td>
        <td>过滤的HTML</td>
        <td>比对结果</td>
      </tr>
      <tr class="table-content">
        <td v-text="content"></td>
        <td ref="b" v-text="answerText"></td>
        <td>
          <div ref="result" v-html="resultHtml"></div>
        </td>
      </tr>
      <tr class="table-one-header ">
        <td colspan="3">HTML效果</td>
      </tr>
      <tr>
        <td colspan="3">
          <iframe ref="iframe"></iframe>
        </td>
      </tr>
    </table>

  </el-card>
</template>

<script>
export default {
  name: 'Judge',
  data () {
    return {
      // 配置需提前替换html文本内的转义符号
      replaceTags: [
        ['&lt;', '<'],
        ['&gt;', '>'],
        ['&#39;', '\''],
        ['&quot;', '"'],
        ['&amp;', '&'],
        ['&nbsp;', ' '],
        ['&copy;', '©'],
        // [' ',''], //注释该行，则表示保留空格
        ['\r', ''],
        ['\n', '']
        // [' ','']
      ],
      answerText: '此处显示比对的html文本内容',
      compText: '',
      resultHtml: '此处显示比对结果',
      formInline: {
        newScore: 100
      }
    }
  },
  mounted () {
    setTimeout(() => {
      this.changed()
    }, 500)
  },
  methods: {
    delHtmlTag (str) {
      // 去除所有箭头
      str = str.replace(/<[^>]+>/g, '')
      // 替换所有的转义符号
      this.replaceTags.forEach(element => {
        str = str.replace(new RegExp(element[0], 'gm'), element[1])
      })
      str = str.split('')
      // 过滤 回车符号 和 换行符号
      str = str.filter((char) => {
        return char !== '\r' && char !== '\n' && char !== ' '
      })
      str = str.join('')
      // 去除所有空格
      return str
    },
    changed () {
      this.compText = this.delHtmlTag(this.questionText)
      this.answerText = this.delHtmlTag(this.filterScript(this.filterBody(this.content)))
      // eslint-disable-next-line no-undef
      var diff = JsDiff['diffChars'](this.compText, this.answerText)
      var arr = []
      for (var i = 0; i < diff.length; i++) {
        if (diff[i].added && diff[i + 1] && diff[i + 1].removed) {
          var swap = diff[i]
          diff[i] = diff[i + 1]
          diff[i + 1] = swap
        }
        var diffObj = diff[i]
        var newContent = diffObj.value

        if (diffObj.removed) {
          arr.push('<del title="删除的部分">' + newContent + '</del>')
        } else if (diffObj.added) {
          arr.push('<ins title="新增的部分">' + newContent + '</ins>')
        } else {
          // 没有改动的部分
          arr.push('<span title="没有改动的部分">' + newContent + '</span>')
        }
      }
      var html = arr.join('')
      this.resultHtml = html
      // 在iframe里面显示效果
      this.showHtml()
    },
    onSubmit () {
      if (this.account == null) {
        this.$message({
          type: 'error',
          message: '请先登录',
          showClose: true
        })
        return
      } else if (this.isTeacher === false) {
        this.$message({
          type: 'error',
          message: '老师才能改分呐',
          showClose: true
        })
        return
      }
      // TODO 改分
      this.axios({
        url: this.baseUrl + '/user/check',
        method: 'post',
        transformRequest: [(data) => {
          var oMyForm = new FormData()
          oMyForm.append('userId', this.account.userid)
          oMyForm.append('username', this.account.username)

          return oMyForm
        }]
      }).then((res) => {
        if (res.data === 'OK') {
          this.changeScore()
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
          message: '网络异常',
          showClose: true
        })
      })
    },
    changeScore () {
      this.axios({
        url: this.baseUrl + '/answer/score',
        method: 'post',
        transformRequest: [(data) => {
          var oMyForm = new FormData()
          oMyForm.append('answerId', this.answerId)
          oMyForm.append('score', this.formInline.newScore)
          oMyForm.append('userId', this.account.userid)
          oMyForm.append('username', this.account.username)

          return oMyForm
        }]
      }).then((res) => {
        if (res.data === 'OK') {
          this.$message({
            type: 'success',
            message: '评分成功',
            showClose: true
          })
          this.$emit('changeScore', this.answerId, this.formInline.newScore)
        } else {
          this.$message({
            type: 'error',
            message: '评分失败',
            showClose: true
          })
        }
      }).catch(err => {
        this.$message({
          type: 'error',
          message: '网络异常',
          showClose: true
        })
        console.log(err)
      })
    },
    showHtml () {
      // this.$refs.iframe.src = this.content
      var iwindow = this.$refs.iframe.contentWindow // 获取iframe的window对象
      var idoc = iwindow.document // 获取iframe的document对象
      idoc.open()
      idoc.write(this.filterScript(this.content))
      idoc.close()
    },
    filterBody (str) {
      let index = str.indexOf('<body')
      if (index !== -1) {
        str = str.slice(index)
      }
      return str
    },
    filterScript (str) {
      // str = str.replace(/<script[^<]+<\/script>/gims, '')
      // eslint-disable-next-line no-useless-escape
      str = str.replace(new RegExp('<script[^<]+<\/script>', 'gims'), '')
      return str
    }
  },
  props: {
    answerId: {
      type: Number,
      default: 0
    },
    content: {
      type: String,
      defalut: ''
    },
    score: {
      type: Number,
      default: 0
    },
    questionText: {
      type: String,
      default: ''
    },
    userId: {
      type: String,
      default: ''
    },
    username: {
      type: String,
      default: ''
    }
  },
  watch: {
    answerId: function () {
    },
    content: function () {
    },
    score: function (val) {
      this.newScore = val
    }
  },
  computed: {
    account () {
      return this.$store.state.account
    },
    isTeacher () {
      return this.$store.state.isTeacher
    }
  }
}
</script>

<style scoped>
.judge-card {
  /* justify-content: center; */
  /* align-items: center; */
  width: 95%;
  border-radius: 5px;
  /* background-color: rgba(240, 250, 255, 0.593); */
  min-height: 300px;
  margin: 5px auto;
  margin-bottom: 30px;
  padding-bottom: 5px;
  /* border: 1px dotted dodgerblue; */
}

.judge-card .table {
  flex-direction: row;
  display: -webkit-flex;
  display: flex;
  height: 100%;
  -webkit-flex-wrap: wrap;
  flex-wrap: wrap;
}

.judge-card >>> .student-info-box {
  width: 100%;
  text-align: center;
  margin-top: 10px;
}

.judge-card >>> tr {
  width: 100%;
}

.judge-card >>> tr td {
  width: 400px;
  border: 1px solid #000000e6;
  overflow-wrap: anywhere;
}

.judge-card >>> del {
  background: #ff0000bf;
  text-decoration: none;
}

.judge-card >>> ins {
  background: #00ff22c7;
  text-decoration: none;
}

.judge-card >>> iframe {
  width: 100%;
  height:100%;
  margin:0;
  min-height: 100px;
  max-height: 400px;
  overflow: scroll;
}

.judge-card >>> .table-header {
  height: 50px;
  line-height: 50px;
  text-align: center;
  background-color: rgba(0, 255, 255, 0.718);
}

.judge-card >>> .table-one-header {
  height: 38px;
  width:100%;
  line-height: 38px;
  text-align: center;
  background-color: rgba(0, 255, 255, 0.718);
}
</style>
