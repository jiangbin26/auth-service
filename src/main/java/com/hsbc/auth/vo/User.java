package com.hsbc.auth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@ApiModel(value="user", description="user")
public class User {
    @ApiModelProperty(value="userName")
    private String userName;
}
