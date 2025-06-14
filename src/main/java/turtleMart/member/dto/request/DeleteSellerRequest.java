package turtleMart.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DeleteSellerRequest(
        @NotBlank(message = "비밀번호를 입력해주세요.")
        String password
) {
}
