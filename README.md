# Web 在线校验网站

## 网站功能

### 已实现

1. 登录：根据学号姓名登录，可以使用特殊的账号切换到教师端路由。
2. 学生端
   1. 可根据实验题目提交html文本（可反复提交）
   2. 可查看自己已提交的实验题目
   3. 比对效果可根据提交时间定时查看
3. 教师端
   1. 可根据实验题目查看学生提交内容
   2. 可查看学生提交内容及实验比对文本的区别
   3. 可批改登记分数
   4. 可查看所有学生，所有实验的分数
   5. 允许根据学生筛选特定学生的提交内容
   6. 允许对实验题目进行增删改
4. 接口校验：提交答案及批改评分时会额外校验当前的登录状态。


### 未实现

1. 注册：未开放
2. 学生端：
   1. 允许在教师端评分过后查看自己的比对情况及评分
   2. 允许查看问题列表
3. 教师端：
   1. 提供一个方便导出的已评分列表的表格
   2. 查看的学生列表可折叠
4. 实验类型增加实验效果图
5. 分页功能


## 比对功能

### 已实现
  1. 过滤掉html标签，保留标签内部文本
  2. 对比文字内容时从body标签开始，防止被css部分干扰。
  3. 展示页面内容时删除script标签及其内部内容，防止脚本注入。
  4. 可以识别部分的转义符号并提前转化为文本符号。


### 未实现
    1. 不能校验通过外部script注入的文字（如document.write），只能校验html文件静态的文字。
    2. 不能校验到空格的个数，文本校验时会删除所有的空格。
    3. 可能出现其他没考虑到的转义符号，后续可以根据情况增加。
    4. CSS校验
    5. JS校验
    6. 高级目标：实现最精确的文本判别并自动评分
    7. 终极目标：实现Web整体效果的比对及自动判别

## 其他部分

### 待实现
   1. 提高接口返回的数据规范
   2. 接口安全性的提高，需增加防御机制（断言，过滤），防SQL注入
   3. 整体UI界面的优化
   4. 数据库字段间的限制关系存在优化空间
