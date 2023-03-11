package com.xsh.dao;

import com.xsh.pojo.Gitee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface GiteeRepository extends JpaRepository<Gitee,Long> {


    @Query("select u from Gitee u where u.giteeId = ?1")
    Gitee findUserByGiteeId(Integer giteeId);

    @Transactional
    @Modifying
    @Query("update Gitee u set u.avatar = ?1 where u.id = ?2")
    void updateAvatar(String avatar, Integer id);

    @Transactional
    @Modifying
    @Query("update Gitee g set g.cip = ?1,g.cid = ?2,g.cname = ?3 where g.giteeId = ?4")
    void addIpaddress(String cip,String cid,String cname,Integer giteeId);
}
