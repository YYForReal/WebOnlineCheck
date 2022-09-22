# WebOnlineCheckFrontEnd

## 一. 环境的配置
(1)检查是否安装node.js.使用git命令(右击Git Bash Here) 输入node -v、返回版本号既是安装成功

(2)检查本地的npm版本：npm -v
过低的话使用 npm install  -g npm
安装的时候使用镜像
npm install -g cnpm --registry=https://registry.npm.taobao.org

(3)全局安装webpack: npm inatall webpack -g
(4)安装vue脚手架：npm install vue-cli  -g
(5)创建空文件夹，根据模板创建项目

2.打开文件夹中，右键使用命令:vue init webpack demo(工程名必须英文)新建文件就新建完成。
3.写一个登陆界面，在项目中新建page文件夹，dev本地运行，不是自己想要的效果就去配置路由的路径.
4.在文件配置查看vue和路由的版本
缺少哪些组件，在文件根目录下使用命令cnpm install axios --save  cnpm install element-ui --save


## 二. 运行步骤

1. 修改main.js中的baseUrl
2. 修改后端对应的数据库配置
3. npm run dev本地运行
4. npm run buld打包

