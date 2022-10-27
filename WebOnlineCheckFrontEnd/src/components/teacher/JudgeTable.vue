div<template>
  <div class="judge-table">
    <div class="box">
      <h2>筛选栏</h2>
      <el-form :inline="true" ref="questionForm" :model="form" label-position="left" label-width="0px"
        class="question-container">
        <el-form-item prop="questionIds" style="width:180px">
          <el-select v-model="form.questionIds" multiple collapse-tags placeholder="请选择实验(可选)" class="multiple-select">
            <el-option v-for="item in questionList" :key="item.questionId" :label="item.title"
              :value="item.questionId" />
            <!-- multiple -->
          </el-select>
        </el-form-item>
        <el-form-item prop="username" height="60px">
          <el-input type="text" v-model="form.username" placeholder="姓名（回车搜索）" @keyup.enter.native="refreshAnswerList">
          </el-input>
        </el-form-item>
        <el-form-item prop="userid">
          <el-input type="text" v-model="form.userId" placeholder="学号（回车搜索）" @keyup.enter.native="refreshAnswerList">
          </el-input>
        </el-form-item>
        <el-button type="info" round @click="compareSettingVisible = true">截图配置</el-button>
        <el-button type="primary" icon="el-icon-search" round @click="refreshAnswerList">搜索</el-button>
      </el-form>
      <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange" :current-page="currentPage"
        :page-sizes="pageSizes" :page-size="PageSize" layout="total, sizes, prev, pager, next, jumper"
        :total="totalCount">
      </el-pagination>
    </div>
    <!-- <div class="question-text-box">
      <h3>比对文本</h3>
      <p class="question-text" v-text="questionText"></p>
    </div> -->
    <div class="student-submit-box">
      <h3>学生提交</h3>
      <div class="one-box" v-for="(item, i) in showAnswerList.slice((currentPage - 1) * PageSize, currentPage * PageSize)"
        :key="item.answerId">
        <JudgeVue :answerId="item.answerId" :content="item.content" :score="item.score"
          :questionText="chooseQuestionText(item.questionId)" :userId="item.userId" :username="item.username"
          :updateTime="item.updateTime" :questionId="item.questionId" :basePicUrl2="chooseAnsPicUrl(item.questionId)"
          :questionTitle="chooseQuestionTitle(item.questionId)"
          :compareSetting="compareSetting" :autoCompare="(autoCompare && canCompareNumber >= i)"
          @changeScore="changeScore" @completeCompare="completeCompare" />
      </div>
    </div>
    <!-- :before-close="handleClose" -->
    <el-dialog title="截图配置" :visible.sync="compareSettingVisible" width="50%">
      <el-form>
        <el-form-item label="一键比对">
          <el-switch v-model="autoCompare" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
        </el-form-item>
        <el-form-item label="模拟浏览器宽度（单位：px）">
          <el-input type="number" v-model="compareSetting.width" placeholder="图像宽度"
            @keyup.enter.native="compareSettingVisible = false"></el-input>
        </el-form-item>
        <el-form-item label="模拟浏览器高度（单位：px）">
          <el-input type="number" v-model="compareSetting.height" placeholder="图像高度"
            @keyup.enter.native="compareSettingVisible = false"></el-input>
        </el-form-item>
        <el-form-item label="延迟截图时间（单位：ms）">
          <el-input type="number" v-model="compareSetting.delay" placeholder="延迟时间"
            @keyup.enter.native="compareSettingVisible = false"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <!-- <el-button @click="compareSettingVisible = false">取 消</el-button> -->
        <el-button type="primary" @click="compareSettingVisible = false">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>
<script>
import JudgeVue from './Judge.vue'

export default {
  data () {
    return {
      // 答案问题数据
      answerList: [],
      form: {
        questionIds: null,
        userId: null,
        username: null,
        timer: null// 记录最后更改实验列表的定时器
      },
      questionList: [],
      scanType: 0,
      ansPicUrls: [],

      // 图像比对数据
      autoCompare: false,
      compareAnswerNumber: 0, // 当前比较的参考运行截图序号
      canCompareNumber: 0, // 当前比较的运行截图序号
      isSending: false,
      canAskQuestionPic: false,
      compareSetting: {
        width: 1024,
        height: 960,
        delay: 500
      },
      compareSettingVisible: false,

      // 默认显示第几页
      currentPage: 1,
      // 个数选择器（可修改）
      pageSizes: [10, 20, 30, 40, 50, 60, 80, 100],
      // 默认每页显示的条数（可修改）
      PageSize: 10
    }
  },
  mounted () {
    this.compareAnswerNumber = 0
    setTimeout(() => {
      if (this.isSending === false) {
        this.isSending = true
        this.refreshQuestionList()
      }
    }, 500)
  },
  methods: {
    // 可以比对的图像数量
    completeCompare () {
      this.canCompareNumber++
    },
    // 分页
    // 每页显示的条数
    handleSizeChange (val) {
      // 改变每页显示的条数
      this.PageSize = val
      // 注意：在改变每页显示的条数时，要将页码显示到第一页
      this.currentPage = 1
      this.canCompareNumber = 0
    },
    // 显示第几页
    handleCurrentChange (val) {
      // 改变默认的页数
      this.currentPage = val
      this.canCompareNumber = 0
    },
    // 通过ID选择对应的参考图片
    chooseAnsPicUrl (questionId) {
      // console.log('choise ans:', this.ansPicUrls)
      for (let i = 0; i < this.ansPicUrls.length; i++) {
        if (this.ansPicUrls[i].questionId === questionId) {
          // console.log('check', this.ansPicUrls[i].image)
          return this.ansPicUrls[i].image
        }
      }
      return null
    },
    // 通过id选择对应的比对文本
    chooseQuestionText (questionId) {
      for (let i = 0; i < this.questionList.length; i++) {
        if (this.questionList[i].questionId === questionId) {
          return this.questionList[i].content
        }
      }
      return null
    },
    chooseQuestionTitle (questionId) {
      for (let i = 0; i < this.questionList.length; i++) {
        if (this.questionList[i].questionId === questionId) {
          console.log('CHoose tile：', this.questionList[i].title)
          return this.questionList[i].title
        }
      }
      return null
    },
    checkIsExist (questionId) {
      for (let i = 0; i < this.ansPicUrls.length; i++) {
        if (this.ansPicUrls[i].questionId === questionId) {
          return true
        }
      }
      return false
    },
    askOnePic (questionId) {
      const ansUrls = this.ansPicUrls
      return new Promise((resolve, reject) => {
        let pageUrl = 'http://yywebsite.cn/webcheck/#/template?questionId=' + questionId
        let width = this.compareSetting.width
        let height = this.compareSetting.height
        let timeout = 40000
        let delay = this.compareSetting.delay
        this.axios(
          {
            method: 'POST',
            headers: { 'content-type': 'application/x-www-form-urlencoded' },
            data: `pageUrl=${pageUrl}&width=${width}&height=${height}&timeout=${timeout}&delay=${delay}&element=html`,
            url: 'http://118.31.165.150:3000/api/img'
          }
        ).then((res) => {
          if (res.data.code === 0) {
            console.log('push', this.ansPicUrls)
            this.compareAnswerNumber++
            ansUrls.push({
              questionId: questionId,
              image: res.data.data.image
            })
            if (this.compareAnswerNumber < this.questionPicList.length) {
              this.askOnePic(this.questionPicList[this.compareAnswerNumber].questionId)
            } else {
              // 查询结束
              if (this.autoCompare === true) {
                console.log('autoComapre == true')
                this.autoCompare = false
                setTimeout(() => {
                  this.autoCompare = true // 为了触发vue的双向绑定 监听
                }, 100)
              }
            }
          } else if (res.data.code === 99999) {
            this.$message({
              type: 'warning',
              message: '请求量过大，请稍后再试',
              showClose: true
            })
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
      })
    },
    askPictureUrl () {
      this.ansPicUrls = []
      this.compareAnswerNumber = 0
      this.askOnePic(this.questionPicList[this.compareAnswerNumber].questionId)
      // for (let i = 0; i < this.questionList.length; i++) {
      //   if (this.questionList != null) {
      //     setTimeout(() => {
      //       let pageUrl = 'http://yywebsite.cn/webcheck/#/template?questionId=' + this.questionList[i].questionId
      //       let width = 1024
      //       let height = 768
      //       let timeout = 40000
      //       let delay = 500
      //       this.axios(
      //         {
      //           method: 'POST',
      //           headers: { 'content-type': 'application/x-www-form-urlencoded' },
      //           data: `pageUrl=${pageUrl}&width=${width}&height=${height}&timeout=${timeout}&delay=${delay}`,
      //           url: 'http://118.31.165.150:3000/api/img'
      //         }
      //       ).then((res) => {
      //         if (res.data.code === 0) {
      //           console.log('push', this.ansPicUrls)
      //           this.ansPicUrls.push({
      //             questionId: this.questionList[i].questionId,
      //             image: res.data.data.image
      //           })
      //         } else if (res.data.code === 99999) {
      //           this.$message({
      //             type: 'warning',
      //             message: '请求量过大，请稍后再试',
      //             showClose: true
      //           })
      //         } else {
      //           console.log(res.data.message)
      //         }
      //       }).catch(() => {
      //         this.$message({
      //           type: 'error',
      //           message: '网络异常',
      //           showClose: true
      //         })
      //       })
      //     }, i * 2000)
      //   }
      // }
    },
    refreshAnswerList () {
      // 如果当前已经刷新出了问题列表，且未选择问题，对所有的问题都进行一次图片比对
      if (this.canAskQuestionPic === true && (this.form.questionIds == null || this.form.questionIds.length === 0)) {
        this.askPictureUrl()
      }
      // 获取答案列表
      let requestUrl = this.baseUrl + '/answer/multi-get?questionIds=' + this.form.questionIds
      if (this.form.userId) {
        requestUrl += ('&userId=' + this.form.userId)
      }
      if (this.form.username) {
        requestUrl += ('&username=' + this.form.username)
      }

      this.axios({
        url: requestUrl,
        method: 'get',
        headers: {
          'Access-Control-Allow-Origin': '*'
        }
      }).then((res) => {
        this.answerList = res.data
        console.log('answerList', this.answerList)
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
        this.canAskQuestionPic = true
        // this.askPictureUrl()
      }).catch((err) => {
        console.log('request /question/get: ', err)
      })
    },
    // handleCompareSetting () {
    //   this.$prompt('运行截图配置', '提示', {
    //     confirmButtonText: '确定',
    //     cancelButtonText: '取消',
    //     // inputPattern: /[\w!#$%&'*+/=?^_`{|}~-]+(?:\.[\w!#$%&'*+/=?^_`{|}~-]+)*@(?:[\w](?:[\w-]*[\w])?\.)+[\w](?:[\w-]*[\w])?/,
    //     inputPattern: /\d*/,
    //     inputErrorMessage: '单位像素，输入纯数字'
    //   }).then(({ value }) => {
    //     this.$message({
    //       type: 'success',
    //       message: '配置成功'
    //     })
    //   }).catch(() => {
    //     this.$message({
    //       type: 'info',
    //       message: '取消输入'
    //     })
    //   })
    // },
    // refreashQuestionText () {
    //   this.questionTexts = []
    //   this.questionList.forEach(element => {
    //     if (this.form.questionIds.indexOf(element.questionId) !== -1) {
    //       this.questionTexts.push({
    //         questionId: element.questionId,
    //         questionText: element.content
    //       })
    //     }
    //   })
    // },
    changeScore (answerId, score) {
      for (let i = 0; i < this.answerList.length; i++) {
        if (this.answerList[i].answerId === answerId) {
          this.answerList[i].score = score
        }
      }
    }
  },

  watch: {
    'form.questionIds': {
      handler (val) {
        console.log('newIds', val)
        if (val == null || val.length === 0) { return }
        if (this.form.timer != null) {
          clearTimeout(this.form.timer)
        }
        this.form.timer = setTimeout(() => {
          this.askPictureUrl()
          this.form.timer = null
        }, 2000)
      }
    },
    ansPicUrls: {
      handler () { },
      deep: true
    },
    answerList: {
      handler () { },
      deep: true
    },
    'compareSetting.width': {
      handler () {
        if (this.form.timer != null) {
          clearTimeout(this.form.timer)
        }
        this.form.timer = setTimeout(() => {
          this.askPictureUrl()
          this.form.timer = null
        }, 1500)
      }
    },
    autoCompare: {
      handler () {
        this.canCompareNumber = 0
        if (this.questionList.length === 0 && this.isSending === false) {
          this.refreshQuestionList()
          this.isSending = true
        }
      }
    }
  },
  components: {
    JudgeVue

  },
  computed: {
    showAnswerList () {
      let resList = this.answerList.filter((value) => {
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
      resList.sort((a, b) => {
        if (a.questionId !== b.questionId) {
          return a.questionId - b.questionId
        } else if (a.userId !== b.userId) {
          return a.userId - b.userId
        } else {
          return 1
        }
      })
      return resList
    },
    questionPicList () {
      if (this.form.questionIds == null || this.form.questionIds.length === 0) {
        console.log('questionPicList get from questionList')
        return this.questionList
      } else {
        let res = []
        for (let i = 0; i < this.form.questionIds.length; i++) {
          res.push({
            questionId: this.form.questionIds[i]
          })
        }
        console.log('questionPicList get from questionIds', res)
        return res
      }
    },
    totalCount () {
      if (this.answerList == null) return 0

      return this.showAnswerList.length
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
  height: 60px;
  line-height: 60px;
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

.box {
  margin-bottom: 5px;
}

.student-submit-box {
  margin-top: 15px;
}

.question-container>>>.el-form-item>>>.el-form-item__content {
  vertical-align: center !important;
}

.multiple-select>>>.el-input__inner {
  height: 60px !important;
}

.question-container>>>.el-form-item__content {
  line-height: 60px;
}

.el-pagination {
  text-align: center;
  margin-top: 5px;
}
</style>
