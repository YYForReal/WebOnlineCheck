<template>
  <div class="list">
    <div class="left-bar">
      <h2>导航栏（TODO）</h2>
      <el-form :inline="true" ref="questionForm" :model="form"  label-position="left" label-width="0px"
        class="question-container">
        <el-form-item prop="questionId" style="width:150px">
          <el-select v-model="form.questionId" placeholder="请选择操作/实验">
            <el-option v-for="item in questionList" :key="item.questionId" :label="item.title"
              :value="item.questionId" />
          </el-select>
        </el-form-item>
        <el-button type="primary" @click="refreshAnswerList">刷新提交</el-button>
      </el-form>
      <div class="question-text-box">
        <h3>比对文本</h3>
        <p class="question-text" v-text="questionText"></p>
      </div>
    </div>
    <div class="right-bar">
      <!-- <router-view></router-view> -->
      <div v-if="scanType == 1" class="one-box" v-for="item in answerList" :key="item.answerId">
        <JudgeVue :answerId="item.answerId" :content="item.content" :score="item.score" :questionText="questionText"
        :userId="item.userId" :username="item.username"  @changeScore="changeScore" />
      </div>
      <ScoreListVue v-if="scanType===0"/>

    </div>
  </div>
</template>
<script>
import JudgeVue from './Judge.vue'
import ScoreListVue from './ScoreList.vue'
export default {
  name: 'Home',
  data () {
    return {
      answerList: [],
      form: {
        questionId: null
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
    refreshAnswerList (questionId) {
      this.axios({
        url: this.baseUrl + '/answer/get?questionId=' + questionId,
        method: 'get'
      }).then((res) => {
        this.answerList = res.data
      }).catch((err) => {
        console.log('search answer: ', err)
      })
    },
    refreshQuestionList () {
      this.axios({
        url: this.baseUrl + '/question/get',
        method: 'get'
      }).then((res) => {
        this.questionList = res.data
        this.questionList.unshift({
          questionId: -1,
          title: '查看学生分数'
        })
      }).catch((err) => {
        console.log('search question: ', err)
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
        if (newId === -1) {
          this.scanType = 0
        } else {
          this.scanType = 1
        }
        this.questionList.forEach(element => {
          if (element.questionId === newId) {
            this.questionText = element.content
          }
        })
        this.refreshAnswerList(newId)
      }
    },
    answerList: {
      handler (newList) {
        console.log('new list:', newList)
      },
      deep: true
    }
  },
  components: {
    JudgeVue, ScoreListVue
  }
}
</script>
<style scoped>
.list {
  width: 100%;
  display: flex;

}

.left-bar {
  width: 20%;
  height: 100%;
  min-height: 500px;
  padding: 5px 10px;
  text-align: center;
  background-color: aquamarine;
}

.right-bar {
  width: 80%;
  padding: 5px 10px;

}
.question-container{
  margin: 0 auto;

}

.question-text-box{
  width:100%;
  margin: 0 auto;
}

.question-text{
  margin-top: 5px;
  background-color: rgba(255, 255, 255, 0.51);
  border-radius: 10px;
}

</style>
