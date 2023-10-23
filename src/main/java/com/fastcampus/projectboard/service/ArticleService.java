package com.fastcampus.projectboard.service;

import com.fastcampus.projectboard.domain.Article;
import com.fastcampus.projectboard.domain.constant.SearchType;
import com.fastcampus.projectboard.dto.ArticleDto;
import com.fastcampus.projectboard.dto.ArticleWithCommentsDto;
import com.fastcampus.projectboard.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fastcampus.projectboard.domain.UserAccount;
import com.fastcampus.projectboard.domain.constant.SearchType;
import com.fastcampus.projectboard.repository.UserAccountRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j //로깅하는 롬복의 애노테이션
@RequiredArgsConstructor
@Service
public class ArticleService {
    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        if(searchKeyword==null||searchKeyword.isBlank()){
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }
        //pageable이랑 map 관해서 알아보기
        return switch (searchType){
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword,pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword,pageable).map(ArticleDto::from);
            case ID -> articleRepository.findByUserAccount_UserIdContaining(searchKeyword,pageable).map(ArticleDto::from);
            case NICKNAME -> articleRepository.findByUserAccount_NicknameContaining(searchKeyword,pageable).map(ArticleDto::from);
            case HASHTAG -> articleRepository.findByHashtagContaining("#"+searchKeyword,pageable).map(ArticleDto::from);
        };
    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticleWithComments(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsDto::from)
                .orElseThrow(()->new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }
    @Transactional(readOnly = true)
    public ArticleDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 없습니다 - articleId: " + articleId));
    }

    public void saveArticle(ArticleDto dto) {
        UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().userId());
        articleRepository.save(dto.toEntity(userAccount));
    }

    public void updateArticle(Long articleId, ArticleDto dto) {
        try{
            Article article = articleRepository.getReferenceById(articleId);
        //getReferenceById는 findById랑 비슷한데 find는엔티티 조회 쿼리 날려 데이터 필요 안해도 select쿼리 날라감
        //db 가서 꺼내오는거지.. 쿼리 안보내고 레퍼런스만!
        if(dto.title()!=null){
            article.setTitle(dto.title());
        }
        //record는 getter setter 이미 만들어져있어서 .title해도 나옴!
        if(dto.content()!=null){
            article.setContent(dto.content());
        }
        article.setHashtag(dto.hashtag());
        //앤 null이어도 되니 if문 안써도됨
        //articleRepository.save(article); 필요 없음 transactional에 의해 트랜지션이 묶여있어
        //끝날때 article 변한거 감지해내고 쿼리 날려서 실행됨
        //근데 dto인데도 감지하나..? 코드 다시 보자 -> 웅 dto 아니고 아티클을 수정하는 거네
        } catch(EntityNotFoundException e){
            log.warn("업데이트 실패, 게시글 찾을 수 없음 - dto: {}", dto);
            //string interpolation 이 +보다 나음, 실행 안할 때 메모리 부담 덜음
        }


    }

    public void deleteArticle(long articleId) {
        articleRepository.deleteById(articleId);
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticlesViaHashtag(String hashtag, Pageable pageable) {
        if(hashtag==null||hashtag.isBlank()){
            return Page.empty(pageable);
        }
        return articleRepository.findByHashtagContaining(hashtag, pageable).map(ArticleDto::from);
    }

    public long getArticleCount() {
        return articleRepository.count();
    }

    public List<String> getHashtags() {
        return articleRepository.findAllDistinctHashtags();
    }
}
