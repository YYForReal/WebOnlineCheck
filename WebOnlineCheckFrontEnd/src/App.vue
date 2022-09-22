<template>
  <div id="app">
    <Header/>
    <el-main>
      <router-view></router-view>
    </el-main>
  </div>

</template>

<script>
import Header from './components/Header.vue'
export default {
  name: 'App',
  components: {Header},
  mounted () {
    let string = function () {
      /*
                          _    _                     _    _ __     ____     __
             /\          | |  | |                 _ | |  | |\ \   / /\ \   / /
            /  \   _   _ | |_ | |__    ___   _ __(_)| |__| | \ \_/ /  \ \_/ /
           / /\ \ | | | || __|| '_ \  / _ \ | '__|  |  __  |  \   /    \   /
          / ____ \| |_| || |_ | | | || (_) || |   _ | |  | |   | |      | |
         /_/    \_\\__,_| \__||_| |_| \___/ |_|  (_)|_|  |_|   |_|      |_|
      */
    }
    console.log(this.makeMulti(string))
    console.log('大哥~手下留情啊~')
    console.log('别黑我📌~')
  },
  methods: {
    makeMulti (l) {
      l = String(l)
      l = l.substring(l.indexOf('/*') + 3, l.lastIndexOf('*/'))
      return l
    }
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
*{
  padding: 0;
  margin: 0;
}

</style>
