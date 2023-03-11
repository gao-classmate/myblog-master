package com.xsh.web;

import com.alibaba.fastjson.JSONObject;
import com.xsh.dao.GiteeRepository;
import com.xsh.handler.StateErrorException;
import com.xsh.pojo.Gitee;
import com.xsh.util.GiteeHttpClient;
import com.xsh.vo.GiteeConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.UUID;

@Controller
@RequestMapping("/gitee")
public class GiteeController {

    @Autowired
    private GiteeRepository giteeRepository;


    @Value("${cookie.time}")
    private Integer time;


    /**
     * 请求授权
     * @param session
     * @return
     */
    @GetMapping("/oauth")
    public String giteeAuth(HttpSession session){
        // 用于第三方应用防止CSRF攻击
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        session.setAttribute("state", uuid);

        // Step1：获取Authorization Code
        String url = "https://gitee.com/oauth/authorize?response_type=code" +
                "&client_id=" + GiteeConstant.CLIENT_ID +
                "&redirect_uri=" + URLEncoder.encode(GiteeConstant.CALLBACK) +
                "&state=" + uuid +
                "&scope=user_info";
        //因为使用的是thymeleaf模板引擎，所以是无法解析一个网址的，只能重定向
        return "redirect:"+url;
    }


    @RequestMapping("/callback")
    public String giteeCallBack(HttpServletRequest request, HttpServletResponse response) throws IOException, StateErrorException {
        HttpSession session = request.getSession();
        // 得到Authorization Code
        String code = request.getParameter("code");
        // 我们放在地址中的状态码
        String state = request.getParameter("state");
        String uuid = (String) session.getAttribute("state");

        // 验证信息我们发送的状态码
        if (null != uuid) {
            // 状态码不正确，直接返回登录页面
            if (!uuid.equals(state)) {
                throw new StateErrorException("Gitee state错误");
            }
        }

        // Step2：通过Authorization Code获取Access Token
        String url = "https://gitee.com/oauth/token?grant_type=authorization_code" +
                "&client_id=" + GiteeConstant.CLIENT_ID +
                "&client_secret=" + GiteeConstant.CLIENT_SECRET +
                "&code=" + code +
                "&redirect_uri=" + GiteeConstant.CALLBACK;
        JSONObject accessTokenJson = GiteeHttpClient.getAccessToken(url);

        // Step3: 获取用户信息
        url = "https://gitee.com/api/v5/user?access_token=" + accessTokenJson.get("access_token");
        JSONObject jsonObject = GiteeHttpClient.getUserInfo(url);
       // System.out.println(jsonObject);
        /*
         * 获取到用户信息之后，就该写你自己的业务逻辑了
         */
        session.setAttribute("gitee_id",jsonObject.get("id"));  //gitee_id,用来唯一标识用户
        session.setAttribute("nickname",jsonObject.get("login")); //github名
        session.setAttribute("avatar",jsonObject.getString("avatar_url"));

        session.setAttribute("loginStatus","true");//登录状态
        session.setAttribute("loginType",2);//登录方式,1 QQ，2 github
        Gitee g_user = giteeRepository.findUserByGiteeId(jsonObject.getInteger("id"));
        if(g_user == null){
            Gitee gitee=new Gitee();
            gitee.setAvatar(jsonObject.getString("avatar_url"));
            gitee.setCreatedTime(jsonObject.getString("created_at"));
            gitee.setNickname(jsonObject.getString("login"));
            gitee.setIndexUrl(jsonObject.getString("html_url"));
            gitee.setPublicRepos(jsonObject.getString("public_repos"));
            gitee.setNickname(jsonObject.getString("login"));
            gitee.setSubscriptions(jsonObject.getString("subscriptions_url"));
            gitee.setUpdatedTime(jsonObject.getString("updated_at"));
            gitee.setGiteeId(jsonObject.getInteger("id"));
            gitee.setReceivedEventsUrl(jsonObject.getString("received_events_url"));
            gitee.setLoginTime(new Date().toString());

            giteeRepository.save(gitee);
        }
        return "redirect:/gitee/giteeInfo";





    }


    @GetMapping("/giteeInfo")
    public String post(HttpSession session, Model model) throws InterruptedException {
        Integer giteeId =(Integer) session.getAttribute("gitee_id");
        Gitee user = giteeRepository.findUserByGiteeId(giteeId);

       /* if(user.getAvatar() == null){
            user.setAvatar("https://foruda.gitee.com/avatar/1663679760318378889/7492713_yu_yinghua_1663679760.png!avatar200");
        }*/
        session.setAttribute("avatar",user.getAvatar());//更新session中头像信息
        model.addAttribute("github",user);
        return "giteeInfo";
    }

    @PostMapping("/saveIp")
    public String post(HttpSession session,Gitee github) {
        Integer node_id = (Integer) session.getAttribute("gitee_id");
        Gitee githubuser = giteeRepository.findUserByGiteeId(node_id);
        if(githubuser.getCip() == null){//ip信息为空，第一次登录，将前端传入的ip信息存入
            giteeRepository.addIpaddress(github.getCip(),github.getCid(),github.getCname(),node_id);
        }
        return "giteeInfo";
    }

    @GetMapping("/giteelogout")
    public String logout(HttpSession session){
        session.removeAttribute("loginStatus"); //退出登录，将session中的user信息清除
        session.removeAttribute("avatar");
        session.removeAttribute("nickname");
        session.removeAttribute("gitee_id");
        session.removeAttribute("loginType");
        return "redirect:/index";
    }




}
