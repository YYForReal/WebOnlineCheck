<template>
  <div>
    <!--
      <td>原内容</td>
      <td v-text="content"></td>
      <td>过滤的HTML</td>
      <td ref="b" v-text="answerText"></td>
     -->
    <table class="comp-card">
      <tr class="table-header">
        <td v-if="question.example !=null">JS校验代码</td>
        <td v-else class="td-text"><a @click="dialogVisiable = true" style="text-decoration:underline;cursor:pointer">比对结果</a></td>
        <td class="td-pic">{{ visitType === 1 ? '参考运行窗口' : '参考运行截图' }} </td>
        <td class="td-pic" @click="showBigIframe()"><a>{{ visitType === 1 ? '运行窗口(点击放大)' : '运行截图(点我放大)' }}</a></td>
      </tr>
      <tr class="table-content">
        <td v-if="question.example !=null"> {{JSCheckResult}}</td>
        <td v-else class="td-text" >
          <div ref="result" v-html="resultHtml"></div>
        </td>
        <td  v-if="visitType === 1">
          <iframe ref="qiframe" sandbox='allow-scripts allow-same-origin'
            :src="'http://yywebsite.cn/webcheck/#/template?question=' + question.questionId"></iframe>
        </td>
        <td v-else v-loading="!comparePic">
          <el-popover placement="top-start" trigger="click">
            <img :src="comparePic" class="comp-image">
            <img slot="reference" :src="comparePic" class="comp-image">
          </el-popover>
        </td>
        <td  v-if="visitType === 1" @click="showBigIframe()">
          <iframe ref="iframe" sandbox='allow-scripts allow-same-origin'></iframe>
        </td>
        <td v-else v-loading="isLoading" >
          <el-popover placement="top-start" trigger="click">
            <!--trigger属性值：hover、click、focus 和 manual-->
            <img :src="runningPic" class="comp-image">
            <img slot="reference" :src="runningPic" class="comp-image">
          </el-popover>
        </td>
      </tr>
      <!-- <tr class="table-one-header " v-if="visitType == 1">
    </tr>
    <tr v-if="visitType == 1"></tr> -->

    </table>
    <el-dialog title="详细信息" :visible.sync="dialogVisiable" width="50%">
      <h3>原内容</h3>
      <p>{{ content }}</p>
      <h3>过滤的HTML</h3>
      <p>{{ answerText }}</p>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisiable = false">确 定</el-button>
      </span>
    </el-dialog>
    <el-dialog title="点击外部区域即可关闭" :visible.sync="iframeDialogVisiable" width="80%">
      <iframe width="100%" height="700px" ref="bigIframe" sandbox='allow-scripts allow-same-origin'></iframe>
      <span slot="footer" class="dialog-footer">
      </span>
    </el-dialog>

  </div>

</template>
<script>
export default {
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
      dialogVisiable: false,
      iframeDialogVisiable: false
    }
  },
  mounted () {
    setTimeout(() => {
      this.changed()
    }, 800)
  },
  methods: {
    showBigIframe () {
      this.iframeDialogVisiable = true
      setTimeout(() => {
        const bigIframe = this.$refs.bigIframe

        if (bigIframe == null) return
        var iwindow = bigIframe.contentWindow
        var idoc = iwindow.document // 获取iframe的document对象
        idoc.open()
        idoc.write(this.filterScript(this.content))
        idoc.close()
      }, 100)
    },
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
      if (this.visitType === 1) {
        // console.log('showHTML')
        this.showHtml()
      }
    },
    showHtml () {
      // this.$refs.iframe.src = this.content
      const qiframe = this.$refs.qiframe
      const iframe = this.$refs.iframe
      const bigIframe = this.$refs.bigIframe

      // 写入内容到答案的iframe
      if (iframe == null) return
      var iwindow = iframe.contentWindow // 获取iframe的window对象
      var idoc = iwindow.document // 获取iframe的document对象
      idoc.open()
      idoc.write(this.filterScript(this.content))
      idoc.close()

      // 为了同步，同时注入src到问题的参考iframe
      qiframe.src = 'http://yywebsite.cn/webcheck/#/template?questionId=' + this.question.questionId

      if (bigIframe == null) return
      iwindow = bigIframe.contentWindow
      idoc = iwindow.document // 获取iframe的document对象
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
    visitType: {
      type: Number,
      default: 0
    },
    JSCheckResult: {
      type: String,
      default: 'JS尚无比对结果'
    },
    content: {
      type: String,
      defalut: ''
    },
    question: {
      type: Object
    },
    questionText: {
      type: String,
      default: ''
    },
    comparePic: {
      type: String,
      default: null
    },
    runningPic: {
      type: String,
      default: 'https://source.acexy.cn/view/YPIBluo'
    },
    isLoading: {
      type: Boolean,
      defalut: false
    }
  },
  watch: {
    content: function () { },
    score: function (val) {
      this.newScore = val
    },
    comparePic: function () { },
    runningPic: function () { },
    isLoading: function () { },
    visitType: function () {
      setTimeout(() => {
        this.showHtml()
      }, 500)
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
.comp-card {
  margin: 0 auto;
}

.comp-card>>>tr {
  width: 100%;
}

.comp-card>>>tr td {
  border: 1px solid #000000e6;
  overflow-wrap: anywhere;
  min-height: 200px;
}

.comp-card>>>del {
  background: #ff0000bf;
  text-decoration: none;
}

.comp-card>>>ins {
  background: #00ff22c7;
  text-decoration: none;
}

.comp-card>>>iframe {
  width: 100%;
  height: 100%;
  margin: 0;
  min-height: 100px;
  max-height: 400px;
  overflow: scroll;
}

.comp-card>>>.table-header {
  height: 50px;
  line-height: 50px;
  text-align: center;
  /* background-color: rgba(0, 255, 255, 0.718); */
  background-color: rgb(24, 226, 230);
}

.comp-card .table-header .td-text {
  width: 250px;
  max-width: 350px;
}

.comp-card .table-header .td-pic {
  min-width: 350px;
}

.comp-card>>>.table-one-header {
  height: 38px;
  width: 100%;
  line-height: 38px;
  text-align: center;
  background-color: rgb(24, 226, 230);
}

.comp-card .comp-image {
  width: 100%;
  min-height: 100px;
}

.table-content .td-text {
  font-size: 0.9rem;
  width: 21%;
  max-width: 350px;
}

.table-content .td-text div {
  max-height: 500px;
  overflow: scroll;
}

.table-content td {
  width: 38%;
  height: 300px;
}

/* 缩方iframe的内容 */
/* iframe {
  width: 133%!important;
  height: 133%!important;
  -ms-zoom: 0.75;
  -moz-transform: scale(0.75);
  -moz-transform-origin: 0 0;
  -o-transform: scale(0.75);
  -o-transform-origin: 0 0;
  -webkit-transform: scale(0.75);
  -webkit-transform-origin: 0 0;
} */
</style>
