package com.hyy.webcheck.controller;

import com.hyy.webcheck.bean.Question;
import com.hyy.webcheck.bean.QuestionWithScore;
import com.hyy.webcheck.bean.UserToken;
import com.hyy.webcheck.config.Result;
import com.hyy.webcheck.service.impl.QuestionService;
import com.hyy.webcheck.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/question")
@CrossOrigin
public class QuestionController {
    @Autowired
    QuestionService questionService;

    @Autowired
    UserService userService;

    //获取所有问题
    @GetMapping("/get")
    public List<Question> getQuestion() {
        List<Question> list = questionService.getQuestion();
        return list;
    }

    //获取所有问题
    @GetMapping("/getlist")
    public List<QuestionWithScore> getQuestionInfo() {
        List<QuestionWithScore> list = questionService.getQuestionInfo();
        return list;
    }

    @GetMapping("/search")
    public Result<String> searchQuestion(Integer questionId){
        return questionService.searchQuestion(questionId);
    }


    //根据问题id获取对应的所有学生回答
    @GetMapping("/all-score")
    public Result<List<QuestionWithScore>> getAllQuestionScore() {
        return questionService.getAllQuestionScore();
    }


    //上传文章
    @PostMapping("/post")
    public Result<Boolean> postQuestion(String title, String content, String userId,String username,String html) {
        return questionService.postQuestion(title,content,userId,username,html);
    }

    @PostMapping("/update")
    public Result<Boolean> updateQuestion(Integer questionId, String title, String content,
                                          String userId,String username,String html) {
        return questionService.updateQuestion(questionId,title,content,userId,username,html);
    }

    @PostMapping("/delete")
    public Result<Boolean> deleteQuestion(Integer questionId,String userId,String username) {
        UserToken userToken = userService.checkUserToken(userId,username);
        if(userToken==null || userToken.getType() == 0){
            return new Result<>(500,"用户异常 或 权限不足",false);
        }else{
            return questionService.deleteQuestion(questionId);
        }
    }



}

