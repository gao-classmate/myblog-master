package com.xsh.aspect;

import com.google.common.cache.Cache;
import com.xsh.annotations.CheckRepeatRequest;
import com.xsh.handler.RepeatRequestException;
import com.xsh.util.GetAddressByIpUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.xsh.util.GetAddressByIpUtils.getClientIPForNginx;


/**
 * @author : xsh
 * @create : 2021-01-12 - 20:35
 * @describe: 频繁请求拦截  测试方法 TestController.testRepeatRequest
 * RequestCache类配置缓存失效时间，即判断多少间隔内为频繁请求
 */
@Order(0) //标记优先级最高
@Component
@Aspect
public class CheckRepeatRequestAspect {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //10
    @Value("${repeatRequestParam.requestNum}")
    private int requestNum;

    @Autowired
    private Cache<String, Integer> cache;
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Around("execution(* com.xsh.web..*(..)) && @annotation(checkRepeatRequest)")
    public Object arround(ProceedingJoinPoint pjp, CheckRepeatRequest checkRepeatRequest) {
        String response = null;
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String sessionId = RequestContextHolder.getRequestAttributes().getSessionId();
            //String key = sessionId + "-" + request.getServletPath();//session可以伪造，不使用session作为key
            //拿到客户端的IP地址
            String ip = getClientIPForNginx(request);
            //ip+访问路径，作为redis的key
            String key = ip + "-" + request.getServletPath();
            logger.info("sessionId:" + sessionId);
            logger.info("客户端的key：{}"+key);
            String ipInfo = GetAddressByIpUtils.getInfoByIp(ip);
            logger.info("客户端IP地址解析：{}"+ipInfo);
            //如果某个客户端ip频繁恶意刷接口，即列入黑名单
            if (checkIpBlack(ip)) {
                Map<String,String> map=new HashMap<>();
                //map.put("ip-path",key);
                map.put("message","因短时间内频繁访问，ip已被拉黑，拒绝访问");
                map.put("解析ip信息",ipInfo);
                response = map.toString();

                response = "因短时间内频繁访问，ip已被拉黑，拒绝访问;" + ipInfo;

                logger.warn(response);
                throw new RepeatRequestException(response);
/*                String headerSource = request.getHeader("x-source");
                if ("message-boardPost".equals(headerSource) && StringUtils.isNotEmpty(sessionId)) {
                    cache.put("message-boardPost" + sessionId, 1);
                    return "redirect:/isRepeatRequest";
                } else if(cache.getIfPresent("message-boardPost" + sessionId)==1){
                    return "redirect:/isRepeatRequest";
                }else {
                    throw new RepeatRequestException(response);
                }*/

            }


            if (cache.getIfPresent(key) == null) {
                //初始化访问次数 1
                cache.put(key, 1);
               //短时间内，即缓存有效期内连续访问超过十次，即加入redis黑名单
            } else if (cache.getIfPresent(key) >= requestNum) {
                //this.redisTemplate.opsForValue().set(ip,request.getServletPath(),1000, TimeUnit.MINUTES);
                this.redisTemplate.opsForList().leftPush("IpBlack:ips", key);
                //this.redisTemplate.expire("IpBlack:ips",10, TimeUnit.MINUTES);
                response = key + "请求频繁，加入redis黑名单";
                throw new Exception(response);
            } else {
                //还在访问的允许次数内
                Integer viewCount = cache.getIfPresent(key);//访问次数
                cache.put(key, ++viewCount);
            }
            //返回注解标注的方法所返回的结果
            Object result = pjp.proceed();
            logger.info("返回结果为：{}",result.toString());
            return result;
        } catch (Throwable e) {
            e.printStackTrace();
            //logger.error("验证频繁请求时出现未知异常!");
            logger.error(response);
            return response;
        }

    }


    /**
     * 对比当前请求IP是否在黑名单中，注意（对比黑名单ip存放在redis中）
     */

    public boolean checkIpBlack(String ip) throws Exception {

        //查看黑名单链表“IpBlack:ips”的长度
        Long size = redisTemplate.opsForList().size("IpBlack:ips");
        if (size >= 1L) {
            for (int i = 0; i < size; i++) {

                //使用equals，则是根据ip+访问路径匹配黑名单；ip不能访问黑名单内对应的路径，但可以访问其它路径
                //使用contains，则是根据ip匹配黑名单。即当ip在黑名单内，就不能访问被@CheckRepeatRequest注解的控制层；

                //redisTemplate.opsForList().index("IpBlack:ips", i).equals(key)
                if (redisTemplate.opsForList().index("IpBlack:ips", i).contains(ip))
                    return true;
            }
        }

        return false;
    }
}
