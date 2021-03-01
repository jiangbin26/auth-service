package com.hsbc.auth.mo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Roles implements Serializable {
    private String name;
}
