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
    console.log('大哥~手下留情啊~别黑我📌')
  }
}
</script>
