package com.fastcampus.projectboard.repository;

import com.fastcampus.projectboard.config.JpaConfig;
import com.fastcampus.projectboard.domain.Article;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
// @ActiveProfiles("testdb") 이거하고 Application.yaml에
/*spring:
  config.activate.on-profile: testdb
  datasource:
    url: jdbc:h2:mem:board;mode=mysql
    driverClassName: org.h2.Driver
  sql.init.mode: always
  test.database.replace: none //자동으로 임의의 testdb 불러오지않고 설정한 db 불러옴
이러면 h2에서 mysql과 가까운 환경으로 꾸밀 수 있..? */
@DisplayName("Jpa 연결 테스트")
@Import(JpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;

    public JpaRepositoryTest( @Autowired ArticleRepository articleRepository,
                             @Autowired ArticleCommentRepository articleCommentRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
    }

    @DisplayName("select 테스트")
    @Test
    void givenTestData_whenSelecting_thenWorksFine(){
        //Given

        //When
        List<Article> articles=articleRepository.findAll();
        //Then
        Assertions.assertThat(articles)
                .isNotNull()
                .hasSize(123);
    }

    @DisplayName("insert 테스트")
    @Test
    void givenTestData_whenInserting_thenWorksFine(){
        //Given
        long previousCount=articleRepository.count();

        //When
        Article savedArticle=articleRepository.save(Article.of("new article", "new comment", "#spring"));

        //Then
        Assertions.assertThat(articleRepository.count()).isEqualTo(previousCount+1);
    }

    @DisplayName("update 테스트")
    @Test
    void givenTestData_whenUpdating_thenWorksFine(){
        //Given
        Article article=articleRepository.findById(1L).orElseThrow();
        String updatedHashtag="#springboot";
        article.setHashtag(updatedHashtag);

        //When
        Article savedArticle=articleRepository.saveAndFlush(article);
        //test 자체를 롤백해서? 쿼리 생성 자체를 안해줌
        //아니면 save 하고 articleRepository.flush(); 해도 됨
        //이러면 쿼리를 생성해주지만 롤백됨

        //Then
        Assertions.assertThat(savedArticle).hasFieldOrPropertyWithValue("hashtag", updatedHashtag);
    }

    @DisplayName("delete 테스트")
    @Test
    void givenTestData_whenDeleting_thenWorksFine(){ //연관 댓글도 사라져야함
        //Given
        Article article=articleRepository.findById(1L).orElseThrow();
        long previousArticleCount=articleRepository.count();
        long previousArticleCommentCount=articleCommentRepository.count();
        int deletedCommentSize=article.getArticleComments().size();

        //When
        articleRepository.delete(article);

        //Then
        Assertions.assertThat(articleRepository.count()).isEqualTo(previousArticleCount-1);
        Assertions.assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount-deletedCommentSize);
    }

}