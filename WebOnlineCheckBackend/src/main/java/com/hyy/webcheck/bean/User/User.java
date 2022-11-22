package com.hyy.webcheck.bean.User;

import lombok.Data;

@Data
public class User {
    private String userId;
    private String userName;
    private String password;
    private String email;
    private String avatar;
    private String description;
    private String background;
    private Integer type;//用户类型
}
