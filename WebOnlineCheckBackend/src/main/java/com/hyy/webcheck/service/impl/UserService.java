package com.hyy.webcheck.service.impl;

import com.alibaba.fastjson.JSON;
import com.hyy.webcheck.bean.*;
import com.hyy.webcheck.bean.User.User;
import com.hyy.webcheck.bean.User.UserScore;
import com.hyy.webcheck.bean.User.UserToken;
import com.hyy.webcheck.service.AnswerDao;
import com.hyy.webcheck.service.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    AnswerDao answerDao;

    @Autowired
    RedisTemplate<String,String> redisTemplate;


    public List<String> getAllUserId() {
        return userDao.getAllUserId();
    }

    public List<UserScore> getAllStudentScore(){
        List<User> users = userDao.getAllStudent();
        List<UserScore> res = new ArrayList<UserScore>();
        for(int i=0;i<users.size();i++){
            UserScore userScore = new UserScore();
            userScore.setUserId(users.get(i).getUserId());
            userScore.setUserName(users.get(i).getUserName());
            userScore.setAnswerList(answerDao.getAllScoreByStudent(userScore.getUserId()));
            res.add(userScore);
        }
        return res;
    }

    // 获取全部学生的提交列表
    public CSVData getCSVData(){
        List<User> users = userDao.getAllStudent();
        List<Answer> answers = answerDao.getAllQuestionAnswer();
        CSVData csvData = new CSVData();
        csvData.setUsers(users);
        csvData.setAnswers(answers);
        return csvData;
    }

    public UserScore getStudentScoreById(String userId,String username){
        UserScore userScore = new UserScore();
        userScore.setUserId(userId);
        userScore.setUserName(username);
        userScore.setAnswerList(answerDao.getAllScoreByStudent(userScore.getUserId()));
        return userScore;
    }

    public User getUserInfoById(String userId) {

        return userDao.getUserInfoById(userId);
    }


    public boolean insertLoginInfo(String userId, String password) {
        return userDao.insertLoginInfo(userId, password) == 1;
    }


    public boolean updateAvatarUrl(String userId, String avatar) {
        return userDao.updateAvatarUrl(userId, avatar) == 1;
    }


    public boolean updateBackground(String userId, String background) {
        return userDao.updateBackground(userId, background) == 1;
    }


    public boolean updateDescription(String userId, String description) {
        return userDao.updateDescription(userId, description) == 1;
    }


    public boolean updateEmail(String userId, String email) {
        return userDao.updateEmail(userId, email) == 1;
    }


    public boolean updateUserName(String userId, String userName) {
        return userDao.updateUserName(userId, userName) == 1;
    }


    public boolean updatePassword(String userId, String password) {
        return userDao.updatePassword(userId, password) == 1;
    }


    public int getArticleNumber(String userId) {
        return userDao.getArticleNumber(userId);
    }


    public int getQuestionNumber(String userId) {
        return userDao.getQuestionNumber(userId);
    }


    public int getChoiceNumber(String userId) {
        return userDao.getChoiceNumber(userId);
    }


    public int checkUser(String userId, String username) {
        return userDao.checkUser(userId,username) ;
    }

    public UserToken checkUserToken(String userId, String username) {
        UserToken userToken;
        String str = redisTemplate.opsForValue().get(userId+"_"+username);
        if(str == null){
            // 从数据库拿，放Redis里面
            userToken = userDao.checkUserToken(userId,username) ;
            str = JSON.toJSONString(userToken);
            redisTemplate.opsForValue().set(userId+"_"+username, str);
            redisTemplate.expire(userId+"_"+username,1, TimeUnit.DAYS);
        }else {
            //从Redis拿
            userToken = JSON.parseObject(str,UserToken.class);
        }
        return userToken;
    }

}
