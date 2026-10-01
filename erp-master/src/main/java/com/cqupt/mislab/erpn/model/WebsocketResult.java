package com.cqupt.mislab.erpn.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * @author whl
 * @date 2020/12/16 - 23:30
 */
@Data
@ToString
public class WebsocketResult<T> {
    @ApiModelProperty("提示信息")
    private String message;

    @ApiModelProperty("1 更新分组;2 更新成员;3 解散分组")
    private Integer type;

    @ApiModelProperty("业务数据")
    private T data;


}
