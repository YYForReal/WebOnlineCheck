package com.hyy.webcheck.service;


import com.hyy.webcheck.bean.Answer;
import com.hyy.webcheck.bean.Question;
import com.hyy.webcheck.bean.QuestionWithScore;
import com.hyy.webcheck.bean.UserOneScore;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionDao {

    List<Question> getQuestions();

    List<QuestionWithScore> getQuestionInfo();


    String searchQuestion(@Param("questionId") Integer questionId);

    Integer postQuestion(@Param("title") String title, @Param("content") String content,
                         @Param("userId") String userId, @Param("releaseTime") String dateTime);

    List<UserOneScore> getUserScoreList(@Param("questionId") Integer questionId);

    Integer updateQuestion(@Param("questionId") Integer questionId, @Param("title") String title,
                           @Param("content") String content, @Param("userId") String userId,
                           @Param("updateTime") String dateTime);

    Integer deleteQuestion(@Param("questionId") Integer questionId);


}
