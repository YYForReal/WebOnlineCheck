<template>
    <div class="user-score-list">
        <table>
            <tr>
                <td style="min-width:80px">学号</td>
                <td style="min-width:80px">姓名</td>
                <td v-for="question in questionList" :key="question.questionId">{{question.title}}</td>
            </tr>
            <tr v-for="student in studentList" :key="student.userId">
                <td>{{student.userId}}</td>
                <td>{{student.userName}}</td>
                <td v-for="(score,i) in student.scoreArr" :key="i">{{score}}</td>
            </tr>
        </table>
        <!-- <el-table :data="questionList" border="true" style="width: 100%">
            <el-table-column prop="userId" label="学号" width="180">
            </el-table-column>
            <el-table-column prop="username" label="姓名" width="180">
            </el-table-column>
            <el-table-column label="实验">
                <el-table-column v-for="item in questionList" :key="item.questionId" prop="score" label="地址">
                </el-table-column>
            </el-table-column>
        </el-table> -->
    </div>
</template>
<script>
export default {
  name: 'ScoreList',
  data () {
    return {
      questionList: [],
      studentList: [],
      studentScore: []
    }
  },
  methods: {
    getQuestions () {
      this.axios({
        url: this.baseUrl + '/question/all-score',
        method: 'get'
      }).then(res => {
        if (res.status === 200) {
          this.questionList = res.data.data
          this.getStudents()
        } else {
          this.$message({
            type: 'error',
            message: '获取问题分数列表失败',
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
    getStudents () {
      this.axios({
        url: this.baseUrl + '/user/get/scores',
        method: 'get'
      }).then(res => {
        if (res.status === 200) {
          this.studentList = res.data.data
          console.log(res)
          const questionLength = this.questionList.length
          for (let i = 0; i < this.studentList.length; i++) {
            let student = this.studentList[i]
            let scoreArr = new Array(questionLength).fill(0)
            for (let j = 0; j < student.answerList.length; j++) {
              let answer = student.answerList[j]
              scoreArr[answer.questionId - 1] = answer.score
            }
            student.scoreArr = scoreArr
            this.studentScore.push(scoreArr)
          }
        } else {
          this.$message({
            type: 'error',
            message: '获取学生分数列表失败',
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
  mounted () {
    this.getQuestions()
  },
  watch: {
    studentList: {
      handler (newValue) {
        // console.log(newValue)
      },
      deep: true
    },
    questionList: {
      handler (newValue) {
        // console.log(newValue)
      },
      deep: true
    }
  }
}
</script>
<style scoped >

table{
    min-width:100%;
}

tr{
    width:100%;
}

tr td {
  width: 300px;
  border: 1px solid #000000e6;
    text-overflow: ellipsis;
    text-align: center;
}

</style>
