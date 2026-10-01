import React, { useRef } from 'react'
import { Input, Button, message } from 'antd'
import httpUtil from 'utils/request/index'
import './index.scss'

function FillExperienceReport(props) {
    //判断是否提交过实验报告
    const [content, setContent] = React.useState('')
    //通过ref获取TextArea节点
    let ipt = useRef()
    const { TextArea } = Input;

    // 将字符串转化为HTML形式
    const changeString = (arr) => {
        let strArr = arr.split("\n")
        let stringEnd = ''
        for (let i = 0; i < strArr.length; i++) {
            let newArr = strArr[i].split("")
            for (let j = 0; j < newArr.length; j++) {
                if (!newArr[j].trim()) {
                    newArr[j] = "&nbsp;"
                }
            }
            stringEnd = stringEnd + '<p>' + newArr.join("") + '</p>'
        }

        return stringEnd
    }

    // 将HTML转化为字符串形式
    const changeHtml = (str) => {
        let strData = str
        strData = strData.replace(/<p>/g, '')
        strData = strData.replace(/<\/p>/g, '\n')
        strData = strData.replace(/&nbsp;/g, ' ')
        console.log(strData)
        return strData
    }

    React.useEffect(() => {
        //页面挂载时获取实验心得信息
        httpUtil.getContent().then(res => {
            if (res.data) {
                if (res.data.context) {
                    setContent(changeHtml(res.data.context))
                } else setContent('无内容')
            }
        })

    }, [])

    // 监测文本域值变化
    const textChange = (textValue) => {
        let string = textValue.replace(/</g, '《')
        string = string.replace(/>/g, '》')
        setContent(string)
    }

    //提交实验心得内容
    const summit = () => {
        console.log(ipt.current.resizableTextArea.textArea.value)
        const speedValue = changeString(
          ipt.current.resizableTextArea.textArea.value
        )

        if(speedValue.length < 500) {
            message.warn('心得体会报告字数不应少于500字')
            return
        }

        httpUtil.addContent({ context: speedValue }).then(res => {
            if (!res.status) message.success('提交成功')
            else message.error('提交失败')
        })
    }

    return <div className="userprepare-wrapper">
        <div className='content'>
            <p className='title'>实验心得</p>
            <TextArea ref={ipt} className='text' rows={13} showCount allowClear onChange={e => textChange(e.target.value)} value={content} placeholder='请认真填写实验心得……' />
            <p className='btn'><Button type='primary' onClick={summit}>提交</Button></p>
        </div>
    </div >
}

export default FillExperienceReport
