package com.hsbc.auth.mo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Token {
    long expiryTime;
    String info;
    Users users;
}
