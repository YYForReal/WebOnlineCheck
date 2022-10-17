package com.hyy.webcheck.service;


import com.hyy.webcheck.bean.Img;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImgDao {

    List<Img> searchImg();


    Integer addImg(@Param("questionId") Integer questionId,@Param("url")  String url);

    Integer updateImg(@Param("questionId") Integer questionId,@Param("url") String url,
                      @Param("imgId")Integer imgId);

    Integer deleteImg(@Param("imgId") Integer imgId);
}
