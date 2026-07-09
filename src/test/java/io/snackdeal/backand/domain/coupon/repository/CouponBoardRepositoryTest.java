package io.snackdeal.backand.domain.coupon.repository;

import io.snackdeal.backand.domain.coupon.entity.CouponBoard;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("CouponBoardRepository 클래스의")
class CouponBoardRepositoryTest {

    @Autowired
    private CouponBoardRepository couponBoardRepository;

    @Nested
    @DisplayName("Describe: findOpenBoards() 메서드는")
    class Describe_findOpenBoards {

        @Nested
        @DisplayName("Context: 사용자 이벤트 쿠폰보드 목록을 조회하는 경우")
        class Context_with_mixed_boards {

            @Test
            @DisplayName("It: 현재 노출 가능한 쿠폰보드만 조회한다")
            void It_현재_노출_가능한_쿠폰보드만_조회() {
                // given
                LocalDateTime now = LocalDateTime.now();
                CouponBoard openBoard = createBoard("열린 쿠폰보드", true, now.minusDays(1), now.plusDays(7));
                CouponBoard upcomingBoard = createBoard("시작 전 쿠폰보드", true, now.plusDays(1), now.plusDays(7));
                CouponBoard closedBoard = createBoard("종료 쿠폰보드", true, now.minusDays(7), now.minusDays(1));
                CouponBoard inactiveBoard = createBoard("비활성 쿠폰보드", false, now.minusDays(1), now.plusDays(7));
                CouponBoard deletedBoard = createBoard("삭제 쿠폰보드", true, now.minusDays(1), now.plusDays(7));
                ReflectionTestUtils.setField(deletedBoard, "deletedAt", now);

                couponBoardRepository.saveAll(List.of(
                        openBoard,
                        upcomingBoard,
                        closedBoard,
                        inactiveBoard,
                        deletedBoard
                ));

                // when
                List<CouponBoard> result = couponBoardRepository.findOpenBoards(now);

                // then
                assertThat(result).hasSize(1);
                assertThat(result.get(0).getTitle()).isEqualTo("열린 쿠폰보드");
            }
        }
    }

    @Nested
    @DisplayName("Describe: findOpenBoardById() 메서드는")
    class Describe_findOpenBoardById {

        @Nested
        @DisplayName("Context: 특정 이벤트 쿠폰보드 상세를 조회하는 경우")
        class Context_with_open_and_closed_board {

            @Test
            @DisplayName("It: 현재 노출 가능한 쿠폰보드만 id로 조회한다")
            void It_현재_노출_가능한_쿠폰보드만_id로_조회() {
                // given
                LocalDateTime now = LocalDateTime.now();
                CouponBoard openBoard = couponBoardRepository.save(
                        createBoard("열린 쿠폰보드", true, now.minusDays(1), now.plusDays(7))
                );
                CouponBoard closedBoard = couponBoardRepository.save(
                        createBoard("종료 쿠폰보드", true, now.minusDays(7), now.minusDays(1))
                );

                // when
                Optional<CouponBoard> openResult =
                        couponBoardRepository.findOpenBoardById(openBoard.getId(), now);
                Optional<CouponBoard> closedResult =
                        couponBoardRepository.findOpenBoardById(closedBoard.getId(), now);

                // then
                assertThat(openResult).isPresent();
                assertThat(openResult.get().getTitle()).isEqualTo("열린 쿠폰보드");
                assertThat(closedResult).isEmpty();
            }
        }
    }

    private CouponBoard createBoard(String title,
                                    boolean isActive,
                                    LocalDateTime startAt,
                                    LocalDateTime endAt) {
        return CouponBoard.builder()
                .title(title)
                .content("쿠폰보드 내용")
                .thumbnailUrl("https://image.test/board.png")
                .isActive(isActive)
                .startAt(startAt)
                .endAt(endAt)
                .build();
    }
}
