<template>
  <el-row class="question-list">
    <h2>已提交列表</h2>
    <el-collapse class="submit-list" v-model="activeNames"  accordion>
      <el-collapse-item v-for="answer in scoreList" :key="answer.answerId" :title="answer.questionTitle"
        :name="answer.answerId">
        <div>
          <span class="collapse-label">
            更新时间
          </span>
          {{formatTime(answer.updateTime)}}
        </div>
        <div>
          <span class="collapse-label">
            所得分数
          </span>
          {{answer.score}}
        </div>
        <div>
          <h4>提交内容</h4>
          <p>{{answer.content}}</p>
        </div>
      </el-collapse-item>
    </el-collapse>
    <h4>说明</h4>
    <ol>
      <li class="information">所得分数以老师登分册为准。 </li>
      <li class="information">提交列表中仅出现已提交的记录，若没提交，则无记录。 </li>
      <li class="information">之前实验已经提交的内容不需要再次提交。 </li>
    </ol>
  </el-row>
</template>
<script>
import {MyFilter} from '@/utils/myFilter.js'
export default {
  name: 'QuestionList',
  data () {
    return {
      activeNames: ['1'],
      scoreList: []
    }
  },
  computed: {
    account () {
      return this.$store.state.account
    }
  },
  mounted () {
    if (this.account === null || this.account === undefined) {
      this.$router.push('/login')
    }
    this.refreshSubmitList()
  },
  methods: {
    formatTime (str) {
      return MyFilter.dateFormat(str)
    },
    refreshSubmitList () {
      if (this.account != null) {
        this.axios.get(this.baseUrl + '/user/get/score?userId=' + this.account.userid + '&username=' + this.account.username)
          .then(res => {
            if (res.status === 200 && res.data.status === 200) {
              this.scoreList = res.data.data.answerList
            } else {
              this.$message({
                type: 'error',
                message: res.data.message,
                showClose: true
              })
            }
          }).catch(err => {
            this.$message({
              type: 'error',
              message: '获取提交列表失败',
              showClose: true
            })
            console.log(err)
          })
      } else {
        this.$router.push('/login')
      }
    }
  }
}
</script>
<style scoped >
.question-list {
  width: 100%;
}
.information{
  text-align: left;
  font-size:small;
}
.question-list h1,
h2,
h3 {
  text-align: center;
  margin: 18px 0;
}

.submit-list {
  width: 100%;
}
.collapse-label{
  font-weight: bold;
}
.question-list >>> .el-collapse-item__header{
  font-size:18px;
  text-align: center;
}

</style>
