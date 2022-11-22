package com.hyy.webcheck.service;


import com.hyy.webcheck.bean.Answer;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerDao {

    List<Answer> getAnswersByArticle(@Param("questionId") Integer questionId);

    List<Answer> getAnswersByIds(@Param("array") Integer[] array,
                                 @Param("userId") String userId,@Param("username") String username);

    List<Answer> getAllScoreByStudent(@Param("userId") String userId);

    List<Answer> getAllQuestionAnswer();


    Integer judgeScore(@Param("answerId") Integer answerId, @Param("score") Integer score ,@Param("updateTime") String dateTime);

    Integer postAnswer(@Param("userId") String userId, @Param("questionId") Integer questionId,
                       @Param("content") String content, @Param("releaseTime") String dateTime);

    void addShowTimes(@Param("answerId") Integer answerId);

    Integer checkSubmit(@Param("userId") String userId, @Param("questionId") Integer questionId);

    Integer changeAnswer(@Param("userId") String userId, @Param("questionId") Integer questionId,
                       @Param("content") String content, @Param("updateTime") String dateTime);

    String searchAnswer(@Param("answerId") Integer answerId);

    Integer searchQuestionIdByAnswerId(@Param("answerId") Integer answerId);



}
