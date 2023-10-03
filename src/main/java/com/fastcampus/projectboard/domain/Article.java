package com.fastcampus.projectboard.domain;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString
@Table(indexes = { //검색 넣을 곳
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class Article extends AuditingFields{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //자동으로 값 안만들어주게
    private Long id; //primary key가 된다.

    @Setter @Column(nullable = false) private String title;
    @Setter @Column(nullable = false, length = 10000) private String content;

    @Setter private String hashtag;

    @ToString.Exclude // article comments 엔 또 article 연결되어 있고 순환되기에 끊어야함
    @OrderBy("id")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private final Set<ArticleComment> articleComments=new LinkedHashSet<>();

    protected Article(){
    }

    private Article(String title, String content, String hashtag) {
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }
    public static Article of(String title, String content, String hashtag) {
        //도메인 아티클을 생성하고자 할 때는 이러한 값 필요하다는 가이드
        //객체 생성 메서드를 만드는 static factory method임
        return new Article(title, content, hashtag);
    }

    //동등성 검증
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article article)) return false; //pattern variable 방식
        return id != null && id.equals(article.id);
        //db에 아직 영속화 되지 않은 entity는 모든 동등성 검사 탈락한다.
        //왜냐면 id가 부여되지 않은게 영속화 x라는 뜻
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
