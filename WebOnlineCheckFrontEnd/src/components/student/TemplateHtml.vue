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
      htmlText: '',
      studentScript: '',
      checkPoints: [],
      code: '',
      isQuestion: false
    }
  },
  mounted () {
    let answerId = this.$route.query.answer || this.$route.query.answerId
    let questionId = this.$route.query.question || this.$route.query.questionId
    if (answerId != null) {
      this.requestCheckCode(questionId).then(() => {
        console.log('请求校验代码成功')
        this.requestStudentCode(answerId)
      })
    } else {
      this.isQuestion = true
      console.log('这是问题ID:', questionId)
      this.requestQuestionCode(questionId)
    }
  },
  methods: {
    requestCheckCode (questionId) {
      return new Promise((resolve, reject) => {
        this.axios({
          url: this.baseUrl + '/code/get?questionId=' + questionId,
          method: 'get'
        }).then((res) => {
          if (res.data.status === 200) {
            console.log('checkCode res:', res.data.data)
            // this.htmlText = res.data.data
            this.checkPoints = res.data.data
            resolve()
          } else {
            console.log(res.data.message)
            reject(res.data.message)
          }
        }).catch((err) => {
          console.log('search answer: ', err)
          reject(err)
        })
      })
    },
    requestStudentCode (answerId) {
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
    },
    requestQuestionCode (questionId) {
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
        console.log('search question: ', err)
      })
    },
    postResult (content) {
      return this.axios({
        url: this.baseUrl + '/code/result/post',
        method: 'post',
        transformRequest: [(data) => {
          var oMyForm = new FormData()
          oMyForm.append('questionId', this.$route.query.question)
          oMyForm.append('answerId', this.$route.query.answer)
          oMyForm.append('content', content)
          return oMyForm
        }]
      })
    },
    checkCode (jsCode) {
      // let bodyDom = this.$refs.tempalteBox
      // try {
      //   // eslint-disable-next-line no-eval
      //   eval(jsCode)
      // } catch (err) {
      //   console.log(err)
      // }
      // for(let i=0;i<navigator;i++){
      //   check
      // }
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
        console.log('body:', bodyDom)
        // console.log('匹配的结果:', res)
        res.forEach((ele) => {
          let startIndex = ele.indexOf('>')
          let endIndex = ele.lastIndexOf('<')
          ele = ele.slice(startIndex + 1, endIndex)
          // console.log(ele)

          this.studentScript += ele
          // 如果是问题的参考代码，则直接加入script，无需校验
          if (this.isQuestion === true) {
            let scriptDom = document.createElement('script')
            scriptDom.type = 'text/javascript'
            scriptDom.text = ele
            console.log('scriptDom:', scriptDom)
            setTimeout(() => {
              try {
                bodyDom.appendChild(scriptDom)
              } catch (e) {
                console.log('JS代码报错:', e)
              }
            }, 100)
          }
        })

        let checkLen = this.checkPoints.length
        if (checkLen > 0) {
          for (let i = 0; i < checkLen; i++) {
            this.checkPoints[i].code = this.checkPoints[i].code.replace('//StudentCode', this.studentScript)
            console.log('Check Code :', this.checkPoints[i].code)

            setTimeout(() => {
              try {
                // eslint-disable-next-line no-eval
                eval(this.checkPoints[i].code)
                // 执行成功
                this.postResult('代码检测通过').then(() => {
                  console.log('Post success success')
                }).catch(() => {
                  console.log('Post success error')
                })
              } catch (e) {
                console.log(e)
                // 执行失败
                this.postResult('代码检测不通过：\n' + e).then(() => {
                  console.log('Post error success')
                }).catch(() => {
                  console.log('Post error error')
                })
              }
            }, 500)

            // let scriptDom = document.createElement('script')
            // scriptDom.type = 'text/javascript'
            // scriptDom.setAttribute('defer', 'defer')
            // scriptDom.text = this.checkPoints[i].code
            // console.log('scriptDom:', scriptDom)
            // try {
            //   bodyDom.appendChild(scriptDom)
            //   console.log('执行成功' + this.checkPoints[i].code)
            // } catch (e) {
            //   console.log('JS代码报错:', e)
            // }
          }
        }

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
  width: inherit !important;
  border-width: 0px !important;
  border-top: 0;
  margin-top: initial !important;
  margin-left: initial !important;
  margin-right: initial !important;
  margin-bottom: initial !important;
  background-color: inherit !important;
  background: inherit !important;
  animation: none !important;
  -moz-animation: none !important;
  -webkit-animation: none !important;
  -o-animation: none !important;
  position: inherit;
  color: inherit !important;
  text-align: inherit !important;
}
</style>
