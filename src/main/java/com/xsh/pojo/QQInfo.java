package com.xsh.pojo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author : xsh
 * @create : 2020-03-15 - 5:35
 * @describe:
 */
@Entity
@Table(name = "t_qquser")
public class QQInfo {
    @Id
    @GeneratedValue
    private Long id;

    private String logintime; //登录时间
    private String openid;  //个人openid
    private String nickname;  //昵称
    private String gender; //性别
    private String year; //生日
    private String cip;  //ip地址
    private String cid;  //地区编号
    private String cname; //所在地
    private String avatar; //头像

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogintime() {
        return logintime;
    }

    public void setLogintime(String logintime) {
        this.logintime = logintime;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCip() {
        return cip;
    }

    public void setCip(String cip) {
        this.cip = cip;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Override
    public String toString() {
        return "QQInfo{" +
                "logintime='" + logintime + '\'' +
                ", openid='" + openid + '\'' +
                ", nickname='" + nickname + '\'' +
                ", gender='" + gender + '\'' +
                ", year='" + year + '\'' +
                ", cip='" + cip + '\'' +
                ", cid='" + cid + '\'' +
                ", cname='" + cname + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
