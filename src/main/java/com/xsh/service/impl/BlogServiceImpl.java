package com.xsh.service.impl;

import com.xsh.dao.BlogRepository;
import com.xsh.handler.NotFoundException;
import com.xsh.pojo.Blog;
import com.xsh.pojo.Type;
import com.xsh.service.BlogService;
import com.xsh.util.MarkdownUtils;
import com.xsh.util.MyBeanUtils;
import com.xsh.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * @author : xsh
 * @create : 2020-02-12 - 1:01
 * @describe:
 */
@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    /**
     * 根据id获取到指定的blog
     * @param id
     * @return
     */
    @Override
    public Blog getBlog(Long id) {
        return blogRepository.getById(id);
    }

    @Transactional
    @Override
    public Blog getAndConvert(Long id) {

        Blog blog = blogRepository.getById(id);
        if(blog==null){
            throw new NotFoundException("该博客不存在");
        }
        Blog b=new Blog();
        BeanUtils.copyProperties(blog,b);
        String content=b.getContent();
        //管理员在后台编辑的Markdown文章需要转换为HTML用于前台展示
        b.setContent(MarkdownUtils.markdownToHtmlExtensions(content));
        //浏览数加一
        blogRepository.updateViews(id);

        return b;
    }


    /*根据标题，分类，是否被推荐动态，是否是草稿动态查询博客*/
    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQuery blog) {

        return blogRepository.findAll(new Specification<Blog>() {
            /**
             *第一个参数Root指定查询的对象，
             *第二个参数CriteriaQuery为查询的条件容器，
             *第三个参数criteriaBuilder设置具体某一条件的表达式
             */
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates=new ArrayList<>();
                //predicates.add()方法，将查询条件加入一个list中；cb.方法第一个参数指定查询对应的实体类，第二个参数指定对应前端所传递的参数
                if(!"".equals(blog.getTitle()) && blog.getTitle()!=null){
                    //判断查询条件之一:根据博客标题查询
                    predicates.add(cb.like(root.get("title"),"%"+blog.getTitle()+"%"));
                }
                //判断查询条件之二:根据博客分类查询
                if(blog.getTypeId()!=null){
                    predicates.add(cb.equal(root.<Type>get("type").get("id"),blog.getTypeId()));
                }
                //判断查询条件之三:根据博客是否被推荐查询
                if(blog.isRecommend()){
                    predicates.add(cb.equal(root.<Boolean>get("recommend"),blog.isRecommend()));
                }
                //判断查询条件之四:根据博客是否已发布查询，过滤掉状态为草稿的博客
                if(blog.isPublished()){
                    predicates.add(cb.equal(root.<Boolean>get("published"),blog.isPublished()));
                }
                //判断查询条件之五:根据博客是否是草稿，实现后台查询草稿博客功能
                if(blog.isDraft()){
                    predicates.add(cb.equal(root.<Boolean>get("published"),!blog.isDraft()));
                }
                /*cq的where方法，相当于sql中的where,接收一个条件数组，所以要将list转为数组*/
                cq.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        },pageable);
    }


    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Override
    public Page<Blog> listBlog(Long tagId, Pageable pageable, BlogQuery blog) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates=new ArrayList<>();
                if(blog.isPublished()){
                    //根据博客是否已发布查询，过滤掉状态为草稿的博客
                    predicates.add(cb.equal(root.<Boolean>get("published"),blog.isPublished()));
                }
                Join join=root.join("tags");
                predicates.add(cb.equal(join.get("id"),tagId));
                cq.where(predicates.toArray(new Predicate[predicates.size()]));
                return null;
            }
        },pageable);
    }

    /**
     * 根据查询条件和对应的描述找到blog
     * @param query
     * @param pageable
     * @return
     */
    @Override
    public Page<Blog> listBlog(String query, Pageable pageable) {
        return blogRepository.findByQuery(query,pageable);
    }

    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
      //更新时间降序
        Sort sort=Sort.by(Sort.Direction.DESC,"updateTime");

        Pageable pageable=PageRequest.of(0,size,sort);
        return blogRepository.findTop(pageable);
    }

    /**
     * 查询最新blog
     * @param size
     * @return
     */
    @Override
    public List<Blog> listNewBlog(Integer size) {
        //Sort sort=new Sort(Sort.Direction.DESC,"updateTime");
        Sort sort=Sort.by(Sort.Direction.DESC,"updateTime");
       // Pageable pageable=new PageRequest(0,size,sort);
        Pageable pageable=PageRequest.of(0,size,sort);
        return blogRepository.findNewBlog(pageable);
    }

    @Override
    public Map<String, List<Blog>> archiveBlog() {
        List<String> years = blogRepository.findGroupYear();
        /*为了保证归档年份按顺序显示，使用LinkedHashMap存放*/
        //Map<String, List<Blog>> map = new HashMap<>();
        //有序性
        Map<String, List<Blog>> map = new LinkedHashMap<>();
        for (String year : years) {
            map.put(year, blogRepository.findByYear(year));
        }
        return map;
    }

    @Transactional
    @Override
    public Blog saveBlog(Blog blog) {
        //如果博客id为空则为新增，有id则为修改
        if(blog.getId()==null){
            blog.setCreateTime(new Date()); //保存博客创建时间(当前时间)
            blog.setUpdateTime(new Date());
            blog.setViews(0); //博客浏览量初始化
        }else {
            blog.setUpdateTime(new Date());
        }
        return blogRepository.save(blog);
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
//        Blog b=blogRepository.findOne(id);
        Blog b = blogRepository.getById(id);
        if(b==null){
            throw new NotFoundException("该博客不存在");
        }
        //将blog的值给b,MyBeanUtils工具类过滤掉属性值为空的属性,这样当属性值为空则保留原有值，执行更新操作
        BeanUtils.copyProperties(blog,b, MyBeanUtils.getNullPropertyNames(blog));
        b.setUpdateTime(new Date());
        return blogRepository.save(b);
    }

    @Transactional
    @Override
    public void deleteBlog(Long id) {
        //blogRepository.delete(id);
        blogRepository.deleteById(id);
    }

    @Override
    public Long countBlog() {
        return blogRepository.countBlog();
    }
}
