package com.springboot_master.blog.dao;

import com.springboot_master.blog.dto.ArticleSearchDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


/**
 * elasticsearch
 *
 * @author yezhiqiu
 * @date 2021/08/10
 */
@Repository
public interface ElasticsearchDao extends ElasticsearchRepository<ArticleSearchDTO,Integer> {
}
