<template>
  <el-row class="question-list">
    <h2>已提交列表</h2>
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
            <el-button type="primary" @click="viewEffect(answer.answerId)">查看效果</el-button>
          </el-form-item>
          <!-- <el-form-item label="相似度:">
            <el-input class="form-span" v-loading="!similarity" v-model="similarity" disabled="disabled"></el-input>
          </el-form-item> -->
        </el-form>

        <!-- v-if="answer.compareText == null" -->
        <div>
          <el-form>
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
    <h4>说明</h4>
    <ol>
      <!-- <li>平台仅作文本校验，提交后仍需给老师检查效果。</li> -->
      <li>提交前注意将img的引用地址从本地地址改为线上链接。</li>
      <li>为了尽可能还原最佳的网站显示效果：最好避免修改body标签样式，同时减少使用标签选择器修改div的样式。</li>

      <!-- <li>查看时间：提交6日后可查看文本比对结果。</li> -->
      <!-- <li>结果表示：<ins>绿色</ins>表示多余内容，<del>红色</del>表示缺少内容。有关换行产生的空格，中英文符号可忽略。</li> -->
    </ol>
    <!-- <h4>图片链接（右键-复制图片地址）</h4> -->
    <!-- <img src="https://source.acexy.cn/view/YPFg5qJ" > -->

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
      let storage = sessionStorage.getItem('web-account')
      let obj = JSON.parse(storage)
      if (storage == null || obj.username == null || obj.userid == null) {
        this.$router.push('/login')
      } else {
        this.$store.dispatch('handleLogin', obj).then(() => {
          this.refreshSubmitList()
        })
      }
    } else {
      this.refreshSubmitList()
    }
  },
  methods: {
    viewEffect (answerId) {
      window.open('http://yywebsite.cn/webcheck/#/template?answerId=' + answerId + '&userId=' + this.$md5(this.account.userid), '_blank') // 注意第二个参数
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
