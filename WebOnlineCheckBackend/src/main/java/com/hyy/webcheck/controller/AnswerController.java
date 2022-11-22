package com.hyy.webcheck.controller;

import com.hyy.webcheck.bean.Answer;
import com.hyy.webcheck.bean.StudentHtml;
import com.hyy.webcheck.config.Result;
import com.hyy.webcheck.service.impl.AnswerService;
import com.hyy.webcheck.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/answer")
@CrossOrigin
public class AnswerController {

    @Autowired
    AnswerService answerService;

    @Autowired
    UserService userService;



    //根据问题id获取对应的所有学生回答
    @GetMapping("/get")
    public List<Answer> getAnswers(Integer questionId) {
        List<Answer> list = answerService.getAnswers(questionId);
        return list;
    }

    //根据问题id获取对应的所有学生回答
    @GetMapping("/multi-get")
    public List<Answer> getAnswersByIds(@RequestParam(value = "questionIds") Integer [] questionIds,
                           @RequestParam(value = "userId",defaultValue = "") String userId,
                           @RequestParam(value = "username",defaultValue = "") String username) {
        List<Answer> list = answerService.getAnswersByIds(questionIds,userId,username);
        return list;
    }


    //回答评分
    @PostMapping("/score")
    public String judgeScore(Integer answerId, Integer score,String userId,String username) {
        if(userService.checkUser(userId,username)>0){
            if (answerService.judgeScore(answerId, score)) {
                return "OK";
            }else{
                return "FAIL";
            }
        } else {
            return "FAIL";
        }
    }

    //上传文章
    @PostMapping("/post")
    public String postAnswer(String userId, Integer questionId, String content) {
        if (answerService.postAnswer(userId, questionId, content).getData() == true) {
            return "OK";
        } else {
            return "FAIL";
        }
    }

    @PostMapping("/compare")
    public Result<Float> CompareAnswer(String pic1,String pic2) {

        float percent = answerService.compare(answerService.getData(pic1), answerService.getData(pic2));

        if (percent == 0) {
            return new Result<>(500,"无法比较",0.0f);
//            return "无法比较";
        } else {
            return new Result<>(200,"两张图片相似度为："+percent+"%",percent);
//            return "两张图片的相似度为：" + percent + "%";
        }
    }

    @PostMapping("/newcompare")
    public Result<Float> newCompareAnswer(String pic1,String pic2) throws IOException {
        return answerService.pyCompare(pic1,pic2);
    }

//    @GetMapping("/search")
//    public Result<String> searchAnswer(Integer answerId){
//        return answerService.searchAnswer(answerId);
//    }

    @GetMapping("/search")
    public Result<StudentHtml> searchAnswer(Integer answerId){
        return answerService.searchStudentHtml(answerId);
    }





//    @GetMapping("/pyCompare")
//    public Result<List<String>> pyCompare() throws Exception{
//        return answerService.pyCompare();
//    }

}

