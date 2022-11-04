<template>
    <div class="container">
        <el-table :data="imgList" style="width: 100%">
            <el-table-column label="图片">
                <template slot-scope="scope">
                    <img :src="scope.row.url" style="width:80px;height:80px;">
                </template>
            </el-table-column>

            <el-table-column label="对应实验" prop="questionTitle"></el-table-column>
            <el-table-column label="操作">
                <template slot-scope="scope">
                    <el-button size="mini" @click="handleEdit(scope.$index, scope.row)">编辑</el-button>
                    <el-button size="mini" type="danger" @click="handleDelete(scope.$index, scope.row)">删除</el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-button class="fix-button" type="primary" icon="el-icon-edit" circle @click="handleAdd" ></el-button>

        <el-dialog :title="form.isDelete?'确认删除？':'实验题目设置'" :visible.sync="dialogFormVisible">
            <el-form :model="form">
                <el-form-item label="对应实验" :label-width="formLabelWidth">
                    <el-select v-model="form.questionId" placeholder="请选择对应实验" >
                        <el-option v-for="item in questionList" :key="item.questionId" :label="item.title"
                            :value="item.questionId" />
                    </el-select>
                </el-form-item>
                <el-form-item label="图片链接" :label-width="formLabelWidth">
                    <el-input v-model="form.url" :disabled="form.isDelete" ></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="handleConfirm">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>
<script>
export default {
  data () {
    // 表单校验,默认的用户名，密码
    return {
      imgList: [],
      questionList: [],
      dialogFormVisible: false,
      form: {
        imgId: null,
        questionId: null,
        questionTitle: null,
        url: null,
        isDelete: false
      },
      formLabelWidth: '120px'
    }
  },
  mounted () {
    this.refreshImgList()
  },
  methods: {
    handleAdd () {
      this.dialogFormVisible = true
      this.form.questionId = null
      this.form.url = null
      this.form.questionTitle = null
      this.form.imgId = null
      this.form.isDelete = false
      if (this.questionList == null || this.questionList.length === 0) {
        this.refreshQuestionList()
      }
    },
    handleEdit (index, row) {
      this.dialogFormVisible = true
      this.form.questionId = row.questionId
      this.form.url = row.url
      this.form.questionTitle = row.questionTitle
      this.form.imgId = row.imgId
      this.form.isDelete = false
      if (this.questionList == null || this.questionList.length === 0) {
        this.refreshQuestionList()
      }
    },
    handleDelete (index, row) {
      this.dialogFormVisible = true
      this.form.questionId = row.questionId
      this.form.url = row.url
      this.form.questionTitle = row.questionTitle
      this.form.imgId = row.imgId
      this.form.isDelete = true
    },
    handleConfirm () {
      this.dialogFormVisible = false
      this.postImg()
    },
    // 删除图片
    deleteImg () {
      this.axios({
        url: this.baseUrl + '/img/delete',
        method: 'post',
        transformRequest: [(data) => {
          var oMyForm = new FormData()
          oMyForm.append('userId', this.account.userid)
          oMyForm.append('username', this.account.username)
          oMyForm.append('imgId', this.form.imgId)
          return oMyForm
        }]
      }).then((res) => {
        console.log('Res:', res)
        if (res.data.status === 200) {
          this.refreshImgList()
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
    // 提交图片修改总入口
    postImg () {
      if (this.form.isDelete === true) {
        this.deleteImg()
        return
      }
      // 提交新问题
      //   if (this.form.imgId == null) {
      this.axios({
        url: this.baseUrl + '/img/post',
        method: 'post',
        transformRequest: [(data) => {
          var oMyForm = new FormData()
          oMyForm.append('questionId', this.form.questionId)
          oMyForm.append('url', this.form.url)
          oMyForm.append('imgId', this.form.imgId)
          return oMyForm
        }]
      }).then((res) => {
        if (res.data.status === 200) {
          this.refreshImgList()
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
          message: '提交失败',
          showClose: true
        })
      })
      //   }
      //   else {
      //     // 更新问题
      //     this.axios({
      //       url: this.baseUrl + '/question/update',
      //       method: 'post',
      //       transformRequest: [(data) => {
      //         var oMyForm = new FormData()
      //         oMyForm.append('userId', this.account.userid)
      //         oMyForm.append('username', this.account.username)
      //         oMyForm.append('title', this.form.title)
      //         oMyForm.append('content', this.form.content)
      //         oMyForm.append('imgId', this.form.imgId)
      //         oMyForm.append('html', this.form.html)
      //         return oMyForm
      //       }]
      //     }).then((res) => {
      //       console.log('Res:', res)
      //       if (res.data.status === 200) {
      //         this.refreshImgList()
      //         this.$message({
      //           type: 'success',
      //           message: res.data.message,
      //           showClose: true
      //         })
      //       } else {
      //         this.$message({
      //           type: 'error',
      //           message: res.data.message,
      //           showClose: true
      //         })
      //       }
      //     }).catch(() => {
      //       this.$message({
      //         type: 'error',
      //         message: '服务器异常',
      //         showClose: true
      //       })
      //     })
      //   }
    },
    // 获取图片数据
    refreshImgList () {
      this.axios({
        url: this.baseUrl + '/img/list',
        method: 'get'
      }).then((res) => {
        this.imgList = res.data.data
        console.log(this.imgList)
      }).catch((err) => {
        console.log('search /img/get: ', err)
      })
    },
    // 获取问题数据
    refreshQuestionList () {
      this.axios({
        url: this.baseUrl + '/question/getlist',
        method: 'get'
      }).then((res) => {
        this.questionList = res.data
      }).catch((err) => {
        console.log('search /img/get: ', err)
      })
    }
  },
  computed: {
    account () {
      return this.$store.state.account
    }
  },
  watch: {
    imgList: {
      handler () {},
      deep: true
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

/* .fix-button{
  position: fixed;
  width:70px;
  height:70px;
  font-size: 40px;
  right:50px;
  bottom:100px;
} */
</style>
