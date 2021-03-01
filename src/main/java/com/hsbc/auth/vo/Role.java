package com.hsbc.auth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

@Data
@ApiModel(value="role", description="role")
public class Role {
    @ApiModelProperty(value="roleName")
    String roleName;
}
