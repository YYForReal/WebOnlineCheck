<template>
  <el-card class="judge-card">
    <div class="student-info-box">
      <el-form :inline="true" :model="formInline">
        <el-form-item label="学号">
          <el-input v-model="userId" disabled="disabled"></el-input>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="username" disabled="disabled"></el-input>
        </el-form-item>
        <el-form-item label="原分数">
          <el-input v-model="score" disabled="disabled"></el-input>
        </el-form-item>
        <el-form-item label="分数">
          <el-input v-model="formInline.newScore" type="number"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSubmit">评分</el-button>
        </el-form-item>
      </el-form>
    </div>
    <CompareCard :visitType="1" :content="content" :questionText="questionText" />
  </el-card>
</template>

<script>
import CompareCard from '../CompareCard.vue'
export default {
  name: 'Judge',
  data () {
    return {
      formInline: {
        newScore: 100
      }
    }
  },
  components: {
    CompareCard
  },
  methods: {
    onSubmit () {
      if (this.account == null) {
        this.$message({
          type: 'error',
          message: '请先登录',
          showClose: true
        })
        return
      } else if (this.isTeacher === false) {
        this.$message({
          type: 'error',
          message: '老师才能改分呐',
          showClose: true
        })
        return
      }
      // TODO 改分
      this.axios({
        url: this.baseUrl + '/user/check',
        method: 'post',
        transformRequest: [(data) => {
          var oMyForm = new FormData()
          oMyForm.append('userId', this.account.userid)
          oMyForm.append('username', this.account.username)

          return oMyForm
        }]
      }).then((res) => {
        if (res.data === 'OK') {
          this.changeScore()
        } else {
          this.$message({
            type: 'error',
            message: '登录状态异常',
            showClose: true
          })
        }
      }).catch(() => {
        this.$message({
          type: 'error',
          message: '网络异常',
          showClose: true
        })
      })
    },
    changeScore () {
      this.axios({
        url: this.baseUrl + '/answer/score',
        method: 'post',
        transformRequest: [(data) => {
          var oMyForm = new FormData()
          oMyForm.append('answerId', this.answerId)
          oMyForm.append('score', this.formInline.newScore)
          oMyForm.append('userId', this.account.userid)
          oMyForm.append('username', this.account.username)
          return oMyForm
        }]
      }).then((res) => {
        if (res.data === 'OK') {
          this.$message({
            type: 'success',
            message: '评分成功',
            showClose: true
          })
          this.$emit('changeScore', this.answerId, this.formInline.newScore)
        } else {
          this.$message({
            type: 'error',
            message: '评分失败',
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
  props: {
    answerId: {
      type: Number,
      default: 0
    },
    content: {
      type: String,
      defalut: ''
    },
    score: {
      type: Number,
      default: 0
    },
    questionText: {
      type: String,
      default: ''
    },
    userId: {
      type: String,
      default: ''
    },
    username: {
      type: String,
      default: ''
    }
  },
  watch: {
    answerId: function () {},
    content: function () {},
    score: function (val) {
      this.newScore = val
    }
  },
  computed: {
    account () {
      return this.$store.state.account
    },
    isTeacher () {
      return this.$store.state.isTeacher
    }
  }
}
</script>

<style scoped>
.judge-card {
  /* justify-content: center; */
  /* align-items: center; */
  width: 95%;
  border-radius: 5px;
  /* background-color: rgba(240, 250, 255, 0.593); */
  min-height: 300px;
  margin: 5px auto;
  margin-bottom: 30px;
  padding-bottom: 5px;
  /* border: 1px dotted dodgerblue; */
}

.judge-card >>> .student-info-box {
  width: 100%;
  text-align: center;
  margin-top: 10px;
}

</style>
