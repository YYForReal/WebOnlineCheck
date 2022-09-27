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
                    @changeScore="changeScore" />
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
      scanType: 0
    }
  },
  mounted () {
    this.refreshQuestionList()
  },
  methods: {
    refreshAnswerList () {
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
    // 'form.userId': {
    //   handler (newId) {

    //   }
    // },
    // 'form.username': {
    //   handler (newvalue) {
    //     this.answerList = this.answerList.filter((value) => {
    //       console.log('value:', value)
    //       return value.username.indexOf(newvalue) !== -1
    //     })
    //   }

    // },
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
