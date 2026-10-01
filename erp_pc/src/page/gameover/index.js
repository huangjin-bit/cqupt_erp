import React, { Fragment } from 'react'
import { Switch, Route, Link, useHistory } from 'react-router-dom'
import { connect } from 'react-redux'
import routes from './routes'
import './index.scss'
import 'assets/icons/iconfont.css'
import { PageHeader, Layout, Menu, Modal, message } from 'antd'
import { OverIcon } from 'assets/icons/index'
import httpUtil from 'utils/request'

import GameoverHeader from './components/header'
import { nanoid } from 'nanoid'
import { SERVER } from 'config'
import { saveAs } from 'file-saver'
import {
  Document,
  Packer,
  Paragraph,
  Table,
  TableRow,
  TableCell,
  WidthType,
  AlignmentType,
  HeadingLevel
} from 'docx'
const { Header: AntdHeader, Sider, Content } = Layout

const { SubMenu } = Menu
const rootSubmenuKeys = routes.map((item) => item.name)

function GameOver(props) {
  const history = useHistory()
  // antd 只展开当前活动的导航菜单
  const [openKeys, setOpenKeys] = React.useState([''])
  const onOpenChange = (keys) => {
    const latestOpenKey = keys.find((key) => openKeys.indexOf(key) === -1)
    if (rootSubmenuKeys.indexOf(latestOpenKey) === -1)
      setOpenKeys(keys)
    else
      setOpenKeys(latestOpenKey ? [latestOpenKey] : [])

  }

  // 重新开始
  const restart = () => {
    Modal.confirm({
      title: '点击确认后游戏数据将被清空,是否重新开始？',
      okText: '确认',
      cancelText: '取消',
      onOk() {
        httpUtil.restartGame().then(res => {
          console.log(res)
          if (!res.status) {
            localStorage.clear()

            message.success('重新开始成功，请重新登录')
            over()
          }
        })
      },
      onCancel() {
      }
    })
  }

  // 退出
  const over = () => {
    localStorage.clear()
    httpUtil.logout().then(res => {
      if (!res.status)
        history.push(`/login`)

    })
  }

  // 下载实验报告（纯前端生成） ---------------2023级喻相鲒
// const downloanDoc = () => {
//   message.loading('正在生成实验报告...', 0);

//   httpUtil.overViewReport().then(res => {
//     message.destroy(); // 关闭 loading

//     // 处理错误情况
//     if (res.status || !res.data) {
//       message.error(res.data?.message || '获取报告数据失败，请稍后重试');
//       return;
//     }

//     const { code, message: errMsg } = res.data;
//     if (code === '11001' || code === '11000') {
//       message.error(errMsg || '暂无实验报告数据');
//       return;
//     }

//     // 提取数据
//     const tableData = res.data.businesses || [];
//     const content = (res.data.context || '').split('<w:br/>');
//     const group = res.data.group || { teamLeader: '', members: [] };

//     // 开始构建 Word 文档
//     const children = [];

//     // 1. 主标题
//     children.push(
//       new Paragraph({
//         text: 'ERP沙盘模拟运营实验报告',
//         heading: HeadingLevel.HEADING_1,
//         alignment: AlignmentType.CENTER,
//       })
//     );

//     // 2. 小组组长
//     children.push(
//       new Paragraph(`小组组长：${group.teamLeader}`, { heading: HeadingLevel.HEADING_4 })
//     );

//     // 3. 小组成员表格
//     if (group.members && group.members.length > 0) {
//       const memberRows = [
//         new TableRow({
//           children: ['学号', '姓名', '职务', '成绩', '评级'].map(text =>
//             new TableCell({ children: [new Paragraph({ text, bold: true })] })
//           ),
//         }),
//         ...group.members.map(m =>
//           new TableRow({
//             children: [
//               new TableCell({ children: [new Paragraph(m.studentNo || '')] }),
//               new TableCell({ children: [new Paragraph(m.studentName || '')] }),
//               new TableCell({ children: [new Paragraph(m.title || '')] }),
//               new TableCell({ children: [new Paragraph(String(m.score || ''))] }),
//               new TableCell({ children: [new Paragraph(m.grade || '')] }),
//             ],
//           })
//         ),
//       ];
//       children.push(
//         new Paragraph('小组成员', { heading: HeadingLevel.HEADING_4 }),
//         new Table({
//           width: { size: 100, type: WidthType.PERCENTAGE },
//           rows: memberRows,
//         })
//       );
//     }

//     // 4. 各期运营表格
//     tableData.forEach(item => {
//       children.push(
//         new Paragraph(`第${item.year}年，第${item.period}期`, {
//           heading: HeadingLevel.HEADING_2,
//         })
//       );

//       const periodRows = [
//         new TableRow({
//           children: [
//             '资产', '期初数', '期末数', '负债和所有者权益', '期初数', '期末数'
//           ].map(text =>
//             new TableCell({ children: [new Paragraph({ text, bold: true })] })
//           ),
//         }),
//         ...(item.rows || []).map(row =>
//           new TableRow({
//             children: [row.col1, row.col2, row.col3, row.col4, row.col5, row.col6].map(cell =>
//               new TableCell({ children: [new Paragraph(cell || '')] })
//             ),
//           })
//         ),
//       ];

//       children.push(
//         new Table({
//           width: { size: 100, type: WidthType.PERCENTAGE },
//           rows: periodRows,
//         })
//       );
//     });

//     // 5. 实验心得
//     children.push(
//       new Paragraph('实验心得报告', { heading: HeadingLevel.HEADING_2 })
//     );
//     content.forEach(paragraphText => {
//       children.push(new Paragraph(paragraphText || ''));
//     });

//     // 生成并下载
//     const doc = new Document({ sections: [{ children }] });
//     Packer.toBlob(doc)
//       .then(blob => {
//         saveAs(blob, 'ERP沙盘模拟运营实验报告.docx');
//         message.success('实验报告已生成并开始下载！');
//       })
//       .catch(err => {
//         console.error('Word生成失败:', err);
//         message.error('报告生成失败，请重试');
//       });
//   }).catch(err => {
//     message.destroy();
//     console.error('请求报告数据失败:', err);
//     message.error('网络错误，请检查后重试');
//   });
// };
  // const downloanDoc = () => {
  //   httpUtil.downloadDoc().then(res => {
  //     if (!res.status) {
  //       // 地址
  //       const eleLink = document.createElement('a');
  //       eleLink.style.display = 'none';
  //       // 动态地址
  //       eleLink.href = window.location.hash + '/erpn/' + res.data.path
  //       document.body.appendChild(eleLink);
  //       eleLink.click();
  //       document.body.removeChild(eleLink);
  //     }
  //     else if (res.status && res.data != null) message.info(res.data.message)
  //   }
  //   )
  // }
    const downloanDoc = text => {
      // 发送请求
      httpUtil
        .downloadDoc()
        .then(res => {
          console.log(res);
          console.log(typeof res);
          // return res
          return res.blob()
        })
        .then(blob => {
          const link = document.createElement('a')
          if (link.download !== undefined) {
            const url = URL.createObjectURL(blob)
            link.setAttribute('href', url)  
            link.setAttribute('download', `${text.groupName}.doc`)
            link.style.visibility = 'hidden'
            document.body.appendChild(link)
            link.click()
            document.body.removeChild(link)
          }
        })
        .catch(() => {
          message.error('下载文件失败')
        })
    }



  return (
    <div className="game-over-wrapper">
      <Layout>
        {/* <AntdHeader> */}
        <GameoverHeader />
        {/* </AntdHeader> */}
        <Layout>
          <Sider>
            <Menu openKeys={openKeys} onOpenChange={onOpenChange} mode="inline">
              {routes.map((item) => {
                if (item.children !== undefined) {
                  return (
                    <SubMenu icon={item.icon} key={item.name} title={item.name}>
                      {item.children
                        ? item.children.map((_item) => {
                          if (_item.name == '下载实验报告') {
                            return <Menu.Item key={_item.name} onClick={downloanDoc}>
                              {_item.name}
                            </Menu.Item>
                          }
                          return (
                            <Menu.Item key={_item.name}>
                              <Link to={_item.path}>{_item.name}</Link>
                            </Menu.Item>
                          )
                        })
                        : null}
                    </SubMenu>
                  )
                } else {
                  return (
                    <Menu.Item icon={item.icon} key={item.name}>
                      <Link to={item.path}>{item.name}</Link>
                    </Menu.Item>
                  )
                }
              })}
              <Menu.Item key='1' icon={OverIcon} onClick={restart}>重新开始</Menu.Item>
              <Menu.Item key='2' icon={OverIcon} onClick={over}>退出游戏</Menu.Item>
            </Menu>
          </Sider>
          <Content>
            <Switch>
              {routes.map((item) => {
                if (item.children !== undefined) {
                  const icon = item.icon
                  const fatherBreadcrumbName = item.name
                  return item.children.map((item) => {
                    const ItemRoute = item.route
                    return (
                      <Route key={item.path} path={item.path}>
                        <div key={nanoid()} className="nav-header">
                          <PageHeader
                            className="site-page-header"
                            onBack={() => null}
                            title={item.name}
                            backIcon={icon}
                            breadcrumb={{
                              routes: [
                                {
                                  path: item.path,
                                  key: 1,
                                  breadcrumbName: fatherBreadcrumbName
                                },
                                {
                                  path: item.path,
                                  key: 2,
                                  breadcrumbName: item.name
                                }
                              ]
                            }}
                            subTitle={item.Englishname}
                          />
                        </div>
                        <div className="content-wrapper">
                          {<item.route />}
                        </div>
                      </Route>
                    )
                  })
                }
                else {
                  return (
                    <Route key={item.path} path={item.path}>
                      <div key={nanoid()} className="nav-header">
                        <PageHeader
                          className="site-page-header"
                          onBack={() => null}
                          title={item.name}
                          subTitle={item.Englishname}
                          backIcon={item.icon}
                          breadcrumb={{
                            routes: [
                              {
                                path: item.path,
                                key: 1,
                                breadcrumbName: '主页'
                              },
                              {
                                path: item.path,
                                key: 2,
                                breadcrumbName: item.name
                              }
                            ]
                          }}
                        />
                      </div>
                      <div className="content-wrapper"> {<item.route />} </div>
                    </Route>
                  )
                }
              })}
            </Switch>
          </Content>
        </Layout>
      </Layout>
      <br />
    </div>
  )
}

export default GameOver
