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
      isQuestion: false,
      answerId: null,
      user: '',
      username: ''
    }
  },
  mounted () {
    let answerId = this.$route.query.answer || this.$route.query.answerId
    this.answerId = answerId
    let questionId = this.$route.query.question || this.$route.query.questionId
    this.user = decodeURIComponent(this.$route.query.user) || this.account.userid
    this.username = decodeURIComponent(this.$route.query.name) || this.account.username

    window.onbeforeunload = function () {
      debugger
    }

    if (answerId != null) {
      this.requestStudentCode(answerId)
      // this.requestCheckCode(questionId).then(() => {
      //   this.requestStudentCode(answerId)
      // })
    } else {
      this.isQuestion = true
      // console.log('这是问题ID:', questionId)
      this.requestQuestionCode(questionId)
    }
  },
  methods: {
    changeScore () {
      this.axios({
        url: this.baseUrl + '/answer/score/default',
        method: 'post',
        transformRequest: [(data) => {
          var oMyForm = new FormData()
          oMyForm.append('answerId', this.answerId)
          oMyForm.append('userId', this.user)
          oMyForm.append('username', this.username)
          return oMyForm
        }]
      }).then((res) => {
        if (res.data === 'OK') {
          // console.log('judge success')
          // this.$message({
          //   type: 'success',
          //   message: '评分成功',
          //   showClose: true
          // })
          // this.$emit('changeScore', this.answerId, this.formInline.newScore)
        } else {
          // console.log('judge error')
          // this.$message({
          //   type: 'error',
          //   message: '评分失败',
          //   showClose: true
          // })
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
    // requestCheckCode (questionId) {
    //   return new Promise((resolve, reject) => {
    //     this.axios({
    //       url: this.baseUrl + '/code/get?questionId=' + questionId,
    //       method: 'get'
    //     }).then((res) => {
    //       if (res.data.status === 200) {
    //         // console.log('checkCode res:', res.data.data)
    //         // this.htmlText = res.data.data
    //         this.checkPoints = res.data.data
    //         resolve()
    //       } else {
    //         console.log(res.data.message)
    //         reject(res.data.message)
    //       }
    //     }).catch((err) => {
    //       console.log('校验代码请求失败: ', err)
    //       reject(err)
    //     })
    //   })
    // },
    requestStudentCode (answerId) {
      this.axios({
        url: this.baseUrl + '/answer/search?answerId=' + answerId,
        method: 'get'
      }).then((res) => {
        if (res.data.status === 200) {
          this.checkPoints = res.data.data.codes
          this.htmlText = res.data.data.content
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
        // console.log('body:', bodyDom)
        // console.log('匹配的结果:', res)
        // 如果具有script标签
        if (res != null) {
          res.forEach((ele) => {
            let startIndex = ele.indexOf('>')
            let endIndex = ele.lastIndexOf('<')
            ele = ele.slice(startIndex + 1, endIndex)
            // console.log(ele)

            this.studentScript += ele
            // 如果是问题的参考代码 || 没有校验代码，则直接加入script，无需校验
            if (this.isQuestion === true || this.checkPoints.length === 0) {
              let scriptDom = document.createElement('script')
              scriptDom.type = 'text/javascript'
              scriptDom.text = ele
              // console.log('scriptDom:', scriptDom)
              setTimeout(() => {
                try {
                  bodyDom.appendChild(scriptDom)
                } catch (e) {
                  console.log('JS代码报错:', e)
                }
              }, 100)
            }
          })
        }

        // 增加分号，防止代码运行时报错
        // this.studentScript.replaceAll('\n', ';')
        this.studentScript.replace(/\n/g, ';')
        let checkLen = this.checkPoints.length
        if (checkLen > 0) {
          for (let i = 0; i < checkLen; i++) {
            this.checkPoints[i].code = this.checkPoints[i].code.replace('//StudentCode', this.studentScript)
            // console.log('Check Code :', this.checkPoints[i].code)

            setTimeout(() => {
              try {
                let failTip = '代码检测不通过：<br>'
                let successTip = '代码检测通过<br>'
                new Promise((resolve, reject) => {
                  // 在执行校验脚本代码前 提供resolve、reject、this、及学生脚本变量

                  // eslint-disable-next-line no-unused-vars
                  var studentScript = this.studentScript
                  // eslint-disable-next-line no-unused-vars
                  var that = this

                  // eslint-disable-next-line no-eval
                  eval(this.checkPoints[i].code)
                }).then((value) => {
                  // 执行成功
                  this.postResult(successTip + value).then(() => {
                    console.log(' (Promise)： Post success success')
                    this.$message({
                      type: 'success',
                      message: '代码校验完成'
                    })
                    this.changeScore()
                  }).catch(() => {
                    console.log('(Resolve)： Post success error')
                    this.$message({
                      type: 'error',
                      message: '代码校验失败'
                    })
                  }, (reason) => {
                    // 执行失败
                    this.postResult(failTip + reason).then(() => {
                      console.log('(Reject)： Post 0')
                      if (reason === '找不到检测的对象') {
                        this.$message({
                          type: 'error',
                          message: '找不到检测的对象，请使用模板，不改id ！！'
                        })
                      } else if (reason.indexOf('undefined') !== -1) {
                        this.$message({
                          type: 'error',
                          duration: 5000,
                          message: '存在undefined。可能原因：“变量未声明”、“函数未提升作用域”或“修改了DOM结构”'
                        })
                      } else {
                        this.$message({
                          type: 'success',
                          message: '代码校验完成'
                        })
                      }
                    }).catch(() => {
                      console.log('(Catch)： Post error error')
                      this.$message({
                        type: 'error',
                        message: '代码校验失败'
                      })
                    })
                  })
                }).catch((e) => {
                  // 执行失败
                  this.postResult('校验学生JS代码异常：<br>' + e).then(() => {
                    this.$message({
                      type: 'success',
                      message: '代码校验完成'
                    })
                    console.log('Catch ： Post error success')
                  }).catch((reason) => {
                    this.$message({
                      type: 'error',
                      message: '代码校验失败'
                    })
                    console.log('2. Catch ： Post error error')
                  })
                })
              } catch (e) {
                console.log(e)
                // 执行失败
                this.postResult('执行校验JS代码出错<br>' + e).then(() => {
                  // console.clear()
                  console.log('Post error success')
                }).catch(() => {
                  // console.clear()
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
