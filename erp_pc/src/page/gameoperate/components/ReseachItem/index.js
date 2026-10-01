import React from 'react'
import './index.scss'
import { Button } from 'antd'

export default function ResearchItem(props) {
    const { img, productName, handleClick, ...productItem } = props.product_msg;
    console.log(props)
    return (
        <div className="product-item">
            <div><img src={img} /></div>
            <div className="product-detail">
                {
                    Object.keys(productItem).map(key => {
                        return (
                            <div className="product-item-msg" key={productName + productItem[key].title}>
                                <span className="msg-title">{productItem[key].title}</span>
                                <span className="msg-value">{productItem[key].value}</span>
                            </div>
                        )
                    })
                }
            </div>
            <Button style={{ height: '100%' }} type='primary' onClick={() => { handleClick.clickEvent(productName) }} >
                {handleClick.value}
            </Button>
        </div>
    )
}
