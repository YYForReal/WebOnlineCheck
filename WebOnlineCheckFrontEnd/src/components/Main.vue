<template>
  <div calss="container">
    <el-row class="tac">
      <el-col class="side-bar" :span="3">
        <el-menu default-active="list" class="el-menu-vertical" :router="true">
          <el-menu-item index="list" id="doc-list">
            <i class="el-icon-document"></i>
            <span slot="title">已提交列表</span>
          </el-menu-item>
          <el-menu-item index="imgs">
            <i class="el-icon-s-promotion"></i>
            <span slot="title">参考图片链接</span>
          </el-menu-item>
          <el-menu-item index="answer" id="answer-item">
            <i class="el-icon-menu"></i>
            <span slot="title">实验提交</span>
          </el-menu-item>
        </el-menu>
        <!-- <span class="registration">123123</span> -->
      </el-col>
      <el-col :span="21">
        <el-badge value="HTML" class="item">
          <el-button type="info" class="fix-button" icon="el-icon-message" @click="dialogVisible = true" circle>
          </el-button>
        </el-badge>
        <el-badge value="JS" class="item-js" >
          <el-button type="info" class="fix-button-js" :class="{ 'dialogShaking': dialogShaking }" icon="el-icon-message" @click="dialogVisibleJS = true" circle>
          </el-button>
        </el-badge>
        <router-view></router-view>
      </el-col>
    </el-row>
    <!-- 一些无法展示效果的案例 -->
    <el-dialog title="一些无法展示效果的案例" :visible.sync="dialogVisible" width="90%" class="carousel-container">
      <el-carousel arrow="always">
        <!-- v-for="answer in scoreList" :key="answer.answerId" :title="answer.questionTitle" -->
        <!-- :name="answer.answerId" -->
        <el-carousel-item v-for="(tip, index) in dialogTips" :key="index" :title="tip.title">
          <!-- <el-divider content-position="left" >{{}}</el-divider> -->
          <h2>{{ tip.title }}</h2>
          <p>{{ tip.content }}</p>
          <div class="code-box-container">
            <div class="code-box">
              <p v-if="tip.errorCode">网站的无效代码</p>
              <pre v-if="tip.errorCode"><code>{{ tip.errorCode }}</code></pre>
            </div>
            <div class="code-box">
              <p v-if="tip.rightCode">有效代码</p>
              <pre v-if="tip.rightCode"><code>{{ tip.rightCode }}</code></pre>
              <!-- <el-input type="textarea" disabled rows="8" v-if="tip.rightCode" v-model="tip.rightCode"></el-input> -->
            </div>
          </div>
        </el-carousel-item>
        <!-- <el-divider><i class="el-icon-mobile-phone"></i></el-divider> -->
        <!-- <el-divider content-position="right">阿里云</el-divider> -->
      </el-carousel>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
      </span>
    </el-dialog>

    <!-- 一些JS错误的代码 -->
    <el-dialog title="JS实验无效代码" :visible.sync="dialogVisibleJS" width="90%" class="carousel-container">
      <el-carousel arrow="always">
        <el-carousel-item v-for="(tip, index) in dialogJSTips" :key="index" :title="tip.title">
          <h2>{{ tip.title }}</h2>
          <p v-html="tip.content"></p>
          <div class="code-box-container">
            <div v-if="tip.errorCode" class="code-box" :class="{ 'code-full-box': !tip.rightCode }">
              <p>冲突代码</p>
              <pre><code>{{ tip.errorCode }}</code></pre>
              <!-- <el-input type="textarea" disabled rows="8" v-if="tip.errorCode" v-model="tip.errorCode"></el-input> -->
            </div>
            <div v-if="tip.rightCode" class="code-box" :class="{ 'code-full-box': !tip.errorCode }">
              <p>有效代码</p>
              <pre><code>{{ tip.rightCode }}</code></pre>
              <!-- <el-input type="textarea" disabled rows="8" v-if="tip.rightCode" v-model="tip.rightCode"></el-input> -->
            </div>
          </div>
        </el-carousel-item>
        <!-- <el-divider><i class="el-icon-mobile-phone"></i></el-divider> -->
        <!-- <el-divider content-position="right">阿里云</el-divider> -->
      </el-carousel>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="dialogVisibleJS = false">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
// import Driver from 'Driver'
export default {
  name: 'Main',
  data () {
    return {
      timer: null,
      dialogShaking: false,
      dialogVisible: false,
      dialogTips: [
        {
          title: 'div选择器的问题',
          content: '因为预览的HTML结构最外层套了两层的div，所以为了避免影响外部div的样式。尽量减少使用div标签选择器',
          errorCode: '<style>\n\tdiv{\n\t\t...\n\t}\n</style>\n\n<div class="box"></div>',
          rightCode: '<style>\n\t.box{\n\t\t...\n\t}\n</style>\n\n<div class="box"></div>'
        },
        {
          title: 'body标签的行内样式',
          content: '不要把body的样式写在body标签内，可以考虑写在内部样式表内。',
          errorCode: '<body bgcolor="#123456"></body>',
          rightCode: `<style>\n\tbody{\n\t\tbackground-color:"#123456";\n\t}\n</style>`
        },
        {
          title: '内部样式表的大小写问题',
          content: '如果内部样式表和HTML标签大小写不匹配，也会没有效果。HTML可以全用小写字母和字符“-”',
          errorCode: '<style>\n\t#hello{\n\t\t...\n\t}\n</style>\n\n<p id="#Hello"></p>',
          rightCode: '<style>\n\t#hello-box{\n\t\t...\n\t}\n</style>\n\n<p id="#hello-box"></p>'
        }
      ],
      dialogVisibleJS: false,
      dialogJSTips: [
        {
          title: '校验失败特例——禁止页面刷新',
          content: '禁止使用页面刷新的API进行页面刷新，否则会一直卡住校验。<br>同理，之前的HTML实验表单提交也要注意禁止页面刷新，确保submit时return false。<br>',
          errorCode: '// 禁止使用\nwindow.location.reload()\n'
        },
        {
          title: '绑定DOM事件（一）',
          content: '给DOM绑定事件时：推荐直接在JS中增加事件监听器。<br>',
          rightCode: '// 方式1\ndom.addEventListener("click",function(){...})\n// 方式2\ndom.onclick = function(){...} \n'
        },
        {
          title: '绑定DOM事件（二）',
          content: '给DOM绑定事件时：不推荐：在HTML标签上声明onclick。<br>如果采取该方式，需要<b>额外将函数xxx手动挂载到window对象上</b>。 ',
          errorCode: '<button onclick="xxx"></button>',
          rightCode: '// 方式3\n<button onclick = "xxx()"></button>\nJS部分:\nfunction xxx(){...}\nwindow.xxx = xxx;\n'
        },
        {
          title: '变量声明',
          content: '网站提交的代码中，JS变量需声明（var、let、const），若不声明而直接使用，本地虽然可以，但网站会报错。',
          errorCode: 'count = 1 ;\nfor(i=0;i<10;i++){\n\t...\n}',
          rightCode: 'var count = 1 ; \nfor(var i=0;i<10;i++){\n\t...\n}'
        },
        {
          title: '增加分号',
          content: '网站提交的代码中，JS语句需增加分号，若不增加，本地虽然可以，但网站<b>可能</b>会报错。（<b>在代码不换行的情况下</b>）',
          errorCode: 'var count = 1 \ncount ++',
          rightCode: 'var count = 1 ; \ncount ++;'
        }
      ]
    }
  },
  computed: {
    account () {
      return this.$store.state.account
    }
  },
  created () {
    if (this.account === null || this.account === undefined) {
      let storage = sessionStorage.getItem('web-account')
      let obj = JSON.parse(window.decodeURIComponent(window.atob(storage)))
      if (storage == null || obj.username == null || obj.userid == null) {
        this.$router.push('/login')
      } else {
        this.$store.dispatch('handleLogin', obj)
      }
    }
  },
  mounted () {
    // eslint-disable-next-line no-undef
    const driver = new Driver({
      allowClose: false, // 禁止点击外部关闭
      doneBtnText: '完成', // 完成按钮标题
      stageBackground: '#fff', // 引导对话的背景色
      closeBtnText: '关闭', // 关闭按钮标题
      animate: true,
      nextBtnText: '下一步', // 下一步按钮标题
      prevBtnText: '上一步' // 上一步按钮标题
    })
    // 定义步骤
    driver.defineSteps([
      {
        element: '#answer-item',
        popover: {
          className: 'first-step-popover-class',
          title: 'HTML模板获取',
          description: '本周实验的HTML模板需先选择最新的实验获取',
          position: 'right'
        }
      },
      {
        element: '#doc-list',
        popover: {
          title: '代码复查',
          description: '提交HTML内容后务必返回此处-点击查看效果阅览',
          position: 'right'
        }
      }
    ])
    let guide = window.localStorage.getItem('web-guide1')
    this.timer = setTimeout(() => {
      // 开始引导
      if (guide == null) {
        guide = {}
        guide.main = 'true'
        window.localStorage.setItem('web-guide1', JSON.stringify(guide))
        driver.start()
      } else if (JSON.parse(guide).main == null) {
        let newGuide = JSON.parse(guide)
        // console.log(newGuide)
        newGuide['main'] = 'true'
        // newGuide.defineProperty('main', 'true')
        window.localStorage.setItem('web-guide1', JSON.stringify(newGuide))
        driver.start()
        if (JSON.parse(guide).js == null) {
          let newGuide = JSON.parse(guide)
          newGuide['js'] = 'true'
          window.localStorage.setItem('web-guide1', JSON.stringify(newGuide))
          this.dialogShaking = true
        }
      } else {
        if (JSON.parse(guide).js == null) {
          let newGuide = JSON.parse(guide)
          newGuide['js'] = 'true'
          window.localStorage.setItem('web-guide1', JSON.stringify(newGuide))
          this.dialogShaking = true
        }
      }
    }, 500)
  },
  destroyed () {
    if (this.timer != null) { clearTimeout(this.timer) }
    this.timer = null
  }
}
</script>

<style scoped>
.container {
  width: 100%;
}

.container>>>.side-bar {
  height: 100%;
}

/* .side-bar>>>.el-menu-item {
  min-width: 0;
} */

/* 弹窗示例 */
.carousel-container {
  width: 85%;
  text-align: center;
  margin: 0 auto;
}

.html-text-box>>>.el-textarea__inner {
  height: 400px;
}

.code-box-container {
  display: flex;
  width: 80%;
  margin: 0 auto;
  justify-content: space-between;
  text-align: left;
}

.code-box {
  width: 45%;
}

.code-full-box {
  width: 90%;
  margin: 0 auto;
}

.item,
.item-js {
  width: 50px;
  height: 50px;
  z-index: 20;
  /* animation: put-aside 1s ;
  animation-fill-mode: forwards;
  transition: all 15s; */
}

.item {
  position: fixed;
  right: 60px;
  bottom: 110px;
}

.item-js {
  position: fixed;
  right: 60px;
  bottom: 190px;
}

.item>>>.el-badge__content,
.item-js>>>.el-badge__content {
  z-index: 20;
}

/* 编辑增加按钮 */
.fix-button-js {
  position: fixed;
  width: 60px;
  height: 60px;
  font-size: 36px;
  right: 50px;
  bottom: 180px;
  z-index: 10;
}

.dialogShaking {
  animation: bounce-down 1s 0.15s linear 20;
}

@keyframes shake {
  10% {
    transform: rotate(15deg);
  }

  20% {
    transform: rotate(-10deg);
  }

  30% {
    transform: rotate(5deg);
  }

  40% {
    transform: rotate(-5deg);
  }

  50%,
  100% {
    transform: rotate(0deg);
  }
}

@keyframes bounce-down {
  25% {
    transform: translateY(-6px);
  }

  50%,
  100% {
    transform: translateY(0);
  }

  75% {
    transform: translateY(6px);
  }
}
</style>
