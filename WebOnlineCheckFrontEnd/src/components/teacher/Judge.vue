<template>
  <el-card class="judge-card">
    <div class="star" ref="star" :class="{ 'active-star': active }" @mouseenter="active = true"
      @mouseleave="active = false">
      <p>{{ showQId }}</p>
    </div>
    <div class="student-info-box">
      <el-form :inline="true" :model="formInline">
        <el-form-item label="学号">
          <!-- <span class="form-span" >{{userId}}</span> -->

          <el-input class="form-span" v-model="userId" disabled="disabled"></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <!-- <span class="form-span" >{{username}}</span> -->
          <el-input class="form-span" v-model="username" disabled="disabled"></el-input>
        </el-form-item>
        <!-- <el-form-item label="原分数">
          <el-input v-model="score" disabled="disabled"></el-input>
        </el-form-item>
         -->
        <el-form-item label="相似度:">
          <!-- <span class="form-span"  v-loading="!similarity">{{similarity}}</span> -->
          <!-- similarity -->
          <el-input class="form-span" v-loading="isLoading2" v-model="similarity" disabled="disabled"></el-input>
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="picCompare">比对刷新</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="viewEffect">查看效果</el-button>
        </el-form-item>
        <p class="form-span">{{ formatTime(updateTime) }}</p>

        <!-- <el-form-item label="分数">
          <el-input v-model="formInline.newScore" type="number"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">评分</el-button>
        </el-form-item> -->
      </el-form>
    </div>
    <CompareCard :visitType="question.display == 1 ? 1 : 0" :content="content" :questionText="question.content"
      :comparePic="basePicUrl2" :runningPic="basePicUrl" :isLoading="isLoading" :question="question" :JSCheckResult="JSCheckResult" />
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
    // this.picCompare()
    if (this.autoCompare) {
      this.picCompare()
    }
    this.askForCheck(this.question.questionId, this.answerId)
  },
  methods: {
    formatTime (time) {
      return MyFilter.dateFormat(time)
    },
    viewEffect () {
      let url = 'http://yywebsite.cn/webcheck/#/template'
      let answerStr = '?answer=' + this.answerId
      let questionStr = '&question=' + this.question.questionId

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
      }).catch((err) => {
        console.log('request /answer/compare: ', err)
      })
    },
    picCompare () {
      this.isLoading = true
      this.isLoading2 = true
      // let pageUrl = 'http://yywebsite.cn/'
      let pageUrl = 'http://yywebsite.cn/webcheck/#/template' + '?answer=' + this.answerId + '&question=' + this.question.questionId
      let width = this.compareSetting.width
      let height = this.compareSetting.height
      let timeout = 40000
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
          this.askForCheck(this.question.questionId, this.answerId)
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
    askForCheck (questionId, answerId) {
      console.log('askForCheck:', questionId, answerId)
      return this.axios({
        url: this.baseUrl + '/code/result/get?questionId=' + questionId + '&answerId=' + answerId,
        method: 'get'
        // transformRequest: [(data) => {
        //   var oMyForm = new FormData()
        //   oMyForm.append('questionId', questionId)
        //   oMyForm.append('answerId', answerId)
        //   return oMyForm
        // }]
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
    userId: {
      type: String,
      default: ''
    },
    username: {
      type: String,
      default: ''
    },
    questionId: {
      type: Number,
      default: null
    },
    basePicUrl2: {
      type: String,
      default: null
    },
    autoCompare: {
      type: Boolean,
      default: false
    },
    updateTime: {
      type: String,
      default: ''
    },
    compareSetting: {
      type: Object,
      default () {
        return {
          width: 1024,
          height: 960,
          delay: 1000
        }
      }
    }
  },
  watch: {
    answerId: function () { },
    content: function () { },
    score: function (val) {
      this.newScore = val
    },
    autoCompare: function (val) {
      // console.log('change SOn')
      if (val === true) {
        console.log('auto')
        this.picCompare()
      }
    },
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
      return this.questionId
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
</style>
