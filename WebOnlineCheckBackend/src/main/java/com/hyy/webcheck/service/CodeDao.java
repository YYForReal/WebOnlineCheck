package com.hyy.webcheck.service;


import com.hyy.webcheck.bean.Code;
import com.hyy.webcheck.bean.Img;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CodeDao {

    List<Code> searchCode(@Param("questionId") Integer questionId);

    Integer updateCheckCode(@Param("codeId") Integer codeId,@Param("questionId")  Integer questionId,
                            @Param("code") String code);

    Integer postCheckCode(@Param("questionId") Integer questionId,@Param("code")  String code);

    Integer deleteCode(@Param("codeId") Integer codeId);

}
