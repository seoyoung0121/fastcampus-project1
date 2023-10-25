package com.fastcampus.projectboard.config;

import com.fastcampus.projectboard.dto.UserAccountDto;
import com.fastcampus.projectboard.dto.security.BoardPrincipal;
import com.fastcampus.projectboard.repository.UserAccountRepository;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth-> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        //위에는 정적 리소스에 대한 시큐리티 무시, 근데 따로 적지마
                        .mvcMatchers(
                                HttpMethod.GET,
                            "/",
                            "/articles",
                            "/articles/search-hashtag"
                        ).permitAll()
                        .anyRequest().authenticated())
                .formLogin().and()
                .logout()
                    .logoutSuccessUrl("/")
                    .and()
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository){
        //인증 데이터 가져오는 서비스
        //여기서 userAccountRepository를 터치하는데 이제 이 안에 인증 데이터가 들어가있지 않으 유저 불러오지 못하면 테스트 실패
        //그래서 jpa레포 테스트 실패..?
        //createdBy에 null이들어가서 실패
        return username -> userAccountRepository
                .findById(username)
                .map(UserAccountDto::from)
                .map(BoardPrincipal::from) //dto를 board principal로 만들어줌
                .orElseThrow(()->new UsernameNotFoundException("유저를 찾을 수 없습니다 - username: "+username));
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}