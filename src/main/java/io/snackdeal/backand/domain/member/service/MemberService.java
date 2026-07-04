package io.snackdeal.backand.domain.member.service;

import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import io.snackdeal.backand.api.user.member.dto.JoinRequest;
import io.snackdeal.backand.api.user.member.dto.MemberDescription;
import io.snackdeal.backand.api.user.member.dto.MemberStatusResponse;
import io.snackdeal.backand.api.user.member.dto.MemberStatusUpdateRequest;
import io.snackdeal.backand.api.user.member.dto.MemberUpdateRequest;
import io.snackdeal.backand.domain.member.entity.Member;
import io.snackdeal.backand.domain.member.entity.MemberRole;
import io.snackdeal.backand.domain.member.entity.MemberStatus;
import io.snackdeal.backand.domain.member.mapper.MemberMapper;
import io.snackdeal.backand.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository repository;
    private final EmailVerificationService emailVerificationService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberDescription join(JoinRequest request) {
        String verifiedEmail = emailVerificationService.getVerifiedEmail(request.verificationToken());
        if (!verifiedEmail.equals(request.email())) {
            throw new BusinessException(ResponseCode.EMAIL_TOKEN_INVALID);
        }

        if (repository.findByEmail(request.email()).isPresent()) {
            throw new BusinessException(ResponseCode.DUPLICATE_EMAIL);
        }

        Member member = Member.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .name(request.name())
                .birth(request.birth())
                .gender(request.gender())
                .phone(request.phone())
                .role(MemberRole.USER)
                .build();

        try {
            Member saved = repository.saveAndFlush(member);
            return MemberMapper.toDescription(saved);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException(ResponseCode.DUPLICATE_EMAIL);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다: " + email));
        return MemberMapper.toDetails(member);
    }

    public Member findByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 회원입니다: " + email));
    }

    public MemberDescription findDescriptionByEmail(String email) {
        return MemberMapper.toDescription(findByEmail(email));
    }

    /*
     * 내 정보 수정 (휴대폰 / 비밀번호).
     * 비밀번호는 민감 정보이므로, "새 비밀번호"를 바꾸려면 반드시 "현재 비밀번호"를 함께 받아 본인 확인을 한다.
     *  - 비밀번호를 바꾸지 않는 경우(request.password()==null): 현재 비밀번호 검증 없이 휴대폰만 수정
     *  - 비밀번호를 바꾸는 경우: 현재 비밀번호가 없거나 틀리면 401(INVALID_PASSWORD)
     * @Transactional 영속 상태의 member 를 수정하므로, 별도 save 없이 변경 감지(dirty checking)로 반영된다.
     */
    @Transactional
    public MemberDescription updateProfile(String email, MemberUpdateRequest request) {
        Member member = findByEmail(email);

        String encodedPassword = null;
        if (request.password() != null) {
            // 비밀번호 변경 시 현재 비밀번호 검증 (본인 확인)
            if (request.currentPassword() == null
                    || !passwordEncoder.matches(request.currentPassword(), member.getPassword())) {
                throw new BusinessException(ResponseCode.INVALID_PASSWORD);
            }
            // DB 에는 평문이 아닌 암호화된 값만 저장한다
            encodedPassword = passwordEncoder.encode(request.password());
        }

        // phone/password 중 null 인 항목은 엔티티 쪽에서 건너뛴다 (부분 수정)
        member.updateProfile(request.phone(), encodedPassword);
        return MemberMapper.toDescription(member);
    }

    /*
     * 관리자 회원 리스트 조회 (검색 + 필터 + 페이징).
     * keyword: 이메일/이름 부분검색, status: 상태 필터. 둘 다 선택값이라 null 이면 해당 조건은 무시된다.
     * 빈 문자열("")도 "조건 없음"으로 보기 위해 null 로 정규화한 뒤 리포지토리에 넘긴다.
     */
    public Page<MemberDescription> search(String keyword, MemberStatus status, Pageable pageable) {
        String normalizedKeyword = (keyword != null && !keyword.isBlank()) ? keyword : null;
        return repository.search(normalizedKeyword, status, pageable).map(MemberMapper::toDescription);
    }

    public MemberDescription findById(Long id) {
        Member member = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.MEMBER_NOT_FOUND));
        return MemberMapper.toDescription(member);
    }

    /*
     * 관리자의 회원 상태 변경 (ACTIVE / INACTIVE / DELETED).
     * 하드 삭제하지 않고 상태값 + deleted_at 으로 관리하여 주문/문의 이력을 보존한다.
     * 방어 규칙:
     *  1) 본인 계정은 변경 불가 → 403 (관리자가 실수로 자기 계정을 잠그는 것을 방지)
     *  2) 이미 탈퇴(DELETED)한 회원은 되돌릴 수 없음(터미널 상태) → 422
     *  3) DELETED 로 바꾸면 RefreshToken 을 즉시 삭제해 로그인 세션을 무효화한다
     *     (로그인 API 에서도 DELETED 계정은 차단 → AuthService.login 참고)
     * @param adminId 요청한 관리자 본인 id (본인 계정 변경 차단 비교용)
     */
    @Transactional
    public MemberStatusResponse changeStatus(Long id, MemberStatusUpdateRequest request, Long adminId) {
        Member member = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.MEMBER_NOT_FOUND));

        // 1) 본인 계정 상태 변경 차단
        if (member.getId().equals(adminId)) {
            throw new BusinessException(ResponseCode.SELF_STATUS_CHANGE_FORBIDDEN);
        }

        // 2) 탈퇴한 회원은 되돌릴 수 없음 (DELETED 는 최종 상태)
        if (member.getStatus() == MemberStatus.DELETED) {
            throw new BusinessException(ResponseCode.INVALID_MEMBER_STATUS_TRANSITION);
        }

        member.changeStatus(request.status());

        // 3) 탈퇴 처리 시 토큰(세션) 즉시 무효화
        if (request.status() == MemberStatus.DELETED) {
            refreshTokenService.delete(member.getEmail());
        }

        return new MemberStatusResponse(
                member.getId(),
                member.getEmail(),
                member.getStatus(),
                member.getUpdatedAt()
        );
    }
}
