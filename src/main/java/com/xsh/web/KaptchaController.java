package com.xsh.web;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.xsh.util.GetAddressByIpUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.xsh.util.GetAddressByIpUtils.getClientIPForNginx;

@Controller
public class KaptchaController {
    @Autowired
    private DefaultKaptcha defaultKaptcha;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/common/kaptcha")
    public void defaultKaptcha(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        //创建字节数组用于存放图片信息
        byte[] captchaOutputStream=null;
        //获得二进制输出流
        ByteArrayOutputStream imgOutputStream=new ByteArrayOutputStream();

        //生产验证码字符串并保存到session中
        String verifyCode = defaultKaptcha.createText();


        httpServletRequest.getSession().setAttribute("verifyCode",verifyCode);
        //使用生成的验证码字符串，完成图片的生成
        BufferedImage image = defaultKaptcha.createImage(verifyCode);

        ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = getClientIPForNginx(request);
        Map<String,String> map=new HashMap<>();
        map.put("verifyCode",verifyCode);
        map.put("ip",ip);
        rabbitTemplate.convertAndSend("sms.exchange","verifycode.sms",map);


        //将图片写到流中
        try {

            ImageIO.write(image,"jpg",imgOutputStream);
        } catch (IllegalArgumentException e) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);

        }
        //使用HttpServletResponse将图片写到浏览器中
        captchaOutputStream= imgOutputStream.toByteArray();
        //通过response设定响应的请求类型
        //no-store用于防止重要的信息被无意地发布。在请求消息中发送将使得请求和响应消息都不使用缓存

        httpServletResponse.setHeader("Cache-Control","no-store");
        //no-cache 指示请求或者响应消息不能缓存
        httpServletResponse.setHeader("Pragma","no-cache");
        /**
         * expires是response的一个属性，它可以设置页面在浏览器的缓存里保存的时间，
         * 超过设定时间就过期。过期后再次浏览该页面就需要重新请求服务器发送页面数据，
         * 如果在规定时间内再次访问页面，就不需要从服务器传送而直接从缓存中读取。
         *
         */
        httpServletResponse.setDateHeader("Expires",0);
        httpServletResponse.setContentType("image/jpeg");
        //通过response获得输出流
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        outputStream.write(captchaOutputStream);
        outputStream.flush();
        outputStream.close();

    }
}
