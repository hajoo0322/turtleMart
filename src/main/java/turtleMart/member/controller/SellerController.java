package turtleMart.member.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import turtleMart.member.dto.request.DeleteSellerRequest;
import turtleMart.member.dto.request.SellerRegisterRequest;
import turtleMart.member.dto.request.UpdateSellerRequest;
import turtleMart.member.dto.response.SellerResponse;
import turtleMart.member.dto.response.TokenResponse;
import turtleMart.member.service.SellerService;
import turtleMart.security.AuthUser;
import turtleMart.security.CheckRole;

@RestController
@RequestMapping("/members/sellers")
@RequiredArgsConstructor
public class SellerController {
    private final SellerService sellerService;

    @PostMapping("register")
    public ResponseEntity<TokenResponse> registerSeller(
            @AuthenticationPrincipal AuthUser authUser,
            @RequestBody @Valid SellerRegisterRequest request
    ) {
        TokenResponse response = sellerService.registerSeller(authUser.memberId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{sellerId}")
    public ResponseEntity<SellerResponse> getSeller(
            @PathVariable Long sellerId
    ) {
        SellerResponse seller = sellerService.findSellerInfo(sellerId);
        return ResponseEntity.status(HttpStatus.OK).body(seller);
    }

    @GetMapping("/{sellerId}/me")
    public ResponseEntity<SellerResponse> getMySellerProfile(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long sellerId
    ) {
        SellerResponse mySellerProfile = sellerService.findMySellerProfile(authUser.memberId(), sellerId);
        return ResponseEntity.status(HttpStatus.OK).body(mySellerProfile);
    }

    @PatchMapping("/{sellerId}/me/modify")
    public ResponseEntity<SellerResponse> updateMySellerProfile(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long sellerId,
            @RequestBody @Valid UpdateSellerRequest request
    ) {
        SellerResponse sellerResponse = sellerService.updateSellerProfile(authUser.memberId(), sellerId, request);
        return ResponseEntity.status(HttpStatus.OK).body(sellerResponse);
    }

    @DeleteMapping("/{sellerId}/me/close")
    public ResponseEntity<TokenResponse> withdrawSeller(
            @AuthenticationPrincipal AuthUser authUser,
            @PathVariable Long sellerId,
            @RequestBody @Valid DeleteSellerRequest request
    ) {
        TokenResponse response = sellerService.deleteSeller(authUser.memberId(), sellerId, request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

//    @CheckRole("SELLER")
//    @GetMapping("/any")
//    public ResponseEntity<String> checkUserRole() {
//        // 테스트용 api
//        String resultMessage = sellerService.ckeckUser();
//        return ResponseEntity.status(HttpStatus.OK).body(resultMessage);
//    }
}
