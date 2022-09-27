export const MyFilter = {
  dateFormat: (time) => {
    //   var time = '2022-03-16T16:00:00.000Z'
    time = time.replace(/-/, '年')
    time = time.replace(/-/, '月')
    time = time.replace(/T/, '日')
    let times = time.split('.')
    return times[0]
  }
}
