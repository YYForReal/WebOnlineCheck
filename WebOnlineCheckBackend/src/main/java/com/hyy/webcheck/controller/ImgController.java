package com.hyy.webcheck.controller;

import com.hyy.webcheck.bean.Img;
import com.hyy.webcheck.bean.UserToken;
import com.hyy.webcheck.config.Result;
import com.hyy.webcheck.service.impl.ImgService;
import com.hyy.webcheck.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/img")
@CrossOrigin
public class ImgController {
    @Autowired
    ImgService imgService;

    @Autowired
    UserService userService;

    @GetMapping("/list")
    public Result<List<Img>> searchImgList(){
        return imgService.searchImgList();
    }

    @PostMapping("/post")
    public Result<Boolean> postImg(Integer questionId,String url,String imgId){
        if(imgId == null || imgId.equals("null")){
            return imgService.addImg(questionId,url);
        }else{
            Integer id = Integer.parseInt(imgId);
            return imgService.updateImg(questionId,url,id);
        }
    }
    @PostMapping("/delete")
    public Result<Boolean> deleteImg(Integer imgId,String userId,String username) {
        UserToken userToken = userService.checkUserToken(userId,username);
        if(userToken==null || userToken.getType() == 0){
            return new Result<>(500,"用户异常 或 权限不足",false);
        }else{
            return imgService.deleteImg(imgId);
        }
    }

}

