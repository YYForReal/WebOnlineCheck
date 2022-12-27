<template>
  <div class="container">
    <el-table :data="questionList" style="width: 100%">
      <el-table-column type="expand">
        <template slot-scope="props">
          <el-form label-position="left" inline class="demo-table-expand">
            <el-form-item label="实验标题">
              <span>{{ props.row.title }}</span>
            </el-form-item>
            <el-form-item label="截止日期">
              <span>{{ MyFilter.dateFormat(props.row.ddl) }}</span>
            </el-form-item>
            <el-form-item label="展示方式" >
              <span>{{ props.row.display == 1 ? 'iframe' : '图片' }}</span>
            </el-form-item>
              <!-- v-if="props.row.description" -->

            <el-form-item label="说明内容" >
              <span>{{ props.row.description }}</span>
            </el-form-item>

            <el-form-item label="比对内容" style="width:100%">
              <span>{{ props.row.content }}</span>
            </el-form-item>
            <!-- v-if="props.row.example" -->
            <el-form-item label="模板HTML" style="width:100%" >
              <span>{{ props.row.example }}</span>
            </el-form-item>
          </el-form>
        </template>
      </el-table-column>
      <el-table-column label="实验标题" prop="title">
      </el-table-column>
      <!-- <el-table-column label="比对文本" prop="name">
    </el-table-column> -->
      <!-- <el-table-column label="描述" prop="desc">
    </el-table-column> -->
      <el-table-column label="操作">
        <template slot-scope="scope">
          <el-button size="mini" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <!-- <el-button type="primary" @click="handleAdd">增加</el-button> -->
    <el-button class="fix-button" type="primary" icon="el-icon-edit" circle @click="handleAdd" ></el-button>

    <el-dialog :title="form.isDelete ? '确认删除？' : '实验题目设置'" :visible.sync="dialogFormVisible">
      <el-form :model="form">
        <el-form-item label="实验标题" :label-width="formLabelWidth">
          <el-input v-model="form.title" :disabled="form.isDelete" placeholder = "实验的标题"></el-input>
        </el-form-item>
        <el-form-item label="比对文本" :label-width="formLabelWidth">
          <el-input v-model="form.content" type="textarea" :rows="8" placeholder = "需要比对的页面文本" :disabled="form.isDelete"></el-input>
        </el-form-item>
        <el-form-item label="比对HTML" :label-width="formLabelWidth">
          <el-input v-model="form.html" type="textarea" :rows="8" placeholder = "参考答案" :disabled="form.isDelete"></el-input>
        </el-form-item>
        <el-form-item label="展示方式" :label-width="formLabelWidth">
          <!-- <el-input v-model="form.display" type="se" :rows="8" :disabled="form.isDelete"></el-input> -->
          <el-select v-model="form.display" placeholder="选择展示方式（默认图片）" :disabled="form.isDelete">
            <el-option label="图片" value="0" checked />
            <el-option label="iframe" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止时间" :label-width="formLabelWidth">
          <el-date-picker v-model="form.ddl" type="date" placeholder = "该实验的截止日期"   :disabled="form.isDelete"></el-date-picker>
        </el-form-item>
        <el-form-item label="实验说明内容" :label-width="formLabelWidth">
          <el-input v-model="form.description" type="textarea" placeholder = "展示给学生的说明性文本" :rows="8" :disabled="form.isDelete"></el-input>
        </el-form-item>
        <el-form-item label="模板HTML" :label-width="formLabelWidth">
          <el-input v-model="form.example" type="textarea" :rows="8" placeholder = "展示给学生的模板HTML，方便测试Javascript"  :disabled="form.isDelete"></el-input>
        </el-form-item>

        <!-- <el-form-item label="CSS属性" v-for="(attribute,index) in form.attributes" :key="index" :label-width="formLabelWidth" >
      <el-input v-model="form.content"  type="textarea"  :disabled="form.isDelete" ></el-input>
    </el-form-item> -->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="handleConfirm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
  <!-- <el-form ref="QuestionForm" :model="form" :rules="rules" class="setting-container">
    <h2>提交答案</h2>
    <div class="inline-box">
      <el-form-item prop="questionId">
        <el-select v-model="form.questionId" placeholder="请选择实验题目">
          <el-option v-for="item in questionList" :key="item.questionId" :label="item.title" :value="item.questionId" />
        </el-select>
      </el-form-item>
      <el-form-item class="submit-btn-box">
        <el-button type="primary" @click="handleSumbit" :loading="logining">提交</el-button>
      </el-form-item>
    </div>
    <el-form-item prop="content">
      <el-input class="html-text-box" type="textarea" v-model="form.content" auto-complete="off" resize="none"
        placeholder="在此输入html文本"></el-input>
    </el-form-item>
  </el-form> -->
</template>

<script>
import { MyFilter } from '../../utils/myFilter'
export default {
  data () {
    // 表单校验,默认的用户名，密码
    return {
      questionList: [],
      logining: false,
      rules: {
        content: [{
          required: true, message: '请输入html文本', trigger: 'blur'
        }],
        questionId: [{
          required: true, message: '请选择实验题目', trigger: 'blur'
        }]
      },
      MyFilter: MyFilter,
      dialogFormVisible: false,
      form: {
        questionId: null,
        title: null,
        content: null,
        userId: null,
        username: null,
        html: null,
        isDelete: false,
        display: 0,
        ddl: new Date(),
        description: null,
        example: null
        // attributes: []
      },
      formLabelWidth: '120px'
    }
  },
  components: {

  },
  mounted () {
    this.refreshQuestionList()
  },
  methods: {
    handleAdd () {
      this.dialogFormVisible = true
      this.form.questionId = null
      this.form.content = null
      this.form.title = null
      this.form.html = null
      this.form.display = 0
      this.form.isDelete = false
      this.form.description = ''
      this.form.ddl = new Date()
      this.form.example = ''
    },
    handleEdit (index, row) {
      this.dialogFormVisible = true
      this.form.questionId = row.questionId
      this.form.content = row.content
      this.form.title = row.title
      this.form.html = row.html
      this.form.display = row.display
      this.form.isDelete = false
      this.form.description = row.description
      this.form.ddl = row.ddl
      this.form.example = row.example
    },
    handleDelete (index, row) {
      this.handleEdit(index, row)
      this.form.isDelete = true
    },
    handleConfirm () {
      this.dialogFormVisible = false
      this.postQuestion()
    },
    deleteQuestion () {
      this.axios({
        url: this.baseUrl + '/question/delete',
        method: 'post',
        transformRequest: [(data) => {
          var oMyForm = new FormData()
          oMyForm.append('userId', this.account.userid)
          oMyForm.append('username', this.account.username)
          oMyForm.append('questionId', this.form.questionId)
          return oMyForm
        }]
      }).then((res) => {
        // console.log('Res:', res)
        if (res.data.status === 200) {
          this.refreshQuestionList()
          this.$message({
            type: 'success',
            message: res.data.message,
            showClose: true
          })
        } else {
          this.$message({
            type: 'error',
            message: res.data.message,
            showClose: true
          })
        }
      }).catch(() => {
        this.$message({
          type: 'error',
          message: '服务器异常',
          showClose: true
        })
      })
    },
    postQuestion () {
      if (this.form.isDelete === true) {
        this.deleteQuestion()
        return
      }
      // 提交新问题
      if (this.form.questionId == null) {
        this.axios({
          url: this.baseUrl + '/question/post',
          method: 'post',
          transformRequest: [(data) => {
            var oMyForm = new FormData()
            oMyForm.append('userId', this.account.userid)
            oMyForm.append('username', this.account.username)
            oMyForm.append('title', this.form.title)
            oMyForm.append('content', this.form.content)
            oMyForm.append('html', this.form.html)
            oMyForm.append('description', this.form.description)
            oMyForm.append('example', this.form.example)
            oMyForm.append('ddl', this.form.ddl)
            oMyForm.append('display', this.form.display)
            return oMyForm
          }]
        }).then((res) => {
          // console.log('Res:', res)
          if (res.data.status === 200) {
            this.refreshQuestionList()
            this.$message({
              type: 'success',
              message: res.data.message,
              showClose: true
            })
          } else {
            this.$message({
              type: 'error',
              message: res.data.message,
              showClose: true
            })
          }
        }).catch(() => {
          this.$message({
            type: 'error',
            message: '服务器异常',
            showClose: true
          })
        })
      } else {
        // 更新问题
        this.axios({
          url: this.baseUrl + '/question/update',
          method: 'post',
          transformRequest: [(data) => {
            var oMyForm = new FormData()
            oMyForm.append('userId', this.account.userid)
            oMyForm.append('username', this.account.username)
            oMyForm.append('title', this.form.title)
            oMyForm.append('content', this.form.content)
            oMyForm.append('questionId', this.form.questionId)
            oMyForm.append('html', this.form.html)
            oMyForm.append('description', this.form.description)
            oMyForm.append('example', this.form.example)
            oMyForm.append('ddl', this.form.ddl)
            oMyForm.append('display', this.form.display)
            return oMyForm
          }]
        }).then((res) => {
          // console.log('Res:', res)
          if (res.data.status === 200) {
            this.refreshQuestionList()
            this.$message({
              type: 'success',
              message: res.data.message,
              showClose: true
            })
          } else {
            this.$message({
              type: 'error',
              message: res.data.message,
              showClose: true
            })
          }
        }).catch(() => {
          this.$message({
            type: 'error',
            message: '服务器异常',
            showClose: true
          })
        })
      }
    },
    refreshQuestionList () {
      this.axios({
        url: this.baseUrl + '/question/get',
        method: 'get'
      }).then((res) => {
        this.questionList = res.data
      }).catch((err) => {
        console.log('refreshQuestionList /question/get: ', err)
      })
    },
    handleSumbit () {
      this.$refs.AnswerFrom.validate((valid) => {
        if (valid) {
          this.checkUser().then((res) => {
            if (res.data === 'OK') {
              this.form.userId = this.account.userid
              this.post()
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
              message: '未处于正确的登录状态，请重新登录',
              showClose: true
            })
          })
        } else {
          return false
        }
      })
    },
    // 校验 用户名 + 学号 是否正确
    checkUser () {
      return this.axios({
        url: this.baseUrl + '/user/check',
        method: 'post',
        transformRequest: [(data) => {
          var oMyForm = new FormData()
          oMyForm.append('userId', this.account.userid)
          oMyForm.append('username', this.account.username)
          return oMyForm
        }]
      })
    }
  },
  computed: {
    account () {
      return this.$store.state.account
    }
  }
}
</script>
<style scoped >
.container {
  width: 100%;
}

.demo-table-expand {
  font-size: 0;
}

.demo-table-expand label {
  width: 90px;
  color: #99a9bf;
}

.demo-table-expand .el-form-item {
  margin-right: 0;
  margin-bottom: 0;
  width: 50%;
}

.demo-table-expand>>>.el-form-item__label {
  font-weight: bold;
}

.fix-button{
  position: fixed;
  width:70px;
  height:70px;
  font-size: 40px;
  right:50px;
  bottom:100px;
}
</style>
