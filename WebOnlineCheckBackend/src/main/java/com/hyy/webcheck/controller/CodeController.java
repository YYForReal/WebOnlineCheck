package com.hyy.webcheck.controller;

import com.hyy.webcheck.bean.Code;
import com.hyy.webcheck.bean.Img;
import com.hyy.webcheck.config.Result;
import com.hyy.webcheck.service.impl.CodeService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/code")
@CrossOrigin
public class CodeController {

    @Autowired
    CodeService codeService;

    // 获取问题的校验代码
    @GetMapping("/get")
    public Result<List<Code>> searchCodeList(Integer questionId){
        return codeService.searchCode(questionId);
    }

//    @GetMapping("/list")
//    public Result<List<Code>> searchImgList(Integer questionId){
//        return codeService.searchCode(questionId);
//    }


    // 获取指定回答的校验结果
    @GetMapping("/result/get")
    public Result<String> getResult(Integer questionId,Integer answerId){
        return codeService.getResult(questionId,answerId);
    }

    // 提交指定回答的校验结果
    @PostMapping("/result/post")
    public Result<Boolean> postResult(Integer questionId,Integer answerId,String content){
        return codeService.postResult(questionId,answerId,content);
    }

    // 提交指定问题的校验代码
    @PostMapping("/add")
    public Result<Boolean> postCheckCode(Integer questionId,String code){
        return codeService.postCheckCode(questionId,code);
    }

    // 更新指定问题的校验代码
    @PostMapping("/update")
    public Result<Boolean> updateCheckCode(Integer codeId,Integer questionId,String code){
        return codeService.updateCheckCode(codeId,questionId,code);
    }

    // 删除指定问题的校验代码
    @PostMapping("/delete")
    public Result<Boolean> deleteCode(Integer codeId,Integer questionId){
        return codeService.deleteCode(codeId,questionId);
    }


}

