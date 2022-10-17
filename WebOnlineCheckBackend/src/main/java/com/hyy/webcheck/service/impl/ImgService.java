package com.hyy.webcheck.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.hyy.webcheck.bean.*;
import com.hyy.webcheck.config.Result;
import com.hyy.webcheck.service.AnswerDao;
import com.hyy.webcheck.service.ImgDao;
import com.hyy.webcheck.service.QuestionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ImgService {

    @Resource
    ImgDao imgDao;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    public Result<List<Img>> searchImgList(){
        List<Img> res ;
        String str = redisTemplate.opsForValue().get("imgList");
        if(str == null){
            // 从数据库拿，放Redis里面
            res =imgDao.searchImg();
            str = JSONArray.toJSONString(res);
            redisTemplate.opsForValue().set("imgList", str);
            redisTemplate.expire("imgList",1, TimeUnit.HOURS);
            System.out.println("Save ImgList into Redis:" + str + " --- time:" + new Date().toString());
        }else {
            //从Redis拿
            res = JSONArray.parseArray(str,Img.class);
        }
        if(res == null){
            return new Result<>(500,"图片不存在",null);
        }else{
            return new Result<>(200,"查询成功",res);
        }
    }


    public Result<Boolean> addImg(Integer questionId, String url) {
        String message = "";
        int status = 0;
        Boolean res = false;
        try{
            res = imgDao.addImg(questionId,url) > 0 ? true : false;
            if(res){
                message = "图片增加成功";
                status = 200;
            }else{
                status = 500;
                message = "图片增加失败";
            }
        }catch(Exception e){
            message = e.getMessage();
            res = false;
            status = 500;
        }finally {
            return new Result<>(status,message,res);
        }
    }

    public Result<Boolean> updateImg(Integer questionId, String url,Integer imgId) {
        String message = "";
        int status = 0;
        Boolean res = false;
        try{
            res = imgDao.updateImg(questionId,url,imgId) > 0 ? true : false;
            if(res){
                message = "图片修改成功";
                status = 200;
            }else{
                message = "图片修改失败";
                status = 500;
            }
        }catch(Exception e){
            message = e.getMessage();
            res = false;
            status = 500;
        }finally {
            return new Result<>(status,message,res);
        }
    }

    public Result<Boolean> deleteImg(Integer imgId){
        if(imgId == null){
            return  new Result<>(500,"图片id为空",false);
        }
        if(imgDao.deleteImg(imgId)>0){
            return new Result<>(200,"删除成功",true);
        }else{
            return new Result<>(500,"删除失败",false);
        }
    }

}
