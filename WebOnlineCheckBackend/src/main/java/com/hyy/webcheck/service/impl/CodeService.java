package com.hyy.webcheck.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.hyy.webcheck.bean.Code;
import com.hyy.webcheck.bean.Img;
import com.hyy.webcheck.config.Result;
import com.hyy.webcheck.service.CodeDao;
import com.hyy.webcheck.service.ImgDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CodeService {

    @Resource
    CodeDao codeDao;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    public Result<List<Code>> searchCode(Integer questionId){
        // 从redis处获取
        String codeStr = redisTemplate.opsForValue().get("checkCode_"+questionId);
        List<Code> res ;

        if(codeStr == null){
            // 增加redis
            res = codeDao.searchCode(questionId);
            String str = JSONArray.toJSONString(res);
            System.out.println(new Date().toString() +  " - searchCode:" + str);
            redisTemplate.opsForValue().set("checkCode_"+questionId,str);
            redisTemplate.expire("checkCode_"+questionId,7,TimeUnit.DAYS);
        }else{
            res = JSONArray.parseArray(codeStr,Code.class);
            System.out.println(new Date().toString() +  " - getCode from redis:" + codeStr);
        }

        if(res == null){
            return new Result<>(400,"校验代码不存在",null);
        }else{
            return new Result<>(200,"查询成功",res);
        }
    }

    public Result<Boolean> postResult(Integer questionId, Integer answerId,String content) {
        redisTemplate.opsForValue().set("code_"+questionId + "_" + answerId,content);
        redisTemplate.expire("code_"+questionId + "_" + answerId,21,TimeUnit.DAYS);
        return new Result<>(200,"上传成功",true);
    }
    public Result<String> getResult(Integer questionId, Integer answerId) {
        String res = redisTemplate.opsForValue().get("code_" + questionId  + "_" + answerId);
        if(res == null){
            return new Result<>(400,"尚未进行代码比对,无结果",res);
        }
        return new Result<>(200,"查询成功",res);
    }

    public Result<Boolean> postCheckCode(Integer questionId, String code) {
        if (codeDao.postCheckCode(questionId,code) >= 0){
            return new Result<>(200,"上传成功",true);
        }else{
            return new Result<>(500,"上传失败",false);
        }

    }

    public Result<Boolean> updateCheckCode(Integer codeId, Integer questionId, String code) {
        redisTemplate.delete("checkCode_"+questionId);
        if (codeDao.updateCheckCode(codeId,questionId,code) >= 0){
            return new Result<>(200,"更新成功",true);
        }else{
            return new Result<>(500,"更新失败",false);
        }
    }

    public Result<Boolean> deleteCode(Integer codeId,Integer questionId) {
        if(questionId != null){
            redisTemplate.delete("checkCode_"+questionId);
        }
        if (codeDao.deleteCode(codeId) >= 0){
            return new Result<>(200,"删除成功",true);
        }else{
            return new Result<>(500,"删除失败",false);
        }
    }

}
