package com.hyy.webcheck.bean;

import lombok.Data;

import java.util.Date;

@Data
public class Answer {
    private Integer answerId;//回答id

    private Integer questionId;

    private String questionTitle;//问题title

    private String content;//回答内容

    private String userId;//回答作者

    private String username;//作者名字

    private String authorDescription;//作者描述

    private String authorAvatar;//作者头像

    private Date releaseTime;//文章创建时间

    private Date updateTime;//文章最后一次更新时间

    private Integer score;//分数

    private Integer showTimes;//查看数

    private String compareText;//比对文本

}
