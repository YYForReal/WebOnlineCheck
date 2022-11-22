package com.hyy.webcheck.bean.User;

import com.hyy.webcheck.bean.Answer;
import lombok.Data;

import java.util.List;

@Data
public class UserScore {
    private String userId;
    private String userName;

    private List<Answer> answerList;
}
