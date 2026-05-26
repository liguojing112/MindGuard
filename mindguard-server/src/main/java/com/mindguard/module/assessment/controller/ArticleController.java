package com.mindguard.module.assessment.controller;

import com.mindguard.common.PageResult;
import com.mindguard.common.Result;
import com.mindguard.module.assessment.entity.Article;
import com.mindguard.module.assessment.service.ArticleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping
    public Result<PageResult<Article>> listArticles(@RequestParam(required = false) String severityLevel,
                                                     @RequestParam(defaultValue = "1") Integer page,
                                                     @RequestParam(defaultValue = "10") Integer size) {
        return Result.ok(articleService.listArticles(severityLevel, page, size));
    }

    @GetMapping("/recommended")
    public Result<List<Article>> getRecommendedArticles(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return Result.ok(articleService.getRecommendedArticles(userId));
    }

    @GetMapping("/{id}")
    public Result<Article> getArticleDetail(@PathVariable Long id) {
        return Result.ok(articleService.getArticleDetail(id));
    }

    @PostMapping
    public Result<Void> createArticle(@RequestBody Article article) {
        articleService.createArticle(article);
        return Result.ok();
    }

    @PutMapping("/{id}")
    public Result<Void> updateArticle(@PathVariable Long id, @RequestBody Article article) {
        article.setId(id);
        articleService.updateArticle(article);
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return Result.ok();
    }
}
