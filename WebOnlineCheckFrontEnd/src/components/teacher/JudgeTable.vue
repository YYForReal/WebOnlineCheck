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
          <el-input type="text" v-model="form.username" placeholder="姓名(可选)（回车搜索）"
            @keyup.enter.native="refreshAnswerList">
          </el-input>
        </el-form-item>
        <el-form-item prop="userid">
          <el-input type="text" v-model="form.userId" placeholder="学号（可选）（回车搜索）"
            @keyup.enter.native="refreshAnswerList">
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
      <div class="one-box" v-for="item in showAnswerList.slice((currentPage-1)*PageSize,currentPage*PageSize)"
        :key="item.answerId">
        <JudgeVue :answerId="item.answerId" :content="item.content" :score="item.score"
          :questionText="chooseQuestionText(item.questionId)" :userId="item.userId" :username="item.username"
          @changeScore="changeScore" :questionId="item.questionId" :basePicUrl2="chooseAnsPicUrl(item.questionId)"
          :autoCompare="autoCompare" />
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
      questionTexts: [],
      autoCompare: false,
      // 默认显示第几页
      currentPage: 1,
      // 个数选择器（可修改）
      pageSizes: [10, 20, 30, 40],
      // 默认每页显示的条数（可修改）
      PageSize: 10

    }
  },
  mounted () {
    this.refreshQuestionList()
  },
  methods: {
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
      for (let i = 0; i < this.ansPicUrls.length; i++) {
        if (this.ansPicUrls[i].questionId === questionId) {
          return this.ansPicUrls[i].image
        }
      }
      return null
    },
    chooseQuestionText (questionId) {
      for (let i = 0; i < this.questionTexts.length; i++) {
        if (this.questionTexts[i].questionId === questionId) {
          return this.questionTexts[i].questionText
        }
      }
      return null
    },
    getQuestionBase () {
      return this.ansPicUrl
    },

    askPictureUrl () {
      for (let i = 0; i < this.form.questionIds.length; i++) {
        let pageUrl = 'http://yywebsite.cn/webcheck/#/template?questionId=' + this.form.questionIds[i]
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
          // console.log('question api/img:', res)
          if (res.data.code === 0) {
            this.ansPicUrls.push({
              questionId: this.form.questionIds[i],
              image: res.data.data.image
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
      }
    },
    refreshAnswerList () {
      this.askPictureUrl()
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
      }).catch((err) => {
        console.log('request /question/get: ', err)
      })
    },
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
        this.ansPicUrl = null
        this.questionTexts = []
        this.questionList.forEach(element => {
          if (newIds.indexOf(element.questionId) !== -1) {
            this.questionTexts.push({
              questionId: element.questionId,
              questionText: element.content
            })
          }
        })
        // this.refreshAnswerList(newId)
      }
    },

    answerList: {
      handler () { },
      deep: true
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
