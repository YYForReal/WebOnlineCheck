package com.hyy.webcheck.service.impl;

import com.hyy.webcheck.bean.User;
import com.hyy.webcheck.bean.UserScore;
import com.hyy.webcheck.bean.UserToken;
import com.hyy.webcheck.service.AnswerDao;
import com.hyy.webcheck.service.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    UserDao userDao;

    @Autowired
    AnswerDao answerDao;


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
        return userDao.checkUserToken(userId,username) ;
    }

}
