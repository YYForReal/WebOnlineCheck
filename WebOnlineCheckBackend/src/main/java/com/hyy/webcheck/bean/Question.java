package com.hyy.webcheck.bean;

import lombok.Data;

import java.util.Date;

@Data
public class Question {
    private Integer questionId;//问题id

    private String title;//实验标题

    private String content;//内容

    private String userId;//作者名字

    private String username;//作者名字

    private Date releaseTime;//文章创建时间

    private Date updateTime;//文章最后一次更新时间

    private String img;//图片效果

    private String html;// 比对的html内容

}
