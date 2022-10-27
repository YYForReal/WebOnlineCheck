<template>
  <div id="app">
    <router-view></router-view>
  </div>

</template>

<script>
export default {
  name: 'App',
  mounted () {
    const author = `
                          _    _                     _    _ __     ____     __
             /\\          | |  | |                 _ | |  | |\\ \\   / /\\ \\   / /
            /  \\   _   _ | |_ | |__    ___   _ __(_)| |__| | \\ \\_/ /  \\ \\_/ /
           / /\\ \\ | | | || __|| '_ \\  / _ \\ | '__|  |  __  |  \\   /    \\   /
          / ____ \\| |_| || |_ | | | || (_) || |   _ | |  | |   | |      | |
         /_/    \\_\\\\__,_| \\__||_| |_| \\___/ |_|  (_)|_|  |_|   |_|      |_|`

    console.log(author)
  }
}
</script>

<style>
#app {
  border: 0px !important;
  width:100%;
  height: initial!important;
  margin: initial!important;
  margin-top: initial!important;
  margin-left: initial!important;
  margin-right: initial!important;
  margin-bottom: initial!important;
  padding: initial!important;
  background-color: inherit!important;
  /* min-height: 800px; */
}
</style>
