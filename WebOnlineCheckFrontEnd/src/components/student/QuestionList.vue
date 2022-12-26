<template>
  <el-row class="question-list">
    <h2>已提交列表</h2>
    <h4>说明</h4>
    <ol>
      <li>提交实验代码需确保页面在点击提交等按钮后不会出现页面跳转或刷新的情况。</li>
      <!-- <li>提交前注意将img的引用地址从本地地址改为线上链接。</li> -->
      <!-- <li v-if="hiddenFlag == false" >提交列表中将于{{targetDate.getDate()+'日'+targetDate.getHours()+'时'}}去除下方<span style="color:blueviolet">提交内容</span>的代码查看，各位记得保存好一份本地代码~</li>
      <li v-else >提交列表已去除<span style="color:blueviolet">提交内容</span>的查看，各位记得保存好一份本地代码~</li>
      <li v-if="hiddenFlag == false" >去除剩余时间：{{releaseTime}}</li>
      <li>本周更新了用户登录、随机数、通讯录的校验代码，如果出现无法检测DOM及函数的情况会出现警告。</li> -->
    </ol>
    <el-collapse class="submit-list" v-model="activeNames" accordion>
      <el-collapse-item v-for="answer in scoreList" :key="answer.answerId" :title="answer.questionTitle"
        :name="answer.answerId">

        <el-form :inline="true">
          <el-form-item label="最近更新时间">
            <span>{{formatTime(answer.updateTime)}}</span>
            <!-- <el-input class="form-span" v-bind="" disabled="disabled"></el-input> -->
          </el-form-item>
          <el-form-item label="是否可查看比对结果" label-width="150px" v-if="false">
            {{answer.compareText?'Yes':'No'}}
            <!-- <span>{{formatTime(answer.updateTime)}}</span> -->
            <!-- <el-input class="form-span" v-bind="" disabled="disabled"></el-input> -->
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="viewEffect(answer.answerId,answer.questionId)">查看效果</el-button>
          </el-form-item>
          <!-- <el-form-item label="相似度:">
            <el-input class="form-span" v-loading="!similarity" v-model="similarity" disabled="disabled"></el-input>
          </el-form-item> -->
        </el-form>

        <!-- v-if="answer.compareText == null" -->
        <div v-if="hiddenFlag == false">
          <el-form >
            <el-form-item label="提交内容">
              <el-input type="textarea" v-text="answer.content" disabled></el-input>
            </el-form-item>
          </el-form>
          <!-- <h4>提交内容</h4> -->
        </div>
        <!-- <div v-else>
          <CompareCardVue :visitType="0" :content="answer.content" :questionText="answer.compareText"></CompareCardVue>
        </div> -->
      </el-collapse-item>
    </el-collapse>
  </el-row>
</template>
<script>
import { MyFilter } from '@/utils/myFilter.js'
import CompareCardVue from '../CompareCard.vue'
export default {
  name: 'QuestionList',
  data () {
    return {
      activeNames: ['1'],
      scoreList: [],
      hiddenFlag: true,
      targetDate: new Date('2022/11/29 17:00:00'),
      releaseTime: null
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
    let now = new Date()
    let target = this.targetDate
    if (now > target) {
      this.hiddenFlag = true
    } else {
      this.hiddenFlag = false
      let now = new Date()
      if (now > target) {
        this.hiddenFlag = true
      } else {
        this.releaseTime = parseInt((this.targetDate - now) / 1000)
        let time = this.releaseTime
        let days = parseInt(time / 3600 / 24)
        let hours = parseInt(time / 3600) % 24
        let seconds = parseInt(time % 60)
        let minutes = parseInt(time / 60) % 60

        this.releaseTime = days + ' 天 ' + hours + ' 时 ' + minutes + ' 分 ' + seconds + ' 秒 '
      }
      let timer = setInterval(() => {
        let now = new Date()
        if (now > target) {
          this.hiddenFlag = true
          clearInterval(timer)
        } else {
          this.releaseTime = parseInt((this.targetDate - now) / 1000)
          let time = this.releaseTime
          let days = parseInt(time / 3600 / 24)
          let hours = parseInt(time / 3600) % 24
          let seconds = parseInt(time % 60)
          let minutes = parseInt(time / 60) % 60
          let timeStr = ''
          if (days > 0) {
            timeStr += days + ' 天 '
          }
          this.releaseTime = timeStr + hours + ' 时 ' + minutes + ' 分 ' + seconds + ' 秒 '
        }
      }, 1000)
    }
    this.refreshSubmitList()

    // if (this.account === null || this.account === undefined) {
    //   let storage = sessionStorage.getItem('web-account')
    //   let obj = JSON.parse(window.decodeURIComponent(window.atob(storage)))
    //   if (storage == null || obj.username == null || obj.userid == null) {
    //     this.$router.push('/login')
    //   } else {
    //     this.$store.dispatch('handleLogin', obj).then(() => {
    //       this.refreshSubmitList()
    //     })
    //   }
    // } else {
    //   this.refreshSubmitList()
    // }
  },
  methods: {
    viewEffect (answerId, questionId) {
      // alert(encodeURI(this.account.userid))
      // alert(encodeURIComponent(this.account.userid))

      // alert(this.account.username)
      // alert(this.account.username)

      let url = 'http://yywebsite.cn/webcheck/#/template'
      // let url = 'http://localhost:8080/#/template'
      let answerIdStr = '?answer=' + answerId
      let userStr = '&user=' + encodeURIComponent(this.account.userid)
      let username = '&name=' + encodeURIComponent(this.account.username)

      let questionStr = '&question=' + questionId
      window.open(url + answerIdStr + userStr + questionStr + username, '_blank') // 注意第二个参数
      // window.open('http://yywebsite.cn/webcheck/#/template?answerId=' + answerId + '&userId=' + window.decodeURIComponent(window.atob(this.account.userid)), '_blank') // 注意第二个参数
      // window.open('http://localhost:8081/#/template?answerId=' + answerId + '&userId=' + this.$md5(this.account.userid), '_blank') // 注意第二个参数
    },
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

.information-list li {
  text-align: left;
  font-size: small;
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

.collapse-label {
  font-weight: bold;
}

.question-list>>>.el-collapse-item__header {
  font-size: 18px;
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
