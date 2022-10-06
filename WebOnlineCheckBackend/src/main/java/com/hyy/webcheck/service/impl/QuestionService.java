package com.hyy.webcheck.service.impl;

import com.hyy.webcheck.bean.Answer;
import com.hyy.webcheck.bean.Question;
import com.hyy.webcheck.bean.QuestionWithScore;
import com.hyy.webcheck.bean.UserToken;
import com.hyy.webcheck.config.Result;
import com.hyy.webcheck.service.AnswerDao;
import com.hyy.webcheck.service.QuestionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class QuestionService {

    @Resource
    QuestionDao questionDao;

    @Autowired
    UserService userService;

    public List<Question> getQuestion() {
        List<Question> list = questionDao.getQuestions();
        return list;
    }
    public List<QuestionWithScore> getQuestionInfo() {
        List<QuestionWithScore> list = questionDao.getQuestionInfo();
        return list;
    }


    public Result<String> searchQuestion(Integer questionId){
        String ans = questionDao.searchQuestion(questionId);
        if(ans == null){
            return new Result<>(500,"问题不存在",ans);
        }else{
            return new Result<>(200,"查询成功",ans);
        }
    }

    public Result<Boolean> postQuestion(String title, String content,String userId,String username) {

        UserToken userToken =  userService.checkUserToken(userId,username);
        if(userToken == null){
            return new Result<>(500,"账号不存在",false);
        }else if(userToken.getType() == 0){
            return new Result<>(500,"权限不足",false);
        }else{
            Date date = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String dateTime = df.format(date); // Formats a Date into a date/time string.
            System.out.println("提交问题：用户ID："+userId + " -- 问题标题："+ title + "-- 时间："+dateTime);

            if (questionDao.postQuestion(title, content,userId,  dateTime) == 1) {
                return new Result<>(200,"问题提交成功",true);
            }
            return new Result<>(500,"问题提交失败",false);
        }

    }

    public Result<List<QuestionWithScore>> getAllQuestionScore(){
        List<QuestionWithScore> list = new ArrayList<>();
        List<Question> questions = getQuestion();
        for(int i=0;i<questions.size();i++){
            Integer id = questions.get(i).getQuestionId();
            QuestionWithScore questionWithScore = new QuestionWithScore();
            questionWithScore.setQuestionId(id);
            questionWithScore.setTitle(questions.get(i).getTitle());
            questionWithScore.setUserScores(
                questionDao.getUserScoreList(id)
            );
            list.add(questionWithScore);
        }
        return new Result<>(200, "获取分数成功", list);
    }

    public Result<Boolean> updateQuestion(Integer questionId, String title,String content,String userId,String username){
        UserToken userToken =  userService.checkUserToken(userId,username);
        if(userToken == null){
            return new Result<>(500,"账号不存在",false);
        }else if(userToken.getType() == 0){
            return new Result<>(500,"权限不足",false);
        }else{
            Date date = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
            String dateTime = df.format(date); // Formats a Date into a date/time string.
            System.out.println("更新问题：用户ID："+userId + " -- 问题标题："+ title + "-- 时间："+dateTime);
            if(questionDao.updateQuestion(questionId,title,content,userId,dateTime)>0){
                return new Result<>(200,"更新成功",true);
            }else{
                return new Result<>(500,"更新失败",false);
            }
        }
    }


    public Result<Boolean> deleteQuestion(int questionId){
        if(questionDao.deleteQuestion(questionId)>0){
            return new Result<>(200,"删除成功",true);
        }else{
            return new Result<>(500,"删除失败",false);
        }
    }



}
