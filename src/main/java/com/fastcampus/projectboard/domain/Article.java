package com.fastcampus.projectboard.domain;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true)
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
    @Setter @ManyToOne(optional = false) private UserAccount userAccount; // 유저 정보 (ID)
    @Setter @Column(nullable = false) private String title;
    @Setter @Column(nullable = false, length = 10000) private String content;

    @Setter private String hashtag;

    @ToString.Exclude // article comments 엔 또 article 연결되어 있고 순환되기에 끊어야함
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private final Set<ArticleComment> articleComments=new LinkedHashSet<>();

    protected Article(){
    }

    private Article(UserAccount userAccount, String title, String content, String hashtag) {
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }
    public static Article of(UserAccount userAccount, String title, String content, String hashtag) {
        return new Article(userAccount, title, content, hashtag);
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
