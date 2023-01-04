<template>
  <el-card class="judge-card">
    <div class="star" ref="star" :class="{ 'active-star': active }" @mouseenter="active = true"
      @mouseleave="active = false">
      <p>{{ showQId }}</p>
    </div>
    <div class="student-info-box">
      <el-form :inline="true" :model="formInline">
        <el-form-item label="分数">
          <el-input :id="'score-dom-' + answer.answerId" ref="scoreDom" v-model="formInline.newScore" type="number"
            @keyup.enter.native="onSubmit" autoComplete></el-input>
        </el-form-item>
        <!-- <el-form-item>
          <el-button type="primary" @click="onSubmit">评分</el-button>
        </el-form-item> -->
        <el-form-item label="学号">
          <!-- <span class="form-span" >{{userId}}</span> -->

          <el-input class="form-span" v-model="answer.userId" disabled="disabled"></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <!-- <span class="form-span" >{{username}}</span> -->
          <el-input class="form-span" v-model="answer.username" disabled="disabled"></el-input>
        </el-form-item>
        <!-- <el-form-item label="原分数">
          <el-input v-model="score" disabled="disabled"></el-input>
        </el-form-item> -->

        <el-form-item label="相似度:">
          <!-- <span class="form-span"  v-loading="!similarity">{{similarity}}</span> -->
          <!-- similarity -->
          <el-input class="form-span" v-loading="isLoading2" v-model="similarity" disabled="disabled"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="picCompare">比对刷新</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="viewEffect">校验效果</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="viewOriginalEffect">原生效果</el-button>
        </el-form-item>

        <p class="form-span" :class="{ 'delay-submit': answer.updateTime > question.ddl }">{{
          formatTime(answer.updateTime)
        }}</p>

      </el-form>
    </div>
    <CompareCard :visitType="question.display == 1 ? 1 : 0" :content="answer.content" :questionText="question.content"
      :comparePic="basePicUrl2" :runningPic="basePicUrl" :isLoading="isLoading" :question="question"
      :JSCheckResult="JSCheckResult" ref="compCard" :compareSetting="compareSetting" />
  </el-card>
</template>

<script>
import { MyFilter } from '@/utils/myFilter.js'
import CompareCard from '../CompareCard.vue'
export default {
  name: 'Judge',
  data () {
    return {
      active: false,
      formInline: {
        newScore: 100
      },
      basePicUrl: 'https://source.acexy.cn/view/YPIBluo',
      similarity: null,
      isLoading: false,
      isLoading2: false,
      JSCheckResult: '',
      failTimes: 1
    }
  },
  components: {
    CompareCard
  },
  mounted () {
    if (this.autoCompare) {
      this.picCompare()
    }
    if (this.question.example != null) {
      this.askForCheck(this.question.questionId, this.answer.answerId)
    }
    if (this.answer.score != null) {
      this.formInline.newScore = this.answer.score
    }
  },
  methods: {

    formatTime (time) {
      return MyFilter.dateFormat(time)
    },
    viewOriginalEffect () {
      const newWindow = window.open()
      newWindow.document.write(this.answer.content)
    },
    viewEffect () {
      let url = 'http://yywebsite.cn/webcheck/#/template'
      // let url = 'http://localhost:8080/#/template'

      let answerStr = '?answer=' + this.answer.answerId
      let questionStr = '&question=' + this.question.questionId
      // if (this.account) {
      //   questionStr += '&user=' + encodeURIComponent(this.account.userid)
      //   questionStr += '&name=' + encodeURIComponent(this.account.username)
      // }

      window.open(url + answerStr + questionStr, '_blank') // 在一个新页面打开
    },
    askForSimilarity (pic1, pic2) {
      this.axios({
        url: this.baseUrl + '/answer/newcompare',
        method: 'post',
        transformRequest: [(data) => {
          var oMyForm = new FormData()
          oMyForm.append('pic1', pic1)
          oMyForm.append('pic2', pic2)
          return oMyForm
        }]
      }).then((res) => {
        // this.$message({
        //   type: 'success',
        //   message: res.data.message
        // })
        this.similarity = res.data.data * 100
        this.isLoading2 = false

        this.saveCache(this.answer.answerId, pic1, this.similarity)
      }).catch((err) => {
        console.log('request /answer/newcompare: ', err)
        this.isLoading2 = false
      })
    },
    saveCache (answerId, baseStr, similarity) {
      this.axios({
        url: this.baseUrl + '/img/answer/post',
        method: 'post',
        transformRequest: [(data) => {
          var oMyForm = new FormData()
          oMyForm.append('answerId', answerId)
          oMyForm.append('baseStr', baseStr)
          oMyForm.append('similarity', similarity)
          return oMyForm
        }]
      }).then((res) => {
        console.log(answerId + ' 缓存 ' + res.data.message)
      }).catch((err) => {
        console.log('request /answer/newcompare: ', err)
        this.isLoading2 = false
      })
    },
    picCompare () {
      this.isLoading = true
      this.isLoading2 = true
      if (this.autoCache) {
        this.axios.get(this.baseUrl + '/img/answer/get?answerId=' + this.answer.answerId)
          .then(res => {
            if (res.status === 200 && res.data.status === 200) {
              this.isLoading = false
              this.$emit('completeCompare')
              let imgCache = res.data.data
              console.log('imgCache:', imgCache)
              let nowTime = new Date()
              console.log('nowTime:', nowTime)

              if (imgCache != null) {
                this.basePicUrl = imgCache.baseStr
                this.similarity = imgCache.similarity
                this.isLoading = false
                this.isLoading2 = false
                console.log(nowTime < imgCache.date)
                // this.askForSimilarity(this.basePicUrl, this.basePicUrl2)
                // if (this.question.example != null) {
                //   this.askForCheck(this.question.questionId, this.answer.answerId)
                // }
              } else {
                this.askForNewPic()
              }
            }
          })
      } else {
        this.askForNewPic()
      }
    },
    askForNewPic () {
      // 请求获取新图片
      // bug : 注意这里第二个参数后的question会被切为表单的另外一项
      // 当前解决方案：依旧只传入一个参数，让目的Url通过该参数请求重新获取questionid
      let pageUrl = 'http://yywebsite.cn/webcheck/#/template' + '?answer=' + this.answer.answerId + '&question=' + this.question.questionId
      let width = this.compareSetting.width
      let height = this.compareSetting.height
      let timeout = 50000
      let delay = this.compareSetting.delay
      this.axios(
        {
          method: 'POST',
          headers: { 'content-type': 'application/x-www-form-urlencoded' },
          data: `pageUrl=${pageUrl}&width=${width}&height=${height}&timeout=${timeout}&delay=${delay}`,
          url: 'http://118.31.165.150:3000/api/img'
        }
      ).then((res) => {
        this.isLoading = false
        this.$emit('completeCompare')
        // console.log('api/img:', res)
        if (res.data.code === 0) {
          // 获取该答案的base转码
          this.basePicUrl = res.data.data.image
          // console.log('图像的base转码为:', this.basePicUrl)
          // 获取问题的base转码
          // this.basePicUrl2 = this.$emit('getQuestionBase')
          // console.log('答案的base转码为:', this.basePicUrl2)

          this.askForSimilarity(this.basePicUrl, this.basePicUrl2)
          if (this.question.example != null) {
            this.askForCheck(this.question.questionId, this.answer.answerId)
          }
        } else {
          console.log(res.data.message)
        }
      }).catch(() => {
        this.isLoading = false
        this.isLoading2 = false
        this.$message({
          type: 'error',
          message: '网络异常',
          showClose: true
        })
      })
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
          oMyForm.append('answerId', this.answer.answerId)
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
          this.$emit('changeScore', this.answer.answerId, this.formInline.newScore)
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
    askForCheck (questionId, answerId) {
      return this.axios({
        url: this.baseUrl + '/code/result/get?questionId=' + questionId + '&answerId=' + answerId,
        method: 'get'
      }).then((res) => {
        if (res.data.status === 400) {
          this.JSCheckResult = res.data.message
        } else if (res.data.status === 200) {
          this.JSCheckResult = res.data.data
        }
      }).catch((err) => {
        console.log('Search Result：', err)
        setTimeout(() => {
          this.failTimes = this.failTimes * 2
          this.askForCheck(questionId, answerId)
        }, this.failTimes * 1000)
      })
    }
  },
  props: {
    score: {
      type: Number,
      default: 0
    },
    answer: {
      type: Object
    },
    question: {
      tyep: Object
    },
    // questionText: {
    //   type: String,
    //   default: ''
    // },
    // questionTitle: {
    //   type: String,
    //   default: '123123'
    // },
    // userId: {
    //   type: String,
    //   default: ''
    // },
    // username: {
    //   type: String,
    //   default: ''
    // },
    // questionId: {
    //   type: Number,
    //   default: null
    // },
    basePicUrl2: {
      type: String,
      default: null
    },
    autoCompare: {
      type: Boolean,
      default: false
    },
    autoCache: {
      type: Boolean,
      default: true
    },
    // updateTime: {
    //   type: String,
    //   default: ''
    // },
    compareSetting: {
      type: Object,
      default () {
        return {
          width: 1024,
          height: 960,
          delay: 1000
        }
      }
    },
    focusDom: {
      type: Boolean,
      default: false
    }
  },
  watch: {
    answer: {
      handler: function (val) {
        this.newScore = val.score
      },
      deep: true
    },
    question: {
      handler: function () { },
      deep: true
    },
    // score: function (val) {
    //   if (val.indexOf('+') !== -1 || val.indexOf('-') !== -1) {
    //     val.length = val.length - 1
    //   }
    // },
    autoCompare: function (val) {
      if (val === true) {
        console.log('auto')
        this.picCompare()
      }
    },
    autoCache: function () { },
    active: function (val) {
      // console.log('change', this.$refs.star.style)
      // let str = this.questionTitle
      let str = this.question.title
      if (val === true) {
        // 匹配中文个数
        let reg = /[\u4e00-\u9fa5\uf900-\ufa2d]/g // 匹配中文的字符   g表示全局匹配
        let reg1 = /[\u3002|\uff1f|\uff01|\uff0c|\u3001|\uff1b|\uff1a|\u201c|\u201d|\u2018|\u2019|\uff08|\uff09|\u300a|\u300b|\u3008|\u3009|\u3010|\u3011|\u300e|\u300f|\u300c|\u300d|\ufe43|\ufe44|\u3014|\u3015|\u2026|\u2014|\uff5e|\ufe4f|\uffe5]/g
        let res = str.match(reg)
        let res2 = str.match(reg1)
        let width1 = res.length + res2.length
        // 计算其余宽度
        let width2 = Math.max(str.length - width1, 0)
        this.$refs.star.style.width = (width1 * 16 + width2 * 11) + 'px'
      } else {
        this.$refs.star.style.width = '40px'
      }
    },
    focusDom: function (val) {
      if (val === true) {
        document.querySelector('#score-dom-' + this.answer.answerId).scrollIntoView({ behavior: 'smooth', block: 'start', inline: 'nearest' })
        setTimeout(() => {
          document.querySelector('#score-dom-' + this.answer.answerId).focus()
        }, 600)
      }
    }
  },
  computed: {
    account () {
      return this.$store.state.account
    },
    isTeacher () {
      return this.$store.state.isTeacher
    },
    showQId () {
      if (this.active) {
        return this.question.title
      }
      return this.answer.questionId
    }
  }
}
</script>

<style scoped>
.judge-card {
  /* justify-content: center; */
  /* align-items: center; */
  position: relative;
  width: 95%;
  border-radius: 5px;
  /* background-color: rgba(240, 250, 255, 0.593); */
  min-height: 300px;
  margin: 5px auto;
  margin-bottom: 30px;
  padding-bottom: 5px;
  /* border: 1px dotted dodgerblue; */
}

.judge-card .star {
  position: absolute;
  width: 40px;
  height: 38px;
  line-height: 38px;
  background-color: darkturquoise;
  color: aliceblue;
  left: -12px;
  top: 0px;
  z-index: 99;
  border-radius: 20px;
  overflow: hidden;
  text-overflow: ellipsis;
  text-align: left;
  padding-left: 20px;
  transition: all 1s;

}

.judge-card>>>.el-form-item .el-form-item__content {
  max-width: 130px;
}

.judge-card .active-star {
  position: absolute;
  height: 38px;
  background-color: #409EFF;
  left: 0px;
  top: 0px;
  border-radius: 5px;
  padding-right: 20px;
}

.judge-card .star p {
  margin-top: 0;
  margin-bottom: 0;
  transition: all 1s;
}

.judge-card>>>.student-info-box {
  width: 100%;
  text-align: center;
  margin-top: 10px;
}

.form-span {
  display: inline-block;
  min-width: 100px;
  max-width: 150px;
  margin-top: 0;
  margin-bottom: 0;
}

/* 迟交的人特殊标记一下提交日期 */
.delay-submit {
  color: red;
}
</style>
