<template>
    <el-row>
        <h2>各实验题目参考图片链接</h2>
        <!-- <p class="tip">Tip：鼠标右键图片 - 复制图片链接 - 在对应实验需要图片的地方引入即可。 </p> -->
        <el-container class="img-list-box">
            <el-card v-for="item in imgList" :key="item.imgId" class="img-card">
                <h3>{{item.questionTitle}}</h3>
                <div class="img-box">
                    <img :src="item.url" class="item-img">
                </div>
                <div class="img-url-text-box">
                    <el-button type="primary" @click="copyUrl(item.url)" class="img-url-text" >复制链接</el-button>
                </div>
            </el-card>
        </el-container>
    </el-row>
</template>
<script>
export default {
  data () {
    return {
      imgList: null
    }
  },
  mounted () {
    this.refreshImgList()
  },
  methods: {
    copyUrl (val) {
      // 模拟 输入框
      var cInput = document.createElement('input')
      cInput.value = val
      document.body.appendChild(cInput)
      cInput.select() // 选取文本框内容

      // 执行浏览器复制命令
      // 复制命令会将当前选中的内容复制到剪切板中（这里就是创建的input标签）
      // Input要在正常的编辑状态下原生复制方法才会生效

      document.execCommand('copy')

      this.$message({
        type: 'success',
        message: '复制成功'
      })
      // 复制成功后再将构造的标签 移除
      document.body.removeChild(cInput)
    },

    // 获取图片数据
    refreshImgList () {
      this.axios({
        url: this.baseUrl + '/img/list',
        method: 'get'
      }).then((res) => {
        if (res.data && this) {
          this.imgList = res.data.data
        }
        // console.log(this.imgList)
      }).catch((err) => {
        console.log('search /img/list: ', err)
      })
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

h2 {
    text-align: center;
}

h3 {
    width: 100%;
    text-align: center;
    text-overflow: ellipsis;
    overflow: hidden;
    margin-top: 0px;
    margin-bottom: 10px;
}

.tip {
    text-indent: 2rem;
    margin-bottom: 10px;
}

.img-list-box {
    display: flex;
    justify-items: center;
    justify-content: space-around;
    flex-wrap: wrap;
}

@media screen and (min-width:768px) {
    .img-card {
        max-width: 33.0%;
        margin-bottom: 10px;
    }

}

.img-box {
    text-align: center;
}

.item-img {
    height: 150px;
}
.img-url-text-box{
    position: relative;

    text-align: center;
    width:100%;
}
.img-url-text {
    text-align: center;
}
</style>
