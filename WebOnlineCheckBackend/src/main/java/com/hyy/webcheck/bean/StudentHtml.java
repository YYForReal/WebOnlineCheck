package com.hyy.webcheck.bean;

import lombok.Data;

import java.util.List;

@Data
public class StudentHtml {
    private String content;//回答内容
    private List<Code> codes;//校验代码，一般是一个，可能存在多个
}
