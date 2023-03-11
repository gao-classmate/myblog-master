# 修复

- 修复前台显示草稿内容的bug
- 修复博客详情页面，手机扫码阅读博客功能不能跳到指定博客的bug
- 修复archives归档页面，博客超出一定数量(大于15)时，页面显示错乱，只显示15篇博客，后面的博客显示不全的bug
- 修复archives归档页面，根据年份归档，当存在三个年份归档，如分别存在2018年 2019年 2020年份的博客，实际排序为2019 2018 2020，顺序排序错误的bug
- 修复index页面，最新动态未按发布博客时间最近排序的bug
- 修复博客评论区，子评论在第三层时，此时回复会追加第二层的回复的bug（重点）

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




- 底部footer网站运行计时，在_fragments.html文件createtime( ) JS方法内自行替换建站时间

- 博客分享功能，支持携带首图，博客标题分享，并自定义分享描述

  - 支持微信，微博，QQ好友，QQ空间分享。
  - 相关配置在blog.html内的shareQQ()，shareQQZone()，shareWeiXin()，sinaWeiBo()方法内。

- 博客评论新加表情支持，后续会加入b站相关表情

- 新增博客留言页面

- 留言页面支持邮件回复通知，开启后评论，当评论被回复后会收到一封邮件，邮件支持表情显示


**注：邮件回复功能需先开启邮箱POP3/SMTP服务**

支持163邮箱和QQ邮箱，application.yml文件内有详细注释自行替换

- 新增第三方登录——QQ登录方式（申请失败）

  如需此功能，需先申请QQ互联开发者，申请通过后会得到三个参数：appid，appkey，回调地址。


- 新增第三方登录——Github登录方式（后来改成Gitee，github申请太慢）

  如需此功能，需先进入github账号，点击右上角头像选择setting，进入后选择底部Developer settings再点击New GitHub App,填写相关资料后无需审核就能直接使用；如果不知道怎么填可以留言或者百度。

- rabbitmq监听验证码发送情况（没啥用，场景不符合）
