package com.hyy.webcheck.bean;

import lombok.Data;

@Data
public class UserToken {
        private String userId;
        private String userName;
        private Integer type;//用户类型
}
