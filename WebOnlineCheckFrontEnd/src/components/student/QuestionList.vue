<template>
  <el-row class="question-list">
    <h2>已提交列表</h2>
    <el-collapse class="submit-list" v-model="activeNames"  accordion>
      <el-collapse-item v-for="answer in scoreList" :key="answer.answerId" :title="answer.questionTitle"
        :name="answer.answerId">
        <div>
          <span class="collapse-label">更新时间</span>
          {{formatTime(answer.updateTime)}}
        </div>
        <div>
          <span class="collapse-label">是否可查看比对结果：{{answer.compareText?'Yes':'No'}}</span>
        </div>
        <div v-if="answer.compareText == null">
          <h4>提交内容</h4>
          <p>{{answer.content}}</p>
        </div>
        <div v-else>
          <CompareCardVue :visitType="0" :content="answer.content" :questionText="answer.compareText" ></CompareCardVue>
        </div>
      </el-collapse-item>
    </el-collapse>
    <h4>说明</h4>
    <ol class="information-list">
      <li>平台仅作文本校验，提交后仍需给老师检查效果。</li>
      <li>查看时间：提交6日后可查看文本比对结果。</li>
      <li>结果表示：<ins>绿色</ins>表示多余内容，<del>红色</del>表示缺少内容。有关换行产生的空格，中英文符号可忽略。</li>
    </ol>
  </el-row>
</template>
<script>
import {MyFilter} from '@/utils/myFilter.js'
import CompareCardVue from '../CompareCard.vue'
export default {
  name: 'QuestionList',
  data () {
    return {
      activeNames: ['1'],
      scoreList: []
    }
  },
  components: {
    CompareCardVue
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
.information-list li{
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

del {
  background: #ff0000bf;
  text-decoration: none;
}

ins {
  background: #00ff22c7;
  text-decoration: none;
}

</style>
