package com.hyy.webcheck.bean;

import lombok.Data;

import java.util.List;

@Data
public class SimpleQuestion {
    private Integer questionId;//问题id

    private String title;//实验标题

    private String example; //模板HTML

    private String description; //问题说明



}
