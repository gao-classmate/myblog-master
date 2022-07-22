//package com.xsh;
//
//import com.xsh.dao.TagRepository;
//import com.xsh.pojo.Blog;
//import com.xsh.service.BlogService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import javax.annotation.Resource;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class MyblogApplicationTests {
//    @Autowired
//    private TagRepository tagRepository;
//
//    @Resource
//    private BlogService blogService;
//
//   /* for(Map.Entry<String, String> entry:map.entrySet()){
//
//        System.out.println(entry.getKey()+"--->"+entry.getValue());
//
//    }*/
//    @Test
//    public void contextLoads() {
//        /*List<Long> list=new ArrayList<>();
//        list.add(1L);
//        System.out.println(tagRepository.findAllByIdIn(list));*/
//        Map<String, List<Blog>> stringListMap = blogService.archiveBlog();
//        for(Map.Entry<String,List<Blog>> entry:stringListMap.entrySet()){
//            System.out.println(entry.getKey()+"------>"+entry.getValue());
//        }
//
//
//    }
//
//}
