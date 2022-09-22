import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)
export default new Vuex.Store({
  state: {
    account: undefined,
    isTeacher: false
  },
  mutations: {
    /** 学生登录 */
    login (state, payload) {
      state.account = payload
    },
    logout (state) {
      state.account = undefined
    },
    /** 老师登录 */
    teacherLogin (state, payload) {
      state.isTeacher = true
      state.account = payload
    },
    teacherLogout (state) {
      state.isTeacher = false
      state.account = undefined
    }
  },
  actions: {
    handleLogin (context, payload) {
      context.commit('login', payload)
    },
    logout (context) {
      context.commit('logout')
    },
    handleTeacherLogin (context, payload) {
      context.commit('teacherLogin', payload)
    },
    teacherLogout (context) {
      context.commit('teacherLogout')
    }
  }
})
