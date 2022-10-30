<template>
  <div id="template-box" ref="tempalteBox" v-html="htmlText">
    <!-- <iframe src="" frameborder="0"></iframe> -->
  </div>
</template>
<script>
export default {
  name: 'TemplateHtml',
  data () {
    return {
      htmlText: ''
    }
  },
  created () {
    let answerId = this.$route.query.answerId
    if (answerId != null) {
      // console.log('answerId:', answerId)
      this.axios({
        url: this.baseUrl + '/answer/search?answerId=' + answerId,
        method: 'get'
      }).then((res) => {
        if (res.data.status === 200) {
          this.htmlText = res.data.data
        } else {
          console.log(res.data.message)
        }
      }).catch((err) => {
        console.log('search answer: ', err)
      })
    } else {
      let questionId = this.$route.query.questionId
      // console.log('questionId：', questionId)
      this.axios({
        url: this.baseUrl + '/question/search?questionId=' + questionId,
        method: 'get'
      }).then((res) => {
        if (res.data.status === 200) {
          this.htmlText = res.data.data
        } else {
          console.log(res.data.message)
        }
      }).catch((err) => {
        console.log('search answer: ', err)
      })
    }
  },
  watch: {
    htmlText: {
      handler (str) {
        if (str == null || str === '') return
        // 获取script标签内的内容
        let reg = /<script[^>]*>([^<]|<(?!\/script))*<\/script>/gmi
        let res = str.match(reg)
        let bodyDom = this.$refs.tempalteBox

        // console.log('匹配的结果:', res)
        res.forEach((ele) => {
          let startIndex = ele.indexOf('>')
          let endIndex = ele.lastIndexOf('<')
          ele = ele.slice(startIndex + 1, endIndex)
          // console.log(ele)

          let scriptDom = document.createElement('script')
          scriptDom.type = 'text/javascript'
          scriptDom.text = ele
          try {
            bodyDom.appendChild(scriptDom)
          } catch (e) {
            console.log('JS代码报错:', e)
          }
        })

        // let scriptNode = document.createElement('script')
        // scriptNode.text = res
        // document.body.appendChild(scriptNode)
      }
    }
  }
}
</script>
<style>
#template-box {
  margin: initial !important;
  padding: initial !important;
  height: 100%;
  border-width: 0px !important;
  border-top: 0;
  margin-top: initial !important;
  margin-left: initial !important;
  margin-right: initial !important;
  margin-bottom: initial !important;
  background-color: inherit !important;

}
</style>
