package turtleMart.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turtleMart.global.exception.BadRequestException;
import turtleMart.global.exception.ErrorCode;
import turtleMart.member.dto.request.MemberWithdrawRequest;
import turtleMart.member.dto.request.updatePasswordRequest;
import turtleMart.member.dto.request.updateProfileRequest;
import turtleMart.member.dto.response.MemberResponse;
import turtleMart.member.entity.Member;
import turtleMart.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public MemberResponse getMyProfile(Long authId) {
        Member foundMember = getMember(authId);
        return MemberResponse.from(foundMember);
    }

    public MemberResponse updateProfile(Long authId, updateProfileRequest request) {
        Member foundMember = getMember(authId);
        verifyPassword(request.password(), foundMember);
        if (memberRepository.existsByEmail(request.email())) {
//            throw new RuntimeException("이미 사용중인 이메일입니다.");
            throw new BadRequestException(ErrorCode.EMAIL_ALREADY_EXIST);
        }
        foundMember.updateProfile(request);
        memberRepository.save(foundMember);
        return MemberResponse.from(foundMember);
    }

    public String updatePassword(Long authId, updatePasswordRequest request) {
        Member foundMember = getMember(authId);
        verifyPassword(request.oldPassword(), foundMember);
        String encodedpassword = passwordEncoder.encode(request.newPassword());
        foundMember.updatePassword(encodedpassword);
        memberRepository.save(foundMember);
        return "비밀번호가 변경되었습니다.";
    }

    // Todo: 소프트딜리트로 바꿔야 할지...
    public String withdrawMember(Long authId, MemberWithdrawRequest request) {
        Member foundMember = getMember(authId);
        verifyPassword(request.password(), foundMember);
        memberRepository.delete(foundMember);
        return "회원 탈퇴가 완료되었습니다.";
    }

    private Member getMember(Long authId) {
        return memberRepository.findById(authId)
                .orElseThrow(() -> new RuntimeException(""));
    }

    /**
     * 비밀번호 검증
     */
    private void verifyPassword(String password, Member member) {
        if (!passwordEncoder.matches(password, member.getPassword())) {
//            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
            throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
        }
    }
}
