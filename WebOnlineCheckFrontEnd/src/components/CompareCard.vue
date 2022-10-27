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
        <td class="td-text"><a @click="dialogVisiable = true" style="text-decoration:underline;cursor:pointer">比对结果</a></td>
        <td class="td-pic">参考运行截图 </td>
        <td class="td-pic">运行截图</td>
      </tr>
      <tr class="table-content">
        <td class="td-text">
          <div ref="result" v-html="resultHtml"></div>
        </td>
        <td v-loading="!comparePic">
          <el-popover placement="top-start" trigger="click">
            <img :src="comparePic" class="comp-image">
            <img slot="reference" :src="comparePic" class="comp-image">
          </el-popover>
        </td>
        <td v-loading="isLoading">
          <!-- 不使用iframe进行观察，替换为运行截图 -->
          <!-- <iframe ref="iframe"></iframe> -->
          <el-popover placement="top-start" trigger="click">
            <!--trigger属性值：hover、click、focus 和 manual-->
            <img :src="runningPic" class="comp-image">
            <img slot="reference" :src="runningPic" class="comp-image">
          </el-popover>
        </td>
      </tr>
      <!-- <tr class="table-one-header " v-if="visitType == 1">
    </tr>
    <tr v-if="visitType == 1">

    </tr> -->
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
      dialogVisiable: false
    }
  },
  mounted () {
    setTimeout(() => {
      this.changed()
    }, 800)
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
      // this.showHtml()
    },
    showHtml () {
      // this.$refs.iframe.src = this.content
      const iframe = this.$refs.iframe
      if (iframe == null) return
      var iwindow = iframe.contentWindow // 获取iframe的window对象
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
    visitType: {
      type: Number,
      default: 0
    },
    content: {
      type: String,
      defalut: ''
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
    isLoading: function () { }
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
  width: 21%;
  max-width: 350px;
}

.table-content td {
  width: 38%;
  height: 300px;
}
</style>
