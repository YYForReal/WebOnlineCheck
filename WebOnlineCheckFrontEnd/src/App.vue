<template>
  <div id="app">
    <Header />
    <router-view></router-view>
  </div>
</template>

<script>
import Header from './components/Header.vue'
export default {
  name: 'App',
  components: { Header },
  mounted () {
    const author = `
                          _    _                     _    _ __     ____     __
             /\\          | |  | |                 _ | |  | |\\ \\   / /\\ \\   / /
            /  \\   _   _ | |_ | |__    ___   _ __(_)| |__| | \\ \\_/ /  \\ \\_/ /
           / /\\ \\ | | | || __|| '_ \\  / _ \\ | '__|  |  __  |  \\   /    \\   /
          / ____ \\| |_| || |_ | | | || (_) || |   _ | |  | |   | |      | |
         /_/    \\_\\\\__,_| \\__||_| |_| \\___/ |_|  (_)|_|  |_|   |_|      |_|`

    console.log(author)
    console.log('大哥~手下留情啊~别黑我📌')
  }
}
</script>

<style>
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

* {
  padding: 0;
  margin: 0;
}
</style>
