package com.hyy.webcheck.bean;

import lombok.Data;

import java.util.List;

@Data
public class QuestionWithScore {
    private Integer questionId;//问题id

    private String title;//实验标题

    private List<UserOneScore> userScores; //学生分数


}
