const Change = (key) => {
  switch (key) {
    case 'depreciationCost':
      return "折旧"
    case 'interestCost':
      return "利息"
    case 'managementCost':
      return "管理费"
    case "operationCost":
      return "经营费"
    case "adCost":
      return "广告"
    case "productCost":
      return "直接成本"
    default:
      return ""
  }
}
export default Change