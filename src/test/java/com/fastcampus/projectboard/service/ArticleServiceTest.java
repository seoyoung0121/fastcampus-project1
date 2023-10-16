package com.fastcampus.projectboard.service;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.type.SearchType;
import com.fastcampus.projectboard.dto.ArticleDto;
import com.fastcampus.projectboard.dto.ArticleUpdateDto;
import com.fastcampus.projectboard.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
@DisplayName("비즈니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
    @InjectMocks private ArticleService sut;
    @Mock private ArticleRepository articleRepository;

    @DisplayName("게시글 검색 시 게시글 리스트 반환")
    @Test
    void givenSearchParameters_whenSearchingArticles_thenReturnsArticleList(){
        //given

        //when
        Page<ArticleDto> articles = sut.searchArticles(SearchType.TITLE, "search keyword");  //제목, 본문, 아이디 , 닉네임, 해시태그

        //then
        assertThat(articles).isNotNull();

    }

    @DisplayName("게시글 조회 시 게시글 반환")
    @Test
    void givenArticleId_whenSearchingArticle_thenReturnsArticle(){
        //given

        //when
        ArticleDto articles=sut.searchArticle(1L);  //제목, 본문, 아이디 , 닉네임, 해시태그

        //then
        assertThat(articles).isNotNull();

    }

    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다.")
    @Test
    void givenArticleInfo_whenSavingArticle_thenSavesArticle(){
        //given
        BDDMockito.given(articleRepository.save(BDDMockito.any(Article.class))).willReturn(null);

        //when
        sut.saveArticle(ArticleDto.of("title","content", "#java", LocalDateTime.now(), "Seoyoung"));

        //then
        BDDMockito.then(articleRepository).should().save((BDDMockito.any(Article.class)));
    }

    @DisplayName("게시글 ID와 수정 정보를 입력하면, 게시글을 수정한다.")
    @Test
    void givenArticleIdAndModifiedInfo_whenUpdatingArticle_thenUpdatesArticle(){
        //given
        BDDMockito.given(articleRepository.save(BDDMockito.any(Article.class))).willReturn(null);

        //when
        sut.updateArticle(1L, ArticleUpdateDto.of("title","content", "#java"));

        //then
        BDDMockito.then(articleRepository).should().save((BDDMockito.any(Article.class)));
    }

    @DisplayName("게시글 ID를 입력하면, 게시글을 삭제한다.")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeletesArticle(){
        //given
        BDDMockito.willDoNothing().given(articleRepository).delete(BDDMockito.any(Article.class));

        //when
        sut.deleteArticle(1L);

        //then
        BDDMockito.then(articleRepository).should().delete((BDDMockito.any(Article.class)));
    }


}