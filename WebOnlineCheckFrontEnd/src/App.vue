<template>
  <div id="app">
    <router-view></router-view>
  </div>

</template>

<script>
export default {
  name: 'App',
  mounted () {
    // eslint-disable-next-line no-unused-vars
    const author = `
                          _    _                     _    _ __     ____     __
             /\\          | |  | |                 _ | |  | |\\ \\   / /\\ \\   / /
            /  \\   _   _ | |_ | |__    ___   _ __(_)| |__| | \\ \\_/ /  \\ \\_/ /
           / /\\ \\ | | | || __|| '_ \\  / _ \\ | '__|  |  __  |  \\   /    \\   /
          / ____ \\| |_| || |_ | | | || (_) || |   _ | |  | |   | |      | |
         /_/    \\_\\\\__,_| \\__||_| |_| \\___/ |_|  (_)|_|  |_|   |_|      |_|`

    // console.log(author)
  }
}
</script>

<style>
#app {
  border: 0px !important;
  /* width:100%; */
  width: inherit !important;

  height: initial !important;
  margin: initial !important;
  margin-top: initial !important;
  margin-left: initial !important;
  margin-right: initial !important;
  margin-bottom: initial !important;
  padding: initial !important;
  background-color: inherit !important;
  /* min-height: 800px; */
  background: inherit !important;
  animation: none !important;
  -moz-animation: none !important;
  -webkit-animation: none !important;
  -o-animation: none !important;
  position: inherit;
  color: inherit !important;
  text-align: inherit !important;
  /* 一键哀悼 */
  /* filter: grayscale(100%); */
}

/* 通用动画 */
@keyframes put-aside{
  0% {
    transform: scale(1);
  }

  20%{
    transform: scale(1.5);
  }

  40%{
    transform: scale(0.7);
  }

  60%{
    transform: scale(1.2);
  }

  80%{
    transform: scale(0.9);
  }

  100% {
    right: 0px;
    /* transform: scale(1) translateX(70px); */
    /* background: linear-gradient(0deg, rgba(0, 3, 255, 1) 0%, rgba(2, 126, 251, 1) 100%); */
  }
}

/* 右下角编辑增加按钮 */
.fix-button {
  position: fixed;
  width: 60px;
  height: 60px;
  font-size: 36px;
  right: 50px;
  bottom: 100px;
  z-index: 10;
  transition: all 1s;
}

/* 代码框区域 */
.code-box pre {
  border-radius: .5em;
  border: .3em solid hsl(0, 0%, 33%);
  box-shadow: 1px 1px 0.5em black inset;
  margin: .5em 0;
  overflow: auto;
  padding: 1em;
  background: hsl(0, 0%, 8%);
  max-height: 560px;
}

.code-box pre,
.code-box code,
.code-box tt {
  font-size: 12px;
  font-family: Consolas, "Liberation Mono", Courier, monospace;
}

.code-box code,
.code-box tt {
  margin: 0 0px;
  padding: 0px 0px;
  border: 1px solid #eaeaea;
  background-color: #f8f8f8;
  border-radius: 3px;
}

.code-box pre>code {
  margin: 0;
  padding: 0;
  white-space: pre;
  color: rgba(10, 223, 24, 0.86);
  font-size: 16px;
  border: none;
  background: transparent;

}

.code-box pre code,
.code-box pre tt {
  background-color: transparent;
  border: none;
}
</style>
