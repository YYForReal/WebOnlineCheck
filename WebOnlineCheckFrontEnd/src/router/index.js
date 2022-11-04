import Vue from 'vue'
import Router from 'vue-router'
import All from '@/components/All.vue'
import Main from '@/components/Main.vue'

/* 懒加载方式，当路由被访问的时候才加载对应的组件 */
const Login = () => import('@/components/Login.vue')
const Answer = resolve => require(['@/components/student/Answer'], resolve)
const Home = () => import('@/components/teacher/Home')
const QuestionList = () => import('@/components/student/QuestionList')
const JudgeTable = () => import('@/components/teacher/JudgeTable')
const ScoreList = () => import('@/components/teacher/ScoreList')
const QuestionSetting = () => import('@/components/teacher/QuestionSetting')
const TemplateHtml = () => import('@/components/student/TemplateHtml')
const ImgList = () => import('@/components/student/ImgList')
const ImgSetting = () => import('@/components/teacher/ImgSetting')
const CodeSetting = () => import('@/components/teacher/CodeSetting')

Vue.use(Router)// 注册vue-router

export default new Router({
  base: '/webcheck/',
  routes: [
    {
      path: '/',
      name: 'All',
      component: All,
      redirect: Main,
      children: [
        {
          path: '/',
          name: 'Main',
          component: Main,
          redirect: '/list',
          children: [
            {
              path: '/answer',
              name: 'Answer',
              component: Answer
            }, {
              path: '/list',
              name: 'QuestionList',
              component: QuestionList
            }, {
              path: '/imgs',
              name: 'ImgList',
              component: ImgList
            }
          ]
        },
        {
          path: '/login',
          name: 'Login',
          component: Login
        },
        {
          path: '/teacher',
          name: 'Home',
          component: Home,
          redirect: '/teacher/home',
          children: [
            {
              path: 'list',
              name: 'ScoreList',
              component: ScoreList
            },
            {
              path: 'home',
              name: 'JudgeTable',
              component: JudgeTable
            }, {
              path: 'setting',
              name: 'QuestionSetting',
              component: QuestionSetting
            }, {
              path: 'img-setting',
              name: 'ImgSetting',
              component: ImgSetting
            }, {
              path: 'code-setting',
              name: 'CodeSetting',
              component: CodeSetting
            }
          ]
        }
      ]
    },
    {
      path: '/template',
      name: 'tempalte',
      component: TemplateHtml
    }
  ]
})
