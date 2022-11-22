package com.hyy.webcheck.controller;

import com.hyy.webcheck.bean.*;
import com.hyy.webcheck.bean.User.User;
import com.hyy.webcheck.bean.User.UserScore;
import com.hyy.webcheck.bean.User.UserToken;
import com.hyy.webcheck.config.Result;
import com.hyy.webcheck.service.impl.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin
public class UserController {

    @Resource
    UserService userService;

    @GetMapping("/getAllUserId")
    public List<String> getAllUserId() {
        return userService.getAllUserId();
    }

    @GetMapping("/getUserInfo")
    public User getUserInfoById(String userId) {
        return userService.getUserInfoById(userId);
    }

    // 获取全部学生的成绩
    @GetMapping("/get/scores")
    public Result<List<UserScore>> getAllStudentScore () {
        Result<List<UserScore>> res = new Result<>(200,"查询成功",userService.getAllStudentScore());
        return res;
    }

    // 获取全部学生的提交列表
    @GetMapping("/get/submit-list")
    public Result<CSVData> getAllStudentSubmitList () {
        Result<CSVData> res = new Result<>(200,"查询成功",userService.getCSVData());
        return res;
    }


    // 获取单个学生的成绩
    @GetMapping ("/get/score")
    public Result<UserScore> getStudentScoreById (String userId,String username) {
        if (userService.checkUser(userId,username) > 0 ){
            return new Result<>(200,"查询成功",userService.getStudentScoreById(userId,username));
        }
        return new Result<>(400,"数据库查无此人",null);
    }


    @PostMapping("/postLoginInfo")
    public String insertLoginInfo(String userId, String password) {
        return userService.insertLoginInfo(userId, password) ? "OK" : "FAIL";
    }

    @PostMapping("/postAvatar")
    public String updateAvatarUrl(String userId, String avatar) {
        return userService.updateAvatarUrl(userId, avatar) ? "OK" : "FAIL";
    }

    @PostMapping("/postEmail")
    public String updateEmail(String userId, String email) {
        return userService.updateEmail(userId, email) ? "OK" : "FAIL";
    }

    @PostMapping("/postPassword")
    public String updatePassword(String userId, String password) {
        return userService.updatePassword(userId, password) ? "OK" : "FAIL";
    }

    @PostMapping("/postDes")
    public String updateDescription(String userId, String description) {
        return userService.updateDescription(userId, description) ? "OK" : "FAIL";
    }

    @PostMapping("/postName")
    public String updateUserName(String userId, String userName) {
        return userService.updateUserName(userId, userName) ? "OK" : "FAIL";
    }

    @PostMapping("/postBackground")
    public String updateBackground(String userId, String background) {
        return userService.updateBackground(userId, background) ? "OK" : "FAIL";
    }

    @PostMapping("/check")
    public String checkUser(String userId, String username) {
        return userService.checkUser(userId, username) == 1 ? "OK" : "FAIL";
    }

    @PostMapping("/check-token")
    public UserToken checkUserToken(String userId, String username) throws Exception {
        UserToken userToken = userService.checkUserToken(userId, username);
        if(userToken == null) {
            throw new Exception("学号("+userId+")与姓名("+username+")不匹配");
        }
        return userToken;
    }


}
