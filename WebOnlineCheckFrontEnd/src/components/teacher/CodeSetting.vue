<template>
  <!-- <div class="">
    <div class="question-example-box code-box" v-if="(nowQuestion && nowQuestion.example)">
      <el-button type="primary" class="copy-btn" @click="copyCode(nowQuestion.example)">复制代码</el-button>
      <pre>
        <code>{{ nowQuestion.example | trim }}</code>
      </pre>
    </div>
  </div> -->
  <el-container class="container">
    <el-header>
      <h1>校验代码编辑</h1>
    </el-header>
    <el-main>
      <el-collapse v-model="activeNames" @change="handleChange">
        <el-collapse-item v-for="(item, index) in codeList" :key="index" :title="choosequestion(item.questionId).title"
          :name="index">
          <el-card class="box-card code-box">
            <el-button-group class="btn-group">
              <el-button type="primary" icon="el-icon-edit"  @click="handleEdit(item)"></el-button>
              <el-button type="danger" icon="el-icon-delete"  @click="handleDelete(item.codeId,item.questionId)"></el-button>
            </el-button-group>
            <h3>模板HTML</h3>
            <pre><code>{{choosequestion(item.questionId).example}}</code></pre>
            <h3>校验JS代码</h3>
            <pre><code>{{ item.code }}</code></pre>
          </el-card>
        </el-collapse-item>
      </el-collapse>
    </el-main>
    <el-dialog title="校验代码编辑" :visible.sync="dialogFormVisible">
      <el-form :model="form">
        <!-- <el-form-item label="校验Id" :label-width="formLabelWidth">
          <el-input v-model="form.codeId"  disabled></el-input>
        </el-form-item> -->
        <el-form-item label="对应实验" :label-width="formLabelWidth">
          <el-select v-model="form.questionId" placeholder="请选择对应的实验">
            <el-option v-for="question in questionList" :key="question.questionId" :label="question.title"
              :value="question.questionId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="校验代码" :label-width="formLabelWidth">
          <el-input v-model="form.code" type="textarea" :rows="15"></el-input>
        </el-form-item>

      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogFormVisible = false">取 消</el-button>
        <el-button type="primary" @click="confirmEdit()">确 定</el-button>
      </div>
    </el-dialog>
    <el-button class="fix-button" type="primary" icon="el-icon-edit" circle @click="handleAdd"></el-button>
  </el-container>
</template>

<script>
export default {
  data () {
    return {
      codeList: [],
      questionList: [],
      dialogFormVisible: false,
      form: {
        codeId: null,
        questionId: null,
        code: ''
      },
      formLabelWidth: '120px'
    }
  },
  created () {
    this.refreshQuestionList()
  },
  methods: {
    choosequestion (questionId) {
      for (let i = 0; i < this.questionList.length; i++) {
        if (questionId === this.questionList[i].questionId) {
          return this.questionList[i]
        }
      }
      return null
    },
    refreshQuestionList () {
      this.axios({
        url: this.baseUrl + '/question/getlist',
        method: 'get'
      }).then((res) => {
        // this.questionList = res.data
        console.log('questionList:', this.questionList)
        for (let i = 0; i < res.data.length; i++) {
          if (res.data[i].example != null) {
            this.questionList.push(res.data[i])
          }
        }
        this.getCodeList()
      }).catch((err) => {
        console.log('search question: ', err)
      })
    },
    getCodeList () {
      return this.axios({
        url: this.baseUrl + '/code/get',
        method: 'get'
      }).then((res) => {
        console.log('res', res.data.data)
        this.codeList = res.data.data
        // this.codeList = res
      })
    },
    handleAdd () {
      this.dialogFormVisible = true
      this.form.codeId = null
      this.form.questionId = null
      this.form.code = null
    },
    handleEdit (item) {
      // console.log(item)
      this.dialogFormVisible = true
      this.form.codeId = item.codeId
      this.form.questionId = item.questionId
      this.form.code = item.code
      // console.log(this.form)
      // this.$confirm('是否删除？', '确认信息', {
      //   distinguishCancelAndClose: true,
      //   confirmButtonText: '确认',
      //   cancelButtonText: '放弃删除'
      // }).then(() => {
      //   this.postDelete()
      // }).catch(() => {
      //   this.$message({
      //     type: 'info',
      //     message: '放弃删除'
      //   })
      // })
    },
    confirmEdit () {
      this.dialogFormVisible = false
      let url = this.baseUrl
      if (this.form.codeId == null || this.form.codeId === 'null') {
        url += '/code/add'
      } else {
        url += '/code/update'
      }

      this.axios({
        url,
        method: 'post',
        transformRequest: [(data) => {
          var oMyForm = new FormData()
          oMyForm.append('codeId', this.form.codeId)
          oMyForm.append('questionId', this.form.questionId)
          oMyForm.append('code', this.form.code)
          return oMyForm
        }]
      }).then((res) => {
        if (res.data.status === 200) {
          this.$message({
            type: 'success',
            message: '更新成功'
          })
          this.getCodeList()
        } else {
          this.$message({
            type: 'warning',
            message: '更新失败'
          })
        }
      })
    },
    handleDelete (codeId, questionId) {
      this.$confirm('是否删除？', '确认信息', {
        distinguishCancelAndClose: true,
        confirmButtonText: '确认',
        cancelButtonText: '放弃删除'
      }).then(() => {
        this.postDelete(codeId, questionId)
      }).catch(() => {
        this.$message({
          type: 'info',
          message: '放弃删除'
        })
      })
    },
    postDelete (codeId, questionId) {
      console.log('delte,', codeId)
      this.axios({
        url: this.baseUrl + '/code/delete',
        method: 'post',
        transformRequest: [(data) => {
          var oMyForm = new FormData()
          oMyForm.append('codeId', codeId)
          oMyForm.append('questionId', questionId)
          return oMyForm
        }]
      }).then((res) => {
        if (res.data.status === 200) {
          // console.log('res', res.data.data)
          this.$message({
            type: 'success',
            message: '删除成功'
          })
        } else {
          this.$message({
            type: 'warning',
            message: '删除失败'
          })
        }
      })
    }
  },
  watch: {
    form: {
      handler () { },
      deep: true
    }
  }
}
</script>

<style scoped >

.container{
  width:100%;
}

.container >>> .el-collapse-item__header{
  font-size: large;
  font-weight: bolder;
}

.container >>> .el-collapse-item__header.is-active{
  color: blue;
}

.text {
  font-size: 14px;
}

.item {
  margin-bottom: 18px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both
}

.box-card {
  min-width: 480px;
  width: 90%;
  margin: 15px;
  position: relative;

}

.btn-group{
  position: absolute;
  right: 20px;
  top:26px;
}
</style>
