package turtleMart.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turtleMart.global.exception.BadRequestException;
import turtleMart.global.exception.ErrorCode;
import turtleMart.global.exception.NotFoundException;
import turtleMart.member.dto.request.EmailLoginRequest;
import turtleMart.member.dto.request.PhoneNumberLoginRequest;
import turtleMart.member.dto.request.SignupRequest;
import turtleMart.member.dto.response.TokenResponse;
import turtleMart.member.entity.Member;
import turtleMart.member.repository.MemberRepository;
import turtleMart.security.JwtUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService blacklistService;

    /**
     * 회원가입
     */
    @Transactional
    public TokenResponse signup(SignupRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new BadRequestException(ErrorCode.EMAIL_ALREADY_EXIST);
        }
        Member member = Member.of(request, passwordEncoder.encode(request.password()));
        memberRepository.save(member);
        String token = createToken(member);
        return TokenResponse.from("회원가입이 완료되었습니다.", token);
    }

    /**
     * 이메일 로그인
     */
    @Transactional(readOnly = true)
    public TokenResponse emailLogin(EmailLoginRequest request) {
        Member member = memberRepository.findMemberByEmail(request.email())
                .orElseThrow(() -> new NotFoundException(ErrorCode.EMAIL_NOT_FOUND));
        validPassword(request.password(), member);
        String token = createToken(member);
        return TokenResponse.from("로그인이 완료되었습니다.", token);
    }

    /**
     * 휴대폰 번호 로그인
     */
    @Transactional(readOnly = true)
    public TokenResponse phoneNumberLogin(PhoneNumberLoginRequest request) {
        Member member = memberRepository.findMemberByPhoneNumber(request.phoneNumber())
                .orElseThrow(() -> new NotFoundException(ErrorCode.PHONE_NUMBER_NOT_FOUND));
        validPassword(request.password(), member);
        String token = createToken(member);
        return TokenResponse.from("로그인이 완료되었습니다.", token);
    }

    /**
     * 로그아웃
     */
    public String logout(String authorizationHeader) {
        String token = jwtUtil.removePrefix(authorizationHeader);
        long expiration = jwtUtil.extractExpiration(token).getTime();
        blacklistService.blacklistToken(token, expiration);
        return "로그아웃이 완료되었습니다.";
    }

    private String createToken(Member member) {
        String token = jwtUtil.createToken(member.getId(), member.getAuthority());
        return jwtUtil.removePrefix(token);
    }

    private void validPassword(String requestPassword, Member member) {
        if (!passwordEncoder.matches(requestPassword, member.getPassword())) {
            throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
