<template>
  <el-card class="judge-card">
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
          <el-input class="form-span" v-loading="isLoading" v-model="similarity" disabled="disabled"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="picCompare">比对刷新</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="viewEffect">查看效果</el-button>
        </el-form-item>

        <!-- <el-form-item label="分数">
          <el-input v-model="formInline.newScore" type="number"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">评分</el-button>
        </el-form-item> -->
      </el-form>
    </div>
    <CompareCard :visitType="1" :content="content" :questionText="questionText" :comparePic="basePicUrl2"
      :runningPic="basePicUrl" :isLoading="isLoading" />
  </el-card>
</template>

<script>
import CompareCard from '../CompareCard.vue'
export default {
  name: 'Judge',
  data () {
    return {
      formInline: {
        newScore: 100
      },
      basePicUrl: 'https://source.acexy.cn/view/YPIBluo',
      similarity: null,
      isLoading: false
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
  },
  methods: {
    viewEffect () {
      window.open('http://yywebsite.cn/webcheck/#/template?answerId=' + this.answerId, '_blank') // 注意第二个参数
    },
    askForSimilarity (pic1, pic2) {
      this.axios({
        url: this.baseUrl + '/answer/compare',
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
        this.similarity = res.data.data
      }).catch((err) => {
        console.log('request /answer/compare: ', err)
      })
    },
    picCompare () {
      this.isLoading = true
      // let pageUrl = 'http://yywebsite.cn/'
      let pageUrl = 'http://yywebsite.cn/webcheck/#/template' + '?answerId=' + this.answerId
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
          console.log('图像的base转码为:', this.basePicUrl)
          // 获取问题的base转码
          // this.basePicUrl2 = this.$emit('getQuestionBase')
          // console.log('答案的base转码为:', this.basePicUrl2)
          this.askForSimilarity(this.basePicUrl, this.basePicUrl2)
        } else {
          console.log(res.data.message)
        }
      }).catch(() => {
        this.isLoading = false
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
    compareSetting: {
      type: Object,
      default () {
        return {
          width: 1440,
          height: 960,
          delay: 500
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
      if (val === true) {
        console.log('auto')
        this.picCompare()
      }
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

.judge-card>>>.student-info-box {
  width: 100%;
  text-align: center;
  margin-top: 10px;
}

.form-span {
  display: inline-block;
  min-width: 100px;
  max-width: 150px;
}
</style>
