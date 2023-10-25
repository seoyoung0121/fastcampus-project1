package com.fastcampus.projectboard.config;

import com.fastcampus.projectboard.dto.security.BoardPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing
@Configuration
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorAware(){
        //SecurityContextHolder는 인증 정보를 모두 가지고 있는 클래스이다.
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated) //인증 됐는지 확인하고 principal 꺼내옴
                .map(Authentication::getPrincipal)
                .map(BoardPrincipal.class::cast) //boardprincipal로 타입캐스팅(타입을 바꿔줌)
                .map(BoardPrincipal::getUsername);
        //누가 로그인 했는지 뽑아 내게 됨
        //이제 인증 기능을 구현했으니 사용자 정보를 넣을 수 있다!!

    }
}
