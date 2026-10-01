const Change = key => {
  switch (key) {
    case 'cost':
      return '成本'
    case 'price':
      return '价钱'
    case 'sale':
      return '销售'
    default:
      return ''
  }
}
export default Change
