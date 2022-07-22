package com.xsh.dao;

import com.xsh.pojo.Tag;
import org.apache.ibatis.annotations.Select;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author : xsh
 * @create : 2020-02-12 - 0:05
 * @describe:
 */
public interface TagRepository extends JpaRepository<Tag,Long> {

    Tag findByName(String name);

    @Query("select t from Tag t")
    List<Tag> findTop(Pageable pageable);

    /*@Select("select id,name from t_tag where id in <foreach item='item' index='index' collection='list' open='(' separator=',' close=')'> #{item} </foreach> ")
    List<Tag> findList(List<Long> list);*/

    List<Tag> findAllByIdIn(List<Long> list);






}
