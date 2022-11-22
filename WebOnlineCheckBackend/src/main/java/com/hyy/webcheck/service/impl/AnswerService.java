package com.hyy.webcheck.service.impl;

import com.hyy.webcheck.bean.Answer;
import com.hyy.webcheck.bean.Code;
import com.hyy.webcheck.bean.StudentHtml;
import com.hyy.webcheck.config.Result;
import com.hyy.webcheck.service.AnswerDao;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class AnswerService {

    @Resource
    AnswerDao answerDao;

    @Autowired
    CodeService codeService;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    public List<Answer> getAnswers(Integer questionId) {
        List<Answer> list = answerDao.getAnswersByArticle(questionId);
//        for (int i = 0; i < list.size(); i++) {
//            addShowTimes(list.get(i).getAnswerId());
//        }
        return list;
    }

    public List<Answer> getAnswersByIds(Integer[] questionId,String userId,String username) {
        userId = "%" + userId + "%";
        username = "%" + username + "%";
        List<Answer> list = answerDao.getAnswersByIds(questionId,userId,username);
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

//    public Result<String> searchAnswer(Integer answerId){
//        String ans = answerDao.searchAnswer(answerId);
//        if(ans == null){
//            return new Result<>(500,"答案不存在",ans);
//        }else{
//            return new Result<>(200,"查询成功",ans);
//        }
//    }

    public Result<StudentHtml> searchStudentHtml(Integer answerId){
        // 1. 获取答案对应的问题
        String questionStr = redisTemplate.opsForValue().get("answer_"+answerId);
        Integer questionId;
        if(questionStr == null){
            questionId = answerDao.searchQuestionIdByAnswerId(answerId);
            redisTemplate.opsForValue().set("answer_"+answerId, String.valueOf(questionId));
        }else{
            questionId = Integer.parseInt(questionStr);
        }

        // 2. 根据问题ID获取对应的校验代码
        List<Code> codes = codeService.searchCode(questionId).getData();

        // 3. 根据答案ID获取对应的HTML代码文本
        String ans = answerDao.searchAnswer(answerId);

        // 4. 构造返回
        StudentHtml studentHtml = new StudentHtml();
        studentHtml.setContent(ans);
        studentHtml.setCodes(codes);
        if(ans == null){
            return new Result<>(500,"答案不存在",studentHtml);
        }else{
            return new Result<>(200,"查询成功",studentHtml);
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


    public Result<Float> pyCompare(String baseStr1,String baseStr2) throws IOException{
        String tag1 = String.valueOf(Math.floor(Math.random()*10000));
        String tag2 = String.valueOf(Math.floor(Math.random()*10000));

        //将base64编码字符串存储redis
        String key1 = "pic_"+ tag1 + "_" + tag2;
        String key2 = "pic_"+ tag2 + "_" + tag1;

        redisTemplate.opsForValue().set(key1,baseStr1,Duration.ofHours(1));
        redisTemplate.opsForValue().set(key2,baseStr2, Duration.ofHours(1));
        redisTemplate.expire(key1,10,TimeUnit.MINUTES);
        redisTemplate.expire(key2,10,TimeUnit.MINUTES);
        //前面一半是本地环境下的python的启动文件地址，后面一半是要执行的python脚本地址

        String property = System.getProperty("user.dir");
        System.out.println( "path:" + property);
//        String path = property + "/src/main/resources/static/";

//        String[] arguments = new String[] {"python.exe", "E:\\PycharmProjects\\pytorchLearning\\test.py","--key1="+key1,"--key2="+key2};
        String[] arguments = new String[] {"python", property + "/test.py","--key1="+key1,"--key2="+key2};

        Process proc;
        List<String> data = new ArrayList<>();
        int status = 0;
        try {
            proc = Runtime.getRuntime().exec(arguments);// 执行py文件
            //用输入输出流来截取结果
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
                data.add(line);
            }
            in.close();
            //waitFor是用来显示脚本是否运行成功，1表示失败，0表示成功，还有其他的表示其他错误
            int re = proc.waitFor();
            System.out.println(re);
            if(re == 1){
                return new Result<>(500,"调用失败："+data.toString(),0.0f);
            }else if(re == 0){
                return new Result<>(200,"调用成功:"+data.toString(),Float.parseFloat(data.get(0)));
            }else if(re == 2){
                return new Result<>(500,"找不到执行的py程序:"+data.toString(),0.0f);
            }
            else{
                return new Result<>(re,"其他错误:"+data.toString(),0.0f);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Result<>(500,"程序执行错误",null);
    }

}
