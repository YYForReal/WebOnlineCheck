package com.hyy.webcheck.service;


import com.hyy.webcheck.bean.*;
import com.hyy.webcheck.bean.User.UserOneScore;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionDao {

    List<Question> getQuestions();

    List<QuestionWithScore> getQuestionInfoWithScore();

    List<SimpleQuestion> getQuestionInfo();

    String searchQuestion(@Param("questionId") Integer questionId);

    Integer postQuestion(@Param("title") String title, @Param("content") String content,
                         @Param("userId") String userId, @Param("releaseTime") String dateTime,
                         @Param("html") String html,@Param("display") Integer display,
                        @Param("description") String description,@Param("example") String example);

    List<UserOneScore> getUserScoreList(@Param("questionId") Integer questionId);

    Integer updateQuestion(@Param("questionId") Integer questionId, @Param("title") String title,
                           @Param("content") String content, @Param("userId") String userId,
                           @Param("updateTime") String dateTime,@Param("html") String html,
                           @Param("display") Integer display,@Param("description") String description,
                            @Param("example") String example
    );

    Integer deleteQuestion(@Param("questionId") Integer questionId);


}
