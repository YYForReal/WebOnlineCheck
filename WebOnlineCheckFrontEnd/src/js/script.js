/**
 * 文本校验
 * 实现：
 * 1. 可以无视html标签的干扰
 * 2. 可以识别部分的转义符号
 * 3. 共校验3次，第一次校验是否遗漏，第二次校验是否多余，第三次借助diff.js直接比对。
 * 限制：（待解决）
 * 1. 不能校验外部script注入的文字，只能校验html文件静态的文字。
 * 2. 不能校验到空格的个数，文本校验时会删除所有的空格。
 *    如果保留空格的话，可能会因为html文件内换行显示，导致分析html文件时空格数量增多。
 * 3. 可能出现其他没考虑到的转义符号，后续可以根据情况增加。
 * 4. 个人实现：不能检测出遗漏字符的位置，若字符遗漏，则后续内容都会提示出错。
 */

// 配置需提前替换html文本内的转义符号
const replaceTags = [
  ['&lt;', '<'],
  ['&gt;', '>'],
  ['&#39;', '\''],
  ['&quot;', '"'],
  ['&amp;', '&'],
  ['&nbsp;', ' '],
  ['&copy;', '©'],
  // [' ',''], //注释该行，则表示保留空格
  ['\r', ''],
  ['\n', '']
  // [' ','']
]

let sourceTxt = null
let resultTxt = null
var inputs = document.querySelectorAll('input')
var resultText = document.getElementById('resultText')

var a = document.getElementById('a')
var b = document.getElementById('b')
var result = document.getElementById('result')

var iframe = document.getElementById('iframe')

// 解析比对的文本文件txt
inputs[0].onchange = function () {
  // 开始读取
  var file = this.files[0]
  var reader = new FileReader()
  reader.readAsText(file)
  resultText.innerText = ''
  // 文件读取完成后，获取读取的结果
  reader.onload = function () {
    sourceTxt = reader.result.split('')
    // 过滤 回车符号 和 换行符号
    sourceTxt = sourceTxt.filter((char) => {
      return char !== '\r' && char !== '\n'
    })
    resultText.innerText = '读取txt文本成功\r\n'
    a.textContent = sourceTxt.join('')
    if (resultTxt !== null) {
      checkFirst()
      resultText.innerText += '---------------- \r\n'
      checkSecond()
      changed()
    }
  }
}

// 解析比对的html文件
inputs[1].onchange = function () {
  // 开始读取
  var file = this.files[0]
  var reader = new FileReader()
  reader.readAsText(file)
  resultText.innerText = ''
  // 文件读取完成后，获取读取的结果
  reader.onload = function () {
    resultTxt = reader.result

    iframe.src = resultTxt

    var iwindow = iframe.contentWindow // 获取iframe的window对象
    var idoc = iwindow.document // 获取iframe的document对象
    idoc.open()
    idoc.write(resultTxt)
    idoc.close()

    // 取body标签后的内容
    let index = resultTxt.indexOf('body')
    if (index !== -1) {
      resultTxt = resultTxt.slice(index)
    }

    resultText.innerText = '读取html文件成功\r\n'
    // 去除标签的影响
    resultTxt = delHtmlTag(resultTxt)

    b.textContent = resultTxt
    if (sourceTxt !== null) {
      checkFirst()
      resultText.innerText += '---------------- \r\n'
      checkSecond()
      changed()
    }
  }
}

/**
 * 第一次检测
 * 检测思路：类似 双指针 的形式，遍历比对文本的同时扫描html文件内容是否遗漏。
 * 作用：能够确保html文件内容不少于比对文本内容，且顺序相同。
 * 可能问题：需要确保html文件内字符顺序和比对文本顺序相同。
 */
function checkFirst () {
  let nowIndex = 0 // 比对txt字符的下标
  let searchIndex = 0// 检查html文件的下标
  let isError = false

  while (nowIndex < sourceTxt.length) {
    let checkText = sourceTxt[nowIndex]
    let searchResult = resultTxt.indexOf(checkText, searchIndex)
    if (searchResult === -1) {
      resultText.innerText += `\r\n检查第${nowIndex}个字符失败： ${checkText} \r\n`
      isError = true
    } else {
      searchIndex = searchResult
      // console.log(`检查第${nowIndex}个字符成功： ${checkText} html文件位置为：${searchIndex}\r\n`)
    }
    nowIndex++
  }
  if (isError) {
    resultText.innerText += '第一次文字校验失败：\r\n可能原因：字符错误、遗漏字符 或 字符顺序与比对文本文件不匹配。\r\n'
    return false
  } else {
    resultText.innerText += '第一次文字校验成功\r\n'
    return true
  }
}

/**
 * 第二次检测
 * 检测思路：删除所有html标签，直接比对html文件内剩余文本
 * 作用：防止html文件内容多于文本内容
 * 可能问题：容易产生误判
 */
function checkSecond () {
  let htmlText = delHtmlTag(resultTxt)
  let compText = delHtmlTag(sourceTxt.join(''))

  if (htmlText.indexOf(compText) === -1) {
    resultText.innerText += '第二次文字校验失败：\r\n可能原因：字符错误、遗漏字符、字符顺序与比对文本文件不匹配、html存在多余内容\r\n'
    return false
  } else {
    resultText.innerText += '第二次文字校验成功\r\n'
    return true
  }
}

/**
 * 去除所有的html干扰内容
 */
function delHtmlTag (str) {
  // 去除所有箭头
  str = str.replace(/<[^>]+>/g, '')
  // 替换所有的转义符号
  replaceTags.forEach(element => {
    str = str.replace(new RegExp(element[0], 'gm'), element[1])
  })
  str = str.split('')
  // 过滤 回车符号 和 换行符号
  str = str.filter((char) => {
    return char !== '\r' && char !== '\n' && char !== ' '
  })
  str = str.join('')
  // 去除所有空格
  return str
}

// 引入并使用其他的辅助代码

function changed () {
  var oldContent = a.textContent
  var content1 = b.textContent
  // eslint-disable-next-line no-undef
  var diff = JsDiff['diffChars'](oldContent, content1)
  var arr = []
  for (var i = 0; i < diff.length; i++) {
    if (diff[i].added && diff[i + 1] && diff[i + 1].removed) {
      var swap = diff[i]
      diff[i] = diff[i + 1]
      diff[i + 1] = swap
    }
    var diffObj = diff[i]
    var content = diffObj.value

    if (diffObj.removed) {
      arr.push('<del title="删除的部分">' + content + '</del>')
    } else if (diffObj.added) {
      arr.push('<ins title="新增的部分">' + content + '</ins>')
    } else {
      // 没有改动的部分
      arr.push('<span title="没有改动的部分">' + content + '</span>')
    }
  }
  var html = arr.join('')
  result.innerHTML = html
}
