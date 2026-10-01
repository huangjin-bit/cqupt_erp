const escapeCsv = value => {
  const text = value === undefined || value === null ? '' : String(value)
  return /[",\r\n]/.test(text) ? `"${text.replace(/"/g, '""')}"` : text
}

export const downloadImportResultCsv = (rows, fileName = '用户批量导入结果.csv') => {
  const header = ['行号', '姓名', '学号', '用户ID', '导入状态', '失败原因']
  const lines = [header, ...rows.map(row => [
    row.rowNumber,
    row.userName,
    row.studentId,
    row.userId,
    row.status,
    row.reason
  ])]
  const csv = '\uFEFF' + lines.map(line => line.map(escapeCsv).join(',')).join('\r\n')
  const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  link.style.visibility = 'hidden'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}
