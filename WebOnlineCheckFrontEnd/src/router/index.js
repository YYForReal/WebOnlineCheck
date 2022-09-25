import Vue from 'vue'
import Router from 'vue-router'

import Main from '@/components/Main.vue'
import Login from '@/components/Login.vue'
/* 懒加载方式，当路由被访问的时候才加载对应的组件 */
const Answer = resolve => require(['@/components/student/Answer'], resolve)
const Home = () => import('@/components/teacher/Home')
const QuestionList = () => import('@/components/student/QuestionList')
// const Judge = () => import('@/components/teacher/Judge')
// const ScoreList = () => import('@/components/teacher/ScoreList')

Vue.use(Router)// 注册vue-router

export default new Router({
  base: '/webcheck/',
  routes: [
    {
      path: '/',
      name: 'Main',
      component: Main,
      redirect: '/answer',
      children: [
        {
          path: '/answer',
          name: 'Answer',
          component: Answer
        }, {
          path: '/list',
          name: 'QuestionList',
          component: QuestionList

        }
      ]
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/teacher/home',
      name: 'Home',
      component: Home
      // children: [
      //   {
      //     path: 'scorelist',
      //     name: 'ScoreList',
      //     component: ScoreList
      //   },
      //   {
      //     path: 'judge',
      //     name: 'Judge',
      //     component: Judge
      //   }
      // ]
    }

  ]
})
