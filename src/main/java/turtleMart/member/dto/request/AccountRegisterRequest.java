package turtleMart.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AccountRegisterRequest(
        @NotBlank(message = "은행명을 입력해주세요.")
        String bankName,
        @NotBlank(message = "계좌번호를 입력해주세요.")
        @Pattern(regexp = "\\d{8,14}", message = "계좌번호는 숫자 8~14자리여야 합니다.")
        String accountNumber,
        @NotBlank(message = "계좌 비밀번호를 입력해주세요.")
        @Pattern(regexp = "\\d{2}", message = "비밀번호 앞 2자리는 2자리 숫자여야 합니다.")
        String accountPassword
) {
}
