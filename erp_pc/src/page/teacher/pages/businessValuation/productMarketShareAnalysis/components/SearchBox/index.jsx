/* 
  这里是封装的头部搜索模块
*/

import React from 'react'
import { Select, Input, Space } from 'antd';

const { Option } = Select;
const { Search } = Input;


export function SearchBox(props) {
  const funObj = { ...props.fun }
  const { tea, stu } = props
  return (
    <div>
      <Space>
        <Select defaultValue={funObj.curListTag} style={{ width: 140 }} onChange={funObj.onSelectChange}>
          <Option value={stu}>学生自建</Option>
          <Option value={tea}>老师创建</Option>
        </Select>
        <Search
          placeholder="搜索比赛名"
          onSearch={funObj.onSearch}
          onChange={funObj.onSearchChange}
          enterButton
          value={funObj.searchContent}
        />
      </Space>
    </div>
  )
}