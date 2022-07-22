# myblog-master个人博客
- 本博客最初是模仿李仁密老师的小而美blog
- 后来随着学习整合了一些东西进去
# 新增

- 基于redis实现接口防刷校验，自定义注解@CheckRepeatRequest，在对应的接口上加上此注解即可防止频繁访问接口，可防止httpclient频繁请求刷留言板；RequestCache为本地缓存配置，默认缓存60秒；
  - 配置文件application.yml内参数repeatRequestParam为重复请求配置参数
  - cacheExpireTime: 60 #本地缓存有效期 单位秒
  - requestNum: 10 #设置访问多少次为频繁请求
  - 即60秒内重复访问一个接口地址超过10次，就将ip加入黑名单
  - 测试频繁请求路径： /testRepeatRequest

- 前端博客美化效果：雪花效果，动态背景线条效果，点击显示文字效果

  - 雪花效果相关配置：
    - resources/static/js/snow.js文件内，有详细注释说明
    - 可配置雪花颜色，大小，密集程度

  - 动态背景线条相关配置：
    - _fragments.html第232行
    - 可配置颜色，透明度

  - 点击显示文字效果相关配置：
    - resources/static/js/click_show_text.js文件内，可自定义随机显示的文字



- 引入天气插件，固定在页面左上角。

- 引入网易云音乐插件，固定在右下角。在_fragments.html文件的id="netEasy-music"元素内可自行替换歌单

- 适配移动端，以上博客美化样式，天气插件与网易云音乐插件在手机端不显示

- 底部footer网站运行计时，在_fragments.html文件createtime( ) JS方法内自行替换建站时间

- 博客分享功能，支持携带首图，博客标题分享，并自定义分享描述

  - 支持微信，微博，QQ好友，QQ空间分享。
  - 相关配置在blog.html内的shareQQ()，shareQQZone()，shareWeiXin()，sinaWeiBo()方法内。

- 博客评论新加表情支持，后续会加入b站相关表情

- 新增博客留言页面

- 留言页面支持邮件回复通知，开启后评论，当评论被回复后会收到一封邮件，邮件支持表情显示
- rabbitmq监听验证码发送情况，并使用redis保存验证码与发送手机号，五分钟内过期；虽然手机验证码功能目前没有具体使用，但也可以了解一下。

