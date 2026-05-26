package com.mindguard.module.assessment.service;

import com.mindguard.common.PageResult;
import com.mindguard.module.assessment.entity.Article;

import java.util.List;

public interface ArticleService {
    PageResult<Article> listArticles(String severityLevel, Integer page, Integer size);
    Article getArticleDetail(Long id);
    List<Article> getRecommendedArticles(Long userId);
    void createArticle(Article article);
    void updateArticle(Article article);
    void deleteArticle(Long id);
}
