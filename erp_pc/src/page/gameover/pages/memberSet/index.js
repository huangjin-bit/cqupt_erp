import React, { useState } from 'react'
import { Table, Button, Modal, Form, Input, message } from 'antd'

import { nanoid } from 'nanoid';
import httpUtil from 'utils/request/index'
import './index.scss'

function MemberSet(props) {


    // Table组件内部的Form组件的位置设定
    const layout = {
        labelCol: { span: 5 },
        wrapperCol: { span: 16 }
    }
    const tailLayout = {
        wrapperCol: { offset: 8, span: 16 }
    }


    // 控制Modal弹窗显示
    let [isAddModalVisible, setIsAddModalVisible] = useState(false)
    // 修改时暂存需要修改的成员的数据
    let [nowModifyMember, setNowModifyMember] = useState(false)
    // Table组件渲染的数据源
    let [data, setData] = useState([])

    // 默认数据请求
    // React.useEffect(() => {
    //     httpUtil.getMemberInfo().then(res => {
    //         console.log(res);
    //         if (!res.status) changeOldNo(res.data)
    //     })
    // }, [])
    // 2023喻相鲒修改------------------新增修改排名弹窗
    React.useEffect(() => {
  httpUtil.getMemberInfo().then(res => {
    if (!res.status) {
      // 👇 给每个成员加唯一 id
      const membersWithId = res.data.map(member => ({
        ...member,
        id: nanoid()
      }));
      changeOldNo(membersWithId);
    }
  });
}, []);
// 控制“修改排名”弹窗
const [isRankModalVisible, setIsRankModalVisible] = useState(false);

// 存储每个成员的临时排名输入 { memberId: rank }
const [rankInputs, setRankInputs] = useState({});
// 打开“修改排名”弹窗
const openRankModal = () => {
  // 初始化 rankInputs：以当前顺序作为默认排名
  const initialRanks = {};
  data.forEach((member, index) => {
    initialRanks[member.id] = index + 1;
  });
  setRankInputs(initialRanks);
  setIsRankModalVisible(true);
};

// 提交调整后的排名
const submitRankOrder = () => {
  const ranks = Object.values(rankInputs).filter(v => v !== '');

  // 校验：数量是否匹配
  if (ranks.length !== data.length) {
    message.error('请为所有成员填写排名');
    return;
  }

  // 校验：是否包含 1 到 N 的所有整数
  const expectedRanks = Array.from({ length: data.length }, (_, i) => i + 1);
  const sortedRanks = [...ranks].sort((a, b) => a - b);
  const isComplete = expectedRanks.every((r, i) => r === sortedRanks[i]);

  if (!isComplete) {
    message.error(`排名必须是 1 到 ${data.length} 的不重复整数`);
    return;
  }

  // 按新排名排序
  const newData = [...data].sort((a, b) => {
    return rankInputs[a.id] - rankInputs[b.id];
  });

  // 提交到后端
  httpUtil.submitInfo({ members: newData }).then(res => {
    if (!res.status) {
      setData(newData);
      message.success('排名更新成功');
      
      setIsRankModalVisible(false);
    } else {
      message.error('保存失败');
    }
  });
};

    // 将所有oldStudentNo改为null
    const changeOldNo = (memberData = null) => {
        let arr = memberData || data
        arr.map(item => {
            return item.oldStudentNo = ""
        })
        console.log(arr)
        setData([...arr])
    }

    // 判断学号是否重复
    const judgeNo = (value, oldValue = null) => {
        for (let i = 0; i < data.length; i++) {
            if (data[i].studentNo === value && value != oldValue) return false
        }
        return true
    }

    // 删除成员
    const delectMember = student => {
        Modal.confirm({
            title: '是否确认删除该成员？',
            okText: '确认',
            cancelText: '取消',
            onOk() {
                let newData = data
                newData.splice(newData.indexOf(student), 1)
                httpUtil.submitInfo({ members: newData }).then(res => {
                    if (!res.status) {
                        setData([...newData])
                        message.success('删除成员成功')
                    }
                    else message.error('出错了')
                })
            },
            ontouchcancel() { }
        })
    }


    // 打开Modal弹窗
    const modifyMember = (student, bool) => {
        // 通过bool判断修改或添加
        if (bool === true)
            setNowModifyMember(student)
        setIsAddModalVisible(true)
    }

    // Modal弹窗提交数据
    const onFinish = (values) => {
        //添加
        if (nowModifyMember === false) {
            if (values.point % 1 != 0 || values.point <= 0 || values.point > data.length + 1) {
                message.error('请输入正确的排名！')
                return false
            }
            if (!judgeNo(values.studentNo)) {
                message.error('小组成员的学号不能重复！')
                return
            }
            let newData = data
            newData.splice(values.point - 1, 0, {
                oldStudentNo: "",
                studentNo: values.studentNo,
                studentName: values.studentName,
                title: values.title
            })
            httpUtil.submitInfo({ members: newData }).then(res => {
                if (!res.status) {
                    setData(newData); // 更新本地 data
    message.success('添加成员成功');
    
    // 👇 关键：刷新数据以获取后端实际顺序
     httpUtil.getMemberInfo().then(res => {
    if (!res.status) {
      // 👇 给每个成员加唯一 id
      const membersWithId = res.data.map(member => ({
        ...member,
        id: nanoid()
      }));
      changeOldNo(membersWithId);
    }
  });
                    
                }
                else message.error('出错了')
            })

        }
        //修改
        else {
            let newData = data
            let beforePoint = newData.indexOf(nowModifyMember)

            //判断排名是否改变，如果没有改变将point赋值为之前的排名

            values.point = values.point || beforePoint + 1
            if (values.point % 1 != 0 || values.point <= 0 || values.point > data.length) {
                message.error('请输入正确的排名！')
                return false
            }

            if (values.studentNo && !judgeNo(values.studentNo, nowModifyMember.studentNo)) {
                message.error('小组成员的学号不能重复！')
                return
            }
            //删除需要调整的成员的原数据

            newData.splice(beforePoint, 1)
            // 添加上一步删除成员的调整后的信息
            // 并根据排名插入数组相应的位置
            newData.splice(values.point - 1, 0, {

                //如果defaultValue的信息没有被修改就会传undefined,进行判断是否修改然后赋值
                oldStudentNo: nowModifyMember.studentNo,
                studentNo: values.studentNo ? values.studentNo : nowModifyMember.studentNo,
                studentName: values.studentName ? values.studentName : nowModifyMember.studentName,
                title: values.title ? values.title : nowModifyMember.title
            })
            httpUtil.submitInfo({ members: newData }).then(res => {
                if (!res.status) {
                    message.success('修改成员成功')
                    changeOldNo()
                }
                else message.error('出错了')

            })
        }
        setIsAddModalVisible(false)
        setNowModifyMember(false)
    }

    // Modal点击取消
    const handleCancel = () => {
        setNowModifyMember(false)
        setIsAddModalVisible(false)
    }

    // 提交成员表单信息
    const submitInfo = () => {
        Modal.confirm({
            title: '是否确认提交小组成员信息？',
            okText: '确认',
            cancelText: '取消',
            onOk() {
                message.success('提交成功')
                console.log(data)
            },
            ontouchcancel() { }
        })
    }


    // 表数据模型
    const columns = [
        {
            title: '学号',
            dataIndex: 'studentNo',
            key: 'studentNo',
            // 必填项，若该项数据为空则显示'-'

        },
        {
            title: '姓名',
            dataIndex: 'studentName',
            key: 'studentName'
        },
        {
            title: '职务',
            dataIndex: 'title',
            key: 'title'
        },
        {
            title: '贡献排名',
            dataIndex: 'point',
            key: 'point',
            render: (_, student) => {
                return data.indexOf(student) + 1
            }
        },
        {
            title: '操作',
            key: 'tags',
            dataIndex: 'tags',
            render: (_, student) => {
                return (
                    <span key={nanoid()}>
                        <Button className='table-btn' type='primary' onClick={() => delectMember(student)}>删除</Button>
                        <Button className='table-btn' type='primary' onClick={() => modifyMember(student, true)}>修改</Button>
                    </span>)
            }
        }
    ]





    return (<div className="userprepare-wrapper">
        <span className='member-span'>注意：修改信息后，点击提交成员信息。</span>
        <Table size='small' pagination={false} columns={columns} dataSource={data} rowKey={nanoid()}></Table>
        <p className='member-p'>
            <Button type='primary' onClick={() => modifyMember(null, false)}>添加新成员</Button>
            <Button 
    type='primary' 
    style={{ marginLeft: '10px' }}
    onClick={() => openRankModal()}
  >
    修改排名
  </Button>
        </p>
        <Modal destroyOnClose title={nowModifyMember === false ? "添加新成员" : '修改成员信息'} visible={isAddModalVisible} footer={null} onCancel={handleCancel}>
            <Form onFinish={onFinish} {...layout}>

                <Form.Item label='学号' name='studentNo' rules={[{ required: true}]}>
                    {nowModifyMember === false ? <Input /> : <Input defaultValue={nowModifyMember.studentNo} />}
                </Form.Item>
                <Form.Item label='姓名' name='studentName' rules={[{ required: true}]}>
                    {nowModifyMember === false ? <Input /> : <Input defaultValue={nowModifyMember.studentName} />}
                </Form.Item>
                <Form.Item label='职位' name='title' rules={[{ required: true}]}>
                    {nowModifyMember === false ? <Input /> : <Input defaultValue={nowModifyMember.title} />}
                </Form.Item>
                <Form.Item label='贡献排名' name='point' rules={[{ required: true}]}>
                    {nowModifyMember === false ? <Input /> : <Input defaultValue={data.indexOf(nowModifyMember) + 1} />}
                </Form.Item>
                <Form.Item {...tailLayout}>
                    <Button type="primary" htmlType='submit'>
                        {nowModifyMember === false ? '添加' : '修改'}
                    </Button>
                    <Button style={{ marginLeft: '20px' }} type="primary" onClick={handleCancel}>
                        取消
                    </Button>
                </Form.Item>
            </Form>
        </Modal>
        {/* ===== 修改排名弹窗 ===== */}
<Modal
  title="修改成员排名"
  visible={isRankModalVisible}
  onCancel={() => setIsRankModalVisible(false)}
  footer={null}
  destroyOnClose
>
  <Form layout="vertical">
    {data.map(member => (
      <Form.Item
        key={member.id}
        label={`${member.studentName}（${member.studentNo}）`}
        required
      >
        <Input
          type="number"
          min={1}
          max={data.length}
          value={rankInputs[member.id] || ''}
          onChange={e => {
            const value = e.target.value ? Number(e.target.value) : '';
            setRankInputs(prev => ({
              ...prev,
              [member.id]: value
            }));
          }}
          style={{ width: '100px' }}
        />
      </Form.Item>
    ))}
  </Form>
  <div style={{ textAlign: 'right', marginTop: '16px' }}>
    <Button onClick={() => setIsRankModalVisible(false)}>取消</Button>
    <Button type="primary" style={{ marginLeft: '8px' }} onClick={submitRankOrder}>
      确认调整
    </Button>
  </div>
</Modal>
    </div>)
}

export default MemberSet


