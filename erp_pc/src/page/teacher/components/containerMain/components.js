//自动化工程
//建立上下文关系
const files = require.context('../../views', true, /\.js$/) //第一个参数是目录，第二个查找子级目录，第三个查找指定文件
//声明组件对象
const components = []
//循环文件
files.keys().map(key => {
  //过滤./index/和./login/开头的文件
  if (key.includes('./index/') || key.includes('./login/')) {
    return false
  }
  //拼接path路径
  const splitFilesName = key.split('.')
  const path = `/index${splitFilesName[1].toLowerCase()}`
  //获取组件
  const component = files(key).default
  //写入对象
  const jsonObj = {}
  jsonObj.path = path
  jsonObj.component = component
  components.push(jsonObj)
})

export default components
