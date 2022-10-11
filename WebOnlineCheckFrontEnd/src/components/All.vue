<template>
    <div class="all">
        <HeaderVue />
        <router-view></router-view>
    </div>
</template>
<script>
import HeaderVue from './Header.vue'
export default {
  name: 'All',
  components: {
    HeaderVue
  }
}
</script>
<style scoped>
.all {
    width: 100%;
    font-family: 'Avenir', Helvetica, Arial, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
}

/* *{
  padding: 0;
  margin: 0;
} */
</style>
