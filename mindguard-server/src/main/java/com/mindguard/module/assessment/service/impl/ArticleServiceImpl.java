package com.mindguard.module.assessment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mindguard.common.BusinessException;
import com.mindguard.common.PageResult;
import com.mindguard.module.assessment.entity.Article;
import com.mindguard.module.assessment.entity.AssessmentResult;
import com.mindguard.module.assessment.mapper.ArticleMapper;
import com.mindguard.module.assessment.mapper.AssessmentResultMapper;
import com.mindguard.module.assessment.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;
    private final AssessmentResultMapper assessmentResultMapper;

    @Override
    public PageResult<Article> listArticles(String severityLevel, Integer page, Integer size) {
        Page<Article> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<Article>()
                .eq(Article::getIsActive, 1)
                .orderByDesc(Article::getCreatedAt);
        if (severityLevel != null && !severityLevel.isEmpty()) {
            wrapper.eq(Article::getSeverityLevel, severityLevel);
        }
        Page<Article> result = articleMapper.selectPage(pageObj, wrapper);
        return PageResult.of(result);
    }

    @Override
    @Transactional
    public Article getArticleDetail(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        article.setViewCount((article.getViewCount() == null ? 0 : article.getViewCount()) + 1);
        articleMapper.updateById(article);
        return article;
    }

    @Override
    public List<Article> getRecommendedArticles(Long userId) {
        AssessmentResult latestResult = assessmentResultMapper.selectOne(
                new LambdaQueryWrapper<AssessmentResult>()
                        .eq(AssessmentResult::getUserId, userId)
                        .orderByDesc(AssessmentResult::getCreatedAt)
                        .last("LIMIT 1"));

        if (latestResult == null) {
            return Collections.emptyList();
        }

        return articleMapper.selectList(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getIsActive, 1)
                        .eq(Article::getSeverityLevel, latestResult.getSeverityLevel())
                        .orderByDesc(Article::getViewCount)
                        .last("LIMIT 5"));
    }

    @Override
    @Transactional
    public void createArticle(Article article) {
        if (article.getType() == null || article.getType().isEmpty()) {
            article.setType("通用");
        }
        article.setViewCount(0);
        article.setIsActive(1);
        articleMapper.insert(article);
    }

    @Override
    @Transactional
    public void updateArticle(Article article) {
        Article existing = articleMapper.selectById(article.getId());
        if (existing == null) {
            throw new BusinessException("文章不存在");
        }
        articleMapper.updateById(article);
    }

    @Override
    @Transactional
    public void deleteArticle(Long id) {
        Article article = articleMapper.selectById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }
        articleMapper.deleteById(id);
    }
}
