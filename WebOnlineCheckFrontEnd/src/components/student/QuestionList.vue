<template>
  <el-row class="question-list">
    <h2>已提交列表</h2>
    <p class="infomation">说明：提交后分数默认为0，不要慌张，这时候只是还没有批改。<small><del>（当然，不排除交了个0分答案）</del></small></p>
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
      this.axios.get(this.baseUrl + '/user/get/score?userId=' + this.account.userid + '&username=' + this.account.username)
        .then(res => {
          if (res.status === 200 && res.data.status === 200) {
            console.log('look:', res)
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
    }
  }
}
</script>
<style scoped >
.question-list {
  width: 100%;
}
.infomation{
  text-align: left;
  text-indent: 2em;
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
