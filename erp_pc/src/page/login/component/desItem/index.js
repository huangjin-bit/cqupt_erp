import React from 'react'
import './index.scss'

import pic_user from 'assets/images/pic_user.png'
import pic_accurate from 'assets/images/pic_accurate.png'
import pic_fast from 'assets/images/pic_fast.png'

const descList = [
  {
    id: 1,
    picUrl: pic_user,
    title: '竞争',
    desc1: '以游戏组为比赛单位',
    desc2: '在竞争中增强实力',
  },
  {
    id: 2,
    picUrl: pic_accurate,
    title: '全面',
    desc1: '全方位展现企业管理',
    desc2: '全面覆盖运作流程',
  },
  {
    id: 3,
    picUrl: pic_fast,
    title: '有趣',
    desc1: '置身模拟现实的复杂环境',
    desc2: '寓教与乐，新鲜有趣',
  },
]

function DescItem() {
  return (
    <div className="desc-wrapper">
      {descList.map(item => (
        <div className="desc-item" key={item.id}>
          <img src={item.picUrl} alt="pic"></img>
          <p className="title">{item.title}</p>
          <div className="desc">
            {item.desc1}
            <br />
            {item.desc2}
          </div>
        </div>
      ))}
    </div>
  )
}

export default DescItem
