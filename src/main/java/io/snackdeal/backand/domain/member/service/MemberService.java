package io.snackdeal.backand.domain.member.service;

import io.snackdeal.backand.global.config.code.ResponseCode;
import io.snackdeal.backand.global.exception.BusinessException;
import io.snackdeal.backand.api.user.member.dto.JoinRequest;
import io.snackdeal.backand.api.user.member.dto.MemberDescription;
import io.snackdeal.backand.api.user.member.dto.MemberStatusUpdateRequest;
import io.snackdeal.backand.api.user.member.dto.MemberUpdateRequest;
import io.snackdeal.backand.domain.member.entity.Member;
import io.snackdeal.backand.domain.member.entity.MemberRole;
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

    @Transactional
    public MemberDescription updateProfile(String email, MemberUpdateRequest request) {
        Member member = findByEmail(email);
        String encodedPassword = (request.password() != null) ? passwordEncoder.encode(request.password()) : null;
        member.updateProfile(request.phone(), encodedPassword);
        return MemberMapper.toDescription(member);
    }

    public Page<MemberDescription> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(MemberMapper::toDescription);
    }

    public MemberDescription findById(Long id) {
        Member member = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.MEMBER_NOT_FOUND));
        return MemberMapper.toDescription(member);
    }

    @Transactional
    public MemberDescription changeStatus(Long id, MemberStatusUpdateRequest request) {
        Member member = repository.findById(id)
                .orElseThrow(() -> new BusinessException(ResponseCode.MEMBER_NOT_FOUND));
        member.changeStatus(request.status());
        return MemberMapper.toDescription(member);
    }
}
