package com.fastcampus.projectboard.repository;

import com.fastcampus.projectboard.config.JpaConfig;
import com.fastcampus.projectboard.domain.Article;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import com.fastcampus.projectboard.domain.UserAccount;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;
import java.util.Optional;

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
@Import(JpaRepositoryTest.TestJpaConfig.class)
@DataJpaTest
class JpaRepositoryTest {
    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserAccountRepository userAccountRepository;

    public JpaRepositoryTest( @Autowired ArticleRepository articleRepository,
                              @Autowired ArticleCommentRepository articleCommentRepository,
                              @Autowired UserAccountRepository userAccountRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.userAccountRepository = userAccountRepository;
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
        UserAccount userAccount = userAccountRepository.save(UserAccount.of("newUno", "pw", null, null, null));
        Article article = Article.of(userAccount, "new article", "new content", "#spring");

        //When
        articleRepository.save(article);

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

    @EnableJpaAuditing
    @TestConfiguration //test할때만 config
    public static class TestJpaConfig{
        @Bean
        public AuditorAware<String> auditorAware(){
            return () -> Optional.of("uno");
        }
        //시큐리티에 영향받지 않게끔 만들어 줬다.

    }

}