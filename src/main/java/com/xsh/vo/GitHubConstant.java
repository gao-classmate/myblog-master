package com.xsh.vo;

/**
 * @author : xsh
 * @create : 2020-03-22 - 15:11
 * @describe:
 */
public class GitHubConstant {
    // 这里填写在GitHub上注册应用时候获得 CLIENT ID
    public static final String  CLIENT_ID="Iv1.17e66ff0fbffd89d";
    //这里填写在GitHub上注册应用时候获得 CLIENT_SECRET
    public static final String CLIENT_SECRET="c3496b09ab62454d5a3a72c4cf374ee1dc66a007";
    // 回调路径
    public static final String CALLBACK = "http://127.0.0.1/github/callback";

    //获取code的url
    public static final String CODE_URL = "https://github.com/login/oauth/authorize?client_id="+CLIENT_ID+"&state=STATE&redirect_uri="+CALLBACK+"";
    //获取token的url
    public static final String TOKEN_URL = "https://github.com/login/oauth/access_token?client_id="+CLIENT_ID+"&client_secret="+CLIENT_SECRET+"&code=CODE&redirect_uri="+CALLBACK+"";
    //获取用户信息的url
    public static final String USER_INFO_URL = "https://api.github.com/user?access_token=TOKEN";

}
