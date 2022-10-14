<template>
  <div class="judge-table">
    <div class="box">
      <h2>筛选栏</h2>
      <el-form :inline="true" ref="questionForm" :model="form" label-position="left" label-width="0px"
        class="question-container">
        <el-form-item prop="questionIds" style="width:180px">
          <el-select v-model="form.questionIds" multiple collapse-tags placeholder="请选择实验(可选)" class="multiple-select">
            <el-option v-for="item in questionList" :key="item.questionId" :label="item.title"
              :value="item.questionId" />
            <!-- multiple -->
          </el-select>
        </el-form-item>
        <el-form-item prop="username" height="60px">
          <el-input type="text" v-model="form.username" placeholder="姓名（回车搜索）" @keyup.enter.native="refreshAnswerList">
          </el-input>
        </el-form-item>
        <el-form-item prop="userid">
          <el-input type="text" v-model="form.userId" placeholder="学号（回车搜索）" @keyup.enter.native="refreshAnswerList">
          </el-input>
        </el-form-item>
        <el-form-item>
          一键比对
          <el-switch v-model="autoCompare" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
        </el-form-item>

        <el-button type="primary" @click="refreshAnswerList">搜索</el-button>

      </el-form>
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
        :page-sizes="pageSizes" :page-size="PageSize" layout="total, sizes, prev, pager, next, jumper"
        :total="totalCount">
      </el-pagination>
    </div>
    <!-- <div class="question-text-box">
      <h3>比对文本</h3>
      <p class="question-text" v-text="questionText"></p>
    </div> -->
    <div class="student-submit-box">
      <h3>学生提交</h3>
      <div class="one-box" v-for="(item,i) in showAnswerList.slice((currentPage-1)*PageSize,currentPage*PageSize)"
        :key="item.answerId">
        <JudgeVue :answerId="item.answerId" :content="item.content" :score="item.score"
          :questionText="chooseQuestionText(item.questionId)" :userId="item.userId" :username="item.username"
          @changeScore="changeScore" :questionId="item.questionId" :basePicUrl2="chooseAnsPicUrl(item.questionId)"
          :autoCompare="(autoCompare && canCompareNumber>=i)" @completeCompare="completeCompare" />
      </div>
    </div>
  </div>
</template>
<script>
import JudgeVue from './Judge.vue'

export default {
  data () {
    return {
      answerList: [],
      form: {
        questionIds: null,
        userId: null,
        username: null
      },
      questionList: [],
      scanType: 0,
      ansPicUrls: [],
      // questionTexts: [],
      autoCompare: false,
      compareAnswerNumber: 0, // 当前比较的参考运行截图序号
      canCompareNumber: 0, // 当前比较的运行截图序号
      isSending: false,
      // 默认显示第几页
      currentPage: 1,
      // 个数选择器（可修改）
      pageSizes: [10, 20, 30, 40, 50, 60, 80, 100],
      // 默认每页显示的条数（可修改）
      PageSize: 10
    }
  },
  mounted () {
    this.compareAnswerNumber = 0
    setTimeout(() => {
      if (this.isSending === false) {
        this.isSending = true
        this.refreshQuestionList()
      }
    }, 500)
  },
  methods: {
    // 可以比对的图像数量
    completeCompare () {
      this.canCompareNumber++
    },
    // 分页
    // 每页显示的条数
    handleSizeChange (val) {
      // 改变每页显示的条数
      this.PageSize = val
      // 注意：在改变每页显示的条数时，要将页码显示到第一页
      this.currentPage = 1
    },
    // 显示第几页
    handleCurrentChange (val) {
      // 改变默认的页数
      this.currentPage = val
    },
    chooseAnsPicUrl (questionId) {
      // console.log('choise ans:', this.ansPicUrls)
      for (let i = 0; i < this.ansPicUrls.length; i++) {
        if (this.ansPicUrls[i].questionId === questionId) {
          // console.log('check', this.ansPicUrls[i].image)
          return this.ansPicUrls[i].image
        }
      }
      return null
    },
    chooseQuestionText (questionId) {
      for (let i = 0; i < this.questionList.length; i++) {
        if (this.questionList[i].questionId === questionId) {
          return this.questionList[i].content
        }
      }
      return null
    },
    checkIsExist (questionId) {
      for (let i = 0; i < this.ansPicUrls.length; i++) {
        if (this.ansPicUrls[i].questionId === questionId) {
          return true
        }
      }
      return false
    },
    askOnePic (questionId) {
      const ansUrls = this.ansPicUrls
      return new Promise((resolve, reject) => {
        let pageUrl = 'http://yywebsite.cn/webcheck/#/template?questionId=' + questionId
        let width = 1024
        let height = 768
        let timeout = 40000
        let delay = 500
        this.axios(
          {
            method: 'POST',
            headers: { 'content-type': 'application/x-www-form-urlencoded' },
            data: `pageUrl=${pageUrl}&width=${width}&height=${height}&timeout=${timeout}&delay=${delay}`,
            url: 'http://118.31.165.150:3000/api/img'
          }
        ).then((res) => {
          if (res.data.code === 0) {
            console.log('push', this.ansPicUrls)
            this.compareAnswerNumber++
            ansUrls.push({
              questionId: questionId,
              image: res.data.data.image
            })
            if (this.compareAnswerNumber < this.questionList.length) {
              this.askOnePic(this.questionList[this.compareAnswerNumber].questionId)
            }
          } else if (res.data.code === 99999) {
            this.$message({
              type: 'warning',
              message: '请求量过大，请稍后再试',
              showClose: true
            })
          } else {
            console.log(res.data.message)
          }
        }).catch(() => {
          this.$message({
            type: 'error',
            message: '网络异常',
            showClose: true
          })
        })
      })
    },
    askPictureUrl () {
      this.ansPicUrls = []
      this.askOnePic(this.questionList[this.compareAnswerNumber].questionId)
      // for (let i = 0; i < this.questionList.length; i++) {
      //   if (this.questionList != null) {
      //     setTimeout(() => {
      //       let pageUrl = 'http://yywebsite.cn/webcheck/#/template?questionId=' + this.questionList[i].questionId
      //       let width = 1024
      //       let height = 768
      //       let timeout = 40000
      //       let delay = 500
      //       this.axios(
      //         {
      //           method: 'POST',
      //           headers: { 'content-type': 'application/x-www-form-urlencoded' },
      //           data: `pageUrl=${pageUrl}&width=${width}&height=${height}&timeout=${timeout}&delay=${delay}`,
      //           url: 'http://118.31.165.150:3000/api/img'
      //         }
      //       ).then((res) => {
      //         if (res.data.code === 0) {
      //           console.log('push', this.ansPicUrls)
      //           this.ansPicUrls.push({
      //             questionId: this.questionList[i].questionId,
      //             image: res.data.data.image
      //           })
      //         } else if (res.data.code === 99999) {
      //           this.$message({
      //             type: 'warning',
      //             message: '请求量过大，请稍后再试',
      //             showClose: true
      //           })
      //         } else {
      //           console.log(res.data.message)
      //         }
      //       }).catch(() => {
      //         this.$message({
      //           type: 'error',
      //           message: '网络异常',
      //           showClose: true
      //         })
      //       })
      //     }, i * 2000)
      //   }
      // }
    },
    refreshAnswerList () {
      // this.askPictureUrl()
      let requestUrl = this.baseUrl + '/answer/multi-get?questionIds=' + this.form.questionIds
      if (this.form.userId) {
        requestUrl += ('&userId=' + this.form.userId)
      }
      if (this.form.username) {
        requestUrl += ('&username=' + this.form.username)
      }

      this.axios({
        url: requestUrl,
        method: 'get',
        headers: {
          'Access-Control-Allow-Origin': '*'
        }
      }).then((res) => {
        this.answerList = res.data
        console.log('answerList', this.answerList)
      }).catch((err) => {
        console.log('request /answer/get: ', err)
      })
    },
    refreshQuestionList () {
      this.axios({
        url: this.baseUrl + '/question/get',
        method: 'get'
      }).then((res) => {
        this.questionList = res.data
        // this.refreashQuestionText()
        this.askPictureUrl()
      }).catch((err) => {
        console.log('request /question/get: ', err)
      })
    },
    // refreashQuestionText () {
    //   this.questionTexts = []
    //   this.questionList.forEach(element => {
    //     if (this.form.questionIds.indexOf(element.questionId) !== -1) {
    //       this.questionTexts.push({
    //         questionId: element.questionId,
    //         questionText: element.content
    //       })
    //     }
    //   })
    // },
    changeScore (answerId, score) {
      for (let i = 0; i < this.answerList.length; i++) {
        if (this.answerList[i].answerId === answerId) {
          this.answerList[i].score = score
        }
      }
    }
  },

  watch: {
    'form.questionIds': {
      handler (newIds) {
        // this.ansPicUrl = null
        // this.questionTexts = []
        // console.log('newId:', newIds)
        // console.log(this.questionList)
        // this.refreashQuestionText()
        // this.refreshAnswerList(newId)
      }
    },
    ansPicUrls: {
      deep: true
    },
    answerList: {
      handler () { },
      deep: true
    },
    autoCompare: {
      handler () {
        if (this.questionList.length === 0 && this.isSending === false) {
          this.refreshQuestionList()
          this.isSending = true
        }
        this.canCompareNumber = 0
      }
    }
  },
  components: {
    JudgeVue

  },
  computed: {
    showAnswerList () {
      return this.answerList.filter((value) => {
        let idFlag = true
        let nameFlag = true
        if (this.form.userId != null && this.form.userId !== '') {
          idFlag = value.userId.indexOf(this.form.userId) !== -1
        }
        if (this.form.username != null && this.form.username !== '') {
          nameFlag = value.username.indexOf(this.form.username) !== -1
        }
        return idFlag && nameFlag
      })
    },
    totalCount () {
      if (this.answerList == null) return 0

      return this.showAnswerList.length
    }
  }

}
</script>
<style scoped>
.judge-table {
  width: 100%;
  padding: 5px 10px;
}

.question-text-box {
  width: 100%;
  margin: 0 auto;
}

.question-text {
  width: 80%;
  text-align: left;
  text-indent: 2em;
  margin: 5px auto;
  background-color: rgba(241, 224, 224, 0.456);
  border-radius: 10px;

}

.question-container {
  margin: 0 auto;
  text-align: center;
  height: 60px;
  line-height: 60px;
}

h2,
h3 {
  text-align: center;
  margin-top: 8px;
  margin-bottom: 5px;
}

.one-box {
  padding: 0 20px;

}

.box {
  margin-bottom: 5px;
}

.student-submit-box {
  margin-top: 15px;
}

.question-container>>>.el-form-item>>>.el-form-item__content {
  vertical-align: center !important;
}

.multiple-select>>>.el-input__inner {
  height: 60px !important;
}

.question-container>>>.el-form-item__content {
  line-height: 60px;
}

.el-pagination {
  text-align: center;
  margin-top: 5px;
}
</style>
