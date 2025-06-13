package turtleMart.member.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turtleMart.global.exception.BadRequestException;
import turtleMart.global.exception.ErrorCode;
import turtleMart.global.exception.NotFoundException;
import turtleMart.member.dto.request.DeleteSellerRequest;
import turtleMart.member.dto.request.SellerRegisterRequest;
import turtleMart.member.dto.request.UpdateSellerRequest;
import turtleMart.member.dto.response.SellerResponse;
import turtleMart.member.dto.response.TokenResponse;
import turtleMart.member.entity.Member;
import turtleMart.member.entity.Seller;
import turtleMart.member.repository.MemberRepository;
import turtleMart.member.repository.SellerRepository;
import turtleMart.security.JwtUtil;

@Service
@RequiredArgsConstructor
@Transactional
public class SellerService {
    private final SellerRepository sellerRepository;
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final TokenBlacklistService blacklistService;
    private final HttpServletRequest httpServletRequest;

    /**
     * 판매자 등록
     */
    public TokenResponse registerSeller(Long authId, SellerRegisterRequest request) {
        Member foundMember = memberRepository.findById(authId)
                .orElseThrow(() -> new RuntimeException(""));
        foundMember.registerSeller();
        memberRepository.save(foundMember);
        Seller seller = Seller.of(request, foundMember);
        sellerRepository.save(seller);
        String token = createToken(foundMember);
        expireToken();
        return TokenResponse.from("판매자 등록이 완료되었습니다.", token);
    }

    /**
     * 판매자 조회
     */
    @Transactional(readOnly = true)
    public SellerResponse findSellerInfo(Long sellerId) {
        Seller foundSeller = findSeller(sellerId);
        return SellerResponse.from(foundSeller);
    }

    /**
     * 판매자 조회(판매자 전용)
     */
    @Transactional(readOnly = true)
    public SellerResponse findMySellerProfile(Long authId, Long sellerId) {
        Seller foundSeller = findSeller(sellerId);
        validSellerById(authId, foundSeller);
        return SellerResponse.from(foundSeller);
    }

    /**
     * 판매자 수정
     */
    public SellerResponse updateSellerProfile(Long authId, Long sellerId, UpdateSellerRequest request) {
        Seller foundSeller = findSeller(sellerId);
        validSellerById(authId, foundSeller);
        validPassword(request.password(), foundSeller);
        foundSeller.updateSeller(request);
        sellerRepository.save(foundSeller);
        return SellerResponse.from(foundSeller);
    }

    /**
     * 판매자 삭제
     */
    public TokenResponse deleteSeller(Long authId, Long sellerId, DeleteSellerRequest request) {
        Seller foundSeller = findSeller(sellerId);
        validSellerById(authId, foundSeller);
        validPassword(request.password(), foundSeller);
        foundSeller.getMember().unregisterSeller();
        memberRepository.save(foundSeller.getMember());
        sellerRepository.delete(foundSeller);
        String token = createToken(foundSeller.getMember());
        expireToken();
        return TokenResponse.from("판매자 해지가 완료되었습니다.", token);
    }

    private Seller findSeller(Long sellerId) {
        return sellerRepository.findById(sellerId)
//                .orElseThrow(() -> new RuntimeException("판매자를 찾을 수 없습니다."));
                .orElseThrow(() -> new NotFoundException(ErrorCode.SELLER_NOT_FOUND));
    }

    private void validSellerById(Long authId, Seller seller) {
        if (!seller.getMember().getId().equals(authId)) {
//            throw new RuntimeException("판매자로 등록된 회원이 아닙니다.");
            throw new BadRequestException(ErrorCode.SELLER_NOT_REGISTER);
        }
    }

    private void validPassword(String requestPassword, Seller seller) {
        if (!passwordEncoder.matches(requestPassword, seller.getMember().getPassword())) {
//            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
            throw new BadRequestException(ErrorCode.INVALID_PASSWORD);
        }
    }

    private String getToken(HttpServletRequest request) {
        return (String) request.getAttribute("rawJwtToken");
    }

    private String createToken(Member member) {
        String token = jwtUtil.createToken(member.getId(), member.getAuthority());
        return jwtUtil.removePrefix(token);
    }

    private void expireToken() {
        String oldToken = getToken(httpServletRequest);
        long expiration = jwtUtil.extractExpiration(oldToken).getTime();
        blacklistService.blacklistToken(oldToken, expiration);
    }

//    public String ckeckUser() {
//        // 테스트용 api
//        return "접근 성공";
//    }
}
