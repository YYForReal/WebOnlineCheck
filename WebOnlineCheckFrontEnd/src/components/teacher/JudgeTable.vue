<template>
    <div class="judge-table">
        <div class="box">
            <h2>筛选栏</h2>
            <el-form :inline="true" ref="questionForm" :model="form" label-position="left" label-width="0px"
                class="question-container">
                <el-form-item prop="questionId" style="width:150px">
                    <el-select v-model="form.questionId" placeholder="请选择实验">
                        <el-option v-for="item in questionList" :key="item.questionId" :label="item.title"
                            :value="item.questionId" />
                    </el-select>
                </el-form-item>
                <el-form-item prop="username">
                    <el-input type="text" v-model="form.username" placeholder="姓名(可选)"></el-input>
                </el-form-item>
                <el-form-item prop="userid">
                    <el-input type="text" v-model="form.userId" placeholder="学号（可选）" @keyup.enter.native="handleLogin">
                    </el-input>
                </el-form-item>
                <el-button type="primary" @click="refreshAnswerList">刷新</el-button>
            </el-form>
        </div>
        <div class="question-text-box">
            <h3>比对文本</h3>
            <p class="question-text" v-text="questionText"></p>
        </div>
        <div class="student-submit-box">
            <h3>学生提交</h3>
            <div class="one-box" v-for="item in showAnswerList" :key="item.answerId">
                <JudgeVue :answerId="item.answerId" :content="item.content" :score="item.score"
                    :questionText="questionText" :userId="item.userId" :username="item.username"
                    @changeScore="changeScore" :questionId="form.questionId" :basePicUrl2="ansPicUrl" />
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
        questionId: null,
        userId: null,
        username: null
      },
      questionList: [],
      questionText: '',
      scanType: 0,
      ansPicUrl: null
    }
  },
  mounted () {
    this.refreshQuestionList()
  },
  methods: {
    getQuestionBase () {
      return this.ansPicUrl
    },
    askPictureUrl () {
      let pageUrl = 'http://yywebsite.cn/webcheck/#/template?questionId=' + this.form.questionId
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
          // 获取该问题的base转码
          this.ansPicUrl = res.data.data.image
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
    },
    refreshAnswerList () {
      this.askPictureUrl()
      this.axios({
        url: this.baseUrl + '/answer/get?questionId=' + this.form.questionId,
        method: 'get'
      }).then((res) => {
        this.answerList = res.data
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
    'form.questionId': {
      handler (newId) {
        this.questionList.forEach(element => {
          if (element.questionId === newId) {
            this.questionText = element.content
          }
        })
        this.refreshAnswerList(newId)
      }
    },

    answerList: {
      handler () {},
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
.box{
    margin-bottom: 5px;
}
.student-submit-box {
    margin-top: 15px;
}
</style>
