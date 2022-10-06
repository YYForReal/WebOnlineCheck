package com.hyy.webcheck.service.impl;

import com.hyy.webcheck.bean.Answer;
import com.hyy.webcheck.config.Result;
import com.hyy.webcheck.service.AnswerDao;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class AnswerService {

    @Resource
    AnswerDao answerDao;

    public List<Answer> getAnswers(Integer questionId) {
        List<Answer> list = answerDao.getAnswersByArticle(questionId);
//        for (int i = 0; i < list.size(); i++) {
//            addShowTimes(list.get(i).getAnswerId());
//        }
        return list;
    }



    public boolean judgeScore(Integer answerId, Integer score) {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String dateTime = df.format(date); // Formats a Date into a date/time string.

        if (answerDao.judgeScore(answerId, score,dateTime) == 1) {
            return true;
        } else {
            return false;
        }
    }

    public Result<Boolean> postAnswer(String userId, Integer questionId, String content) {
        Date date = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
        String dateTime = df.format(date); // Formats a Date into a date/time string.

        if(answerDao.checkSubmit(userId,questionId)>0){
            //更新提交
            System.out.println("更新提交：用户ID："+userId + " -- 问题ID："+ questionId + "-- 时间："+dateTime);
            if (answerDao.changeAnswer(userId, questionId, content, dateTime) == 1) {
                return new Result<Boolean>(200,"提交成功",true);
            }
        }else{
            //第一次提交
            System.out.println("提交答案：用户ID："+userId + " -- 问题ID："+ questionId + "-- 时间："+dateTime);
            if (answerDao.postAnswer(userId, questionId, content, dateTime) == 1) {
                return new Result<Boolean>(200,"提交成功",true);
            }
        }

        return new Result<Boolean>(500,"提交失败",false);
    }

    //    增加查看次数
    public void addShowTimes(Integer answerId) {
        answerDao.addShowTimes(answerId);
    }

    public Result<String> searchAnswer(Integer answerId){
        String ans = answerDao.searchAnswer(answerId);
        if(ans == null){
            return new Result<>(500,"答案不存在",ans);
        }else{
            return new Result<>(200,"查询成功",ans);
        }
    }


    public static int[] getData(String baseStr) {
        try {
            //BufferedImage img = ImageIO.read(new File(name));
            BufferedImage img = base64ToBufferedImage(baseStr);
            BufferedImage slt = new BufferedImage(100, 100,
                    BufferedImage.TYPE_INT_RGB);
            slt.getGraphics().drawImage(img, 0, 0, 100, 100, null);
            // ImageIO.write(slt,"jpeg",new File("slt.jpg"));
            int[] data = new int[256];
            for (int x = 0; x < slt.getWidth(); x++) {
                for (int y = 0; y < slt.getHeight(); y++) {
                    int rgb = slt.getRGB(x, y);
                    Color myColor = new Color(rgb);
                    int r = myColor.getRed();
                    int g = myColor.getGreen();
                    int b = myColor.getBlue();
                    data[(r + g + b) / 3]++;
                }
            }
            // data 就是所谓图形学当中的直方图的概念
            return data;
        } catch (Exception exception) {
            System.out.println("有文件没有找到,请检查文件是否存在或路径是否正确");
            return null;
        }
    }

    public static float compare(int[] s, int[] t) {
        try {
            float result = 0F;
            for (int i = 0; i < 256; i++) {
                int abs = Math.abs(s[i] - t[i]);
                int max = Math.max(s[i], t[i]);
                result += (1 - ((float) abs / (max == 0 ? 1 : max)));
            }
            return (result / 256) * 100;
        } catch (Exception exception) {
            return 0;
        }
    }


    private static BufferedImage base64ToBufferedImage(String base64) throws IOException {
        Base64 base = new Base64();
        byte[] image = base.decode(base64.replace("data:image/png;base64,", ""));
        InputStream stream = new ByteArrayInputStream(image);
        BufferedImage bufferedImage = ImageIO.read(stream);
        return bufferedImage;
    }



}
