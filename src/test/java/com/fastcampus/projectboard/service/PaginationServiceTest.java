package com.fastcampus.projectboard.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.List;
import java.util.stream.Stream;

@DisplayName("비즈니스 로직 - 페이지네이션")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE ,classes = PaginationService.class) //스프링 부트 테스트의 무게를 줄여줌
class PaginationServiceTest {
    //특별한 의존성 없기에 private final Paginationservice sut=new 이런식으로 해도 되지만 springboottest를 쓴댄다.
    private final PaginationService sut;

    PaginationServiceTest(@Autowired PaginationService paginationService) {
        this.sut = paginationService;
    }

    @DisplayName("현재 페이지와 총 페이지 수를 주면 페이징 바 리스트를 만들어준다.")
    @MethodSource
    @ParameterizedTest (name = "[{index}] 현재 페이지: {0}, 총 페이지: {1} => {2}")
        //값을 연속적으로 주입해서 동일한 메소드 여러번 테스트해 볼 수 있다.
    //저렇게하면 테스트 이름 포맷이 이뻐짐
    void givenCurrentPageNumberAndTotalPages_whenCalculating_thenReturnPaginationBarNumber(int currentPageNumber, int totalPages, List<Integer> expected){
        //expected는 검증 하고싶은 값
        //given

        //when
        List<Integer> actual=sut.getPaginationBarNumbers(currentPageNumber, totalPages);

        //then
        assertThat(actual).isEqualTo(expected);

    }

    static Stream<Arguments> givenCurrentPageNumberAndTotalPages_whenCalculating_thenReturnPaginationBarNumber(){
        //이렇게하면 변수에 들어감..?
        return Stream.of(
                arguments(0, 13, List.of(0,1,2,3,4)),
                arguments(1, 13, List.of(0,1,2,3,4)),
                arguments(2, 13, List.of(0,1,2,3,4)),
                arguments(3, 13, List.of(1,2,3,4,5)),
                arguments(4, 13, List.of(2,3,4,5,6)),
                arguments(5, 13, List.of(3,4,5,6,7)),
                arguments(6, 13, List.of(4,5,6,7,8)),
                arguments(10, 13, List.of(8,9,10,11,12)),
                arguments(11, 13, List.of(9,10,11,12)),
                arguments(12, 13, List.of(10,11,12))
                );
    }

    @DisplayName("현재 설정되어 있는 페이지네이션 바의 길이를 알려준다. ")
    @Test
    void givenNothing_whenCalling_thenReturnsCurrentBarLength() {
        // Given

        // When
        int barLength = sut.currentBarLength();

        // Then
        assertThat(barLength).isEqualTo(5);
    }
    //스펙의 명세를 위한 테스트 코드.. 이거보면 개발자가 5구나를 알 수 있
}