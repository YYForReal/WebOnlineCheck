<template>
    <el-row>
        <h2>各实验题目参考图片链接</h2>
        <p class="tip">Tip：鼠标右键图片 - 复制图片链接 - 在对应实验需要图片的地方引入即可。 </p>
        <el-container class="img-list-box">
            <el-card v-for="item in imglist" :key="item.imgId" class="img-card">
                <h3>{{item.questionTitle}}</h3>
                <div class="img-box">
                    <img :src="item.url" class="item-img">
                </div>
                <p class="img-url-text">链接：{{item.url}}</p>
            </el-card>
        </el-container>
    </el-row>
</template>
<script>
export default {
  data () {
    return {
      imglist: [
        {
          imgId: 1,
          questionTitle: '实验（七）新闻',
          url: 'https://source.acexy.cn/view/YPFg5qJ'
        }
      ]
    }
  },
  created () {

  }
}
</script>
<style scoped >
/* h1,h2,h3,h4{
    margin: 0;
} */
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
    width: 200px;
}

.img-url-text {
    text-align: center;
}
</style>
