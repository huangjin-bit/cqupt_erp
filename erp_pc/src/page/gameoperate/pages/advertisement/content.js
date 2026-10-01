import React, { useState, useEffect,useContext,useRef } from "react";
import { Table, Tag, Space, Select, Button, Tooltip, Input, Popconfirm, Form } from "antd";
import { SearchOutlined } from "@ant-design/icons";
import { Link } from 'react-router-dom'
import { FormInstance } from 'antd/lib/form';
import pic_right from 'assets/icons/pic_right.jpg'
import httpUtil from 'utils/request/index'
import { color } from "echarts";
import HttpUtil from "utils/request/http";
const { Option } = Select;
const EditableContext = React.createContext(null);
function Content(props) {
  const [adlist,setAdlist] = useState([])
  const [marketlist,setMarketlist] = useState([])
  useEffect(()=>{
    httpUtil.getAlreadyAd().then((res)=>{             //获取已经投放的广告列表
      setAdlist(adlist.concat(res.data))
      console.log('已投放广告列表:',adlist)
    })
    httpUtil.getMarketList().then((res)=>{
      setMarketlist(marketlist.concat(res.data))          //获取已经开拓的市场
      console.log('已经开拓的市场:',marketlist)
    })
  },[])
    console.log(props)
    const EditableRow = ({ index, ...props }) => {
        const [form] = Form.useForm();
        return (
          <Form form={form} component={false}>
            <EditableContext.Provider value={form}>
              <tr {...props} />
            </EditableContext.Provider>
          </Form>
        );
      };
      
      const EditableCell = ({     //21-152都是copy的antd design的UI，但是数据有问题，可以根据需求选择删除或保留
        title,
        editable,
        children,
        dataIndex,
        record,
        handleSave,
        ...restProps
      }) => {
        const [editing, setEditing] = useState(false);
        const inputRef = useRef(null);
        const form = useContext(EditableContext);
        useEffect(() => {
          if (editing) {
            inputRef.current.focus();
          }
        }, [editing]);
      
        const toggleEdit = () => {
          setEditing(!editing);
          form.setFieldsValue({
            [dataIndex]: record[dataIndex],
          });
        };
      
        const save = async () => {
          try {
            const values = await form.validateFields();
            toggleEdit();
            handleSave({ ...record, ...values });
          } catch (errInfo) {
            console.log('Save failed:', errInfo);
          }
        };
      
        let childNode = children;
      
        if (editable) {
          childNode = editing ? (
            <Form.Item
              style={{
                margin: 0,
              }}
              name={dataIndex}
              rules={[
                {
                  required: true,
                  message: `${title} is required.`,
                },
              ]}
            >
              <Input ref={inputRef} onPressEnter={save} onBlur={save} />
            </Form.Item>
          ) : (
            <div
              className="editable-cell-value-wrap"
              style={{
                paddingRight: 24,
              }}
              onClick={toggleEdit}
            >
              {children}
            </div>
          );
        }
      
        return <td {...restProps}>{childNode}</td>;
      };
      
      class EditableTable extends React.Component {     //这里是copy的antd design，其实没什么用，看情况可以删除
        constructor(props) {
          super(props);
          this.columns = [
            {
              title: '编号',
              dataIndex: '编号',
              width: '30%',
            },
            {
              title: '产品',
              dataIndex: '产品',
            },
            {
              title: '投放广告 (单位:万)',
              dataIndex: '投放广告',
              editable: true,
            },
            {
              title: '操作',
              dataIndex: '操作',
              render: (_, record) =>
                this.state.dataSource.length >= 1 ? (
                  <Popconfirm title="确认提交?" onConfirm={() => this.handleDelete(record.key)}>
                    <a>提交</a>
                  </Popconfirm>
                ) : null,
            },
          ];
          this.state = {
            dataSource: [
              {
                key: '1',
                编号: '16303',
                产品: 'P1',
                投放广告: '10',
              },
              {
                key: '2',
                编号: '16383',
                产品: 'P2',
                投放广告: '5',
              },
              {
                key: '3',
                编号: '16203',
                产品: 'P3',
                投放广告: '3',
              },
              {
                key: '4',
                编号: '16243',
                产品: 'P4',
                投放广告: '6',
              },
            ],
            count: 4
          };
        }
      
        handleDelete = (key) => {
          httpUtil.orderAd({adId:'16761',money:10}).then((res)=>{      //确认投放广告:
            console.log('投放广告(未确认):',res)
          })
          const dataSource = [...this.state.dataSource];
          this.setState({
            dataSource: dataSource.filter((item) => item.key !== key),
          });
        };
        handleSave = (row) => {
          const newData = [...this.state.dataSource];
          const index = newData.findIndex((item) => row.key === item.key);
          const item = newData[index];
          newData.splice(index, 1, { ...item, ...row });
          this.setState({
            dataSource: newData,
          });
        };
      
        render() {
          const { dataSource } = this.state;
          const components = {
            body: {
              row: EditableRow,
              cell: EditableCell,
            },
          };
          const columns = this.columns.map((col) => {
            if (!col.editable) {
              return col;
            }
      
            return {
              ...col,
              onCell: (record) => ({
                record,
                editable: col.editable,
                dataIndex: col.dataIndex,
                title: col.title,
                handleSave: this.handleSave,
              }),
            };
          });
          return (
            <div>
              <Table
                components={components}
                rowClassName={() => 'editable-row'}
                bordered
                dataSource={dataSource}
                columns={columns}
              />
            </div>
          );
        }
      }
  let market='本地市场'
  function handleChange(value) {
    market = value;
  }
  function onChange(pagination, filters, sorter, extra) {
    console.log("params", pagination, filters, sorter, extra);
  }
  function createLabel(item) {
    return (
      <p className="ads_left">
        {item}
        <span>{">>"}</span>
      </p>
    );
  }
  function toufang() {
    function getProduct(){
      httpUtil.getProductByMarket({marketName:market}).then((res)=>{           //获取市场下的产品列表,这里200，但是message：失败
        console.log('获取市场下的产品列表(本地市场)',res)
        // res.data是一个数组
      })
    }
    return (
      <div>
        <div className="nav">
          {createLabel("广告投放")}
          <div>
            <div className="container">
              请选择市场:
              <Select
                defaultValue="本地市场"
                style={{ width: 150, margin: "0 10px" }}
                onChange={handleChange}
              >
                <Option value="本地市场">本地市场</Option>
                <Option value="国外市场">国外市场</Option>
                <Option value="其他市场">其他市场</Option>
              </Select>
              <Button
                icon={<SearchOutlined />}
                style={{ backgroundColor: "hsl(230deg 6% 82%)" }}
                onClick={getProduct}
              >
                查询
              </Button>
              <Button
              onClick={()=>props.changeCurrent(1)}
                style={{ backgroundColor: "rgb(32 67 104)" ,color:"white",float:"right"}}
              >
                  已投放完毕, 下一步 {'>'}
                  </Button>
                  <div>
                      <EditableTable/>
                  </div>
            </div>
          </div>
          <div className="main_body"></div>
        </div>
      </div>
    );
  }
  function confirmAdver() {       //获取真实数据后更新
    function confirmAd(){
      httpUtil.confirmOrder().then((res)=>{         //确认投放广告
        console.log('确认投放',res)
        props.changeCurrent(2)
      })
    }
    function updateMoney(){
      httpUtil.backUpdate().then((res)=>{
        console.log('返回修改注入资金',res)       //返回修改注入资金
        props.changeCurrent(0)
      })
    }
    const dataSource = [
      {
        key: '1',
        adId: '16303',
        product: 'P1',
        market:'本地市场',
        money: '1',
      },
      {
        key: '2',
        adId: '16353',
        product: 'P2',
        market:'本地市场',
        money: '3',
      },
      {
        key: '3',
        adId: '16143',
        product: 'P1',
        market:'本地市场',
        money: '4',
      },
      {
        key: '4',
        adId: '16356',
        product: 'P3',
        market:'本地市场',
        money: '5',
      },
      ];
      
      const columns = [
        {
          title: '编号',
          dataIndex: 'adId',
          key: 'adId',
          width:'20%'
        },
        {
          title: '市场',
          dataIndex: 'market',
          key: 'market',
          width:'20%'
        },
        {
          title: '产品',
          dataIndex: 'product',
          key: 'product',
          width:'20%'
        },
        {
          title: '广告费 (单位: 万)',
          dataIndex: 'money',
          key: 'money',
          width:'20%',

        },
      ];
    return <div>
        <div className="nav">
            {createLabel("广告投放确认")}
            </div>
            <div className="container">
            <Button
            onClick={()=>updateMoney()}
                style={{ backgroundColor: "#B6B7BC" ,color:"white"}}
              >{'<'}  返回修改广告费</Button>
                  <Button
                  onClick={()=> confirmAd()}
                style={{ backgroundColor: "rgb(32 67 104)" ,color:"white",float:"right"}}
              >确认订单请按下一步 {'>'}</Button>
              <Table dataSource={dataSource} columns={columns} />;
            </div>
        </div>
  }
  function waitHall() {
    return <div>
      {createLabel("等待大厅")}
      <div>
        等待全部投放后自动跳转
      </div>
    </div>;
  }
  function selectOrder() {
    const orderMessage=[{
      订单编号:123456,
      市场:'本地市场',
      总计金额:28,
      产品:'P1',
      交货日期:9,
      数量:5,
      贷款日期:6,
      单价:7,
      罚金率:0.05,
      特别说明:'无'
    },
    {
      订单编号:123456,
      市场:'本地市场',
      总计金额:28,
      产品:'P1',
      交货日期:9,
      数量:5,
      贷款日期:6,
      单价:7,
      罚金率:0.05,
      特别说明:'无'
    },
    {
      订单编号:123456,
      市场:'本地市场',
      总计金额:28,
      产品:'P1',
      交货日期:9,
      数量:5,
      贷款日期:6,
      单价:7,
      罚金率:0.05,
      特别说明:'无'
    },
    {
      订单编号:123456,
      市场:'本地市场',
      总计金额:28,
      产品:'P1',
      交货日期:9,
      数量:5,
      贷款日期:6,
      单价:7,
      罚金率:0.05,
      特别说明:'无'
    }]
    return <div>
    <div className="ads_left">
        <div style={{float:"left"}}>订单选取 {'>>'}</div>
        <div style={{float:"right"}}><button className="order-button">查看所有订单信息</button></div>
        <div style={{float:"right"}}><button onClick={()=>props.changeCurrent(4)} className="order-button">结束选单</button></div>
        
    </div>
    <br/>
    请选择市场:
    <Select
                defaultValue="本地市场"
                style={{ width: 150, margin: "0 10px" }}
                onChange={handleChange}
              >
                <Option value="本地市场">本地市场</Option>
                <Option value="国外市场">国外市场</Option>
                <Option value="其他市场">其他市场</Option>
              </Select>
    请选择产品:
    <Select
                defaultValue="本地市场"
                style={{ width: 150, margin: "0 10px" }}
                onChange={handleChange}
              >
                <Option value="本地市场">本地市场</Option>
                <Option value="国外市场">国外市场</Option>
                <Option value="其他市场">其他市场</Option>
              </Select>
              <Button
                icon={<SearchOutlined />}
                style={{ backgroundColor: "hsl(230deg 6% 82%)" }}
              >
                查询
              </Button>
      <div style={{display:"flex",width:"65vw",flexWrap:"wrap"}}>
            {orderMessage.map((item,index)=>{
                return(
                  <div className="order-message">
                    <div className="order-detial">
                      <div className="order-left">订单编号:  {item.订单编号} <br/>  市场:  {item.市场}  <br/>产品:  {item.产品} <br/>数量:  {item.数量}<br/> 单价:  {item.单价} <br/>特别说明:  无</div>
                      <div  className="order-right"><button style={{backgroundColor:"rgb(50, 50, 115)",color:'white'}}>选择订单</button><br/>总计金额:  {item.总计金额} <br/>交货日期:  {item.交货日期} <br/>贷款日期:  {item.贷款日期} <br/>罚金率:  {item.罚金率}</div>
                    </div>
                  </div>
                )
            })
            }
      </div>
    </div>
  }
  function finishOrder() {
    return (
    <div>{createLabel("结束选订单")}
      <div className="finish">
        <div><img src={pic_right}></img></div>
        
      你已成功选取订单！<br/>
      查看订单信息，请进入 <Link>销售管理{'>'}查看订单！</Link>
      </div>
    </div>
    )
  }
  function showContent(param) {       //根据index.js传入的props.current的不同展示不同的路由界面
    let content;
    switch (
      param //通过current参数来匹配不同的路由窗口
    ) {
      case 0:
        content = toufang();      //每个路由界面对应一个函数返回react对象
        break;
      case 1:
        content = confirmAdver();
        break;
      case 2:
        content = waitHall();
        break;
      case 3:
        content = selectOrder();
        break;
      case 4:
        content = finishOrder();
    }
    return content;
  }
  return showContent(props.current);    //返回具体的界面
}

export default Content;
