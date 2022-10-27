// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'// 引入路由配置
import Vuex from 'vuex'// 导入仓库配置
// import ElementUI from 'element-ui'
// import ELEMENT from 'element-ui'
// import 'element-ui/lib/theme-chalk/index.css'
import store from './store/store'
import axios from 'axios'
// import md5 from 'js-md5'

// Vue.prototype.$md5 = md5
Vue.prototype.axios = axios
// Vue.prototype.baseUrl = 'http://localhost:5520'
Vue.prototype.baseUrl = 'http://118.31.165.150:5520'
Vue.config.productionTip = false
// Vue.use(ElementUI)
// Vue.use(ELEMENT) // 注意大写

Vue.use(Vuex)
Vue.config.productionTip = false
/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>'
})
