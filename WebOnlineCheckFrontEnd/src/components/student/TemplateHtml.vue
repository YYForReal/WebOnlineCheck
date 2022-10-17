<template>
  <div id="template-box" v-html="htmlText">
    <!-- <iframe src="" frameborder="0"></iframe> -->
  </div>
</template>
<script>
export default {
  name: 'TemplateHtml',
  data () {
    return {
      htmlText: ''
    }
  },
  mounted () {
    // request htmlText
    let answerId = this.$route.query.answerId
    if (answerId != null) {
      // console.log('answerId:', answerId)
      this.axios({
        url: this.baseUrl + '/answer/search?answerId=' + answerId,
        method: 'get'
      }).then((res) => {
        if (res.data.status === 200) {
          this.htmlText = res.data.data
        } else {
          console.log(res.data.message)
        }
      }).catch((err) => {
        console.log('search answer: ', err)
      })
    } else {
      let questionId = this.$route.query.questionId
      // console.log('questionId：', questionId)
      this.axios({
        url: this.baseUrl + '/question/search?questionId=' + questionId,
        method: 'get'
      }).then((res) => {
        if (res.data.status === 200) {
          this.htmlText = res.data.data
        } else {
          console.log(res.data.message)
        }
      }).catch((err) => {
        console.log('search answer: ', err)
      })
    }
  }
}
</script>
<style>
#template-box {
  margin: initial!important;
  padding: initial!important;
  height:100%;
  border-width: 0px!important;
  border-top: 0;
  margin-top: initial!important;
  margin-left: initial!important;
  margin-right: initial!important;
  margin-bottom: initial!important;
}

</style>
