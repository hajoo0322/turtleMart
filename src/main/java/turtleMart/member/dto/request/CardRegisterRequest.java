package turtleMart.member.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CardRegisterRequest(
        @NotBlank(message = "카드사를 입력해주세요.")
        String cardIssuer,
        @NotBlank(message = "카드번호를 입력해주세요.")
        @Pattern(regexp = "\\d{16}", message = "카드번호는 16자리여야 합니다.")
        String cardNumber,
        @NotBlank(message = "유효기간(MM/YY)을 입력해주세요.")
        @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "유효기간은 MM/YY 형식이어야 합니다.")
        String expirationDate,
        @NotBlank(message = "CVC를 입력해주세요.")
        @Pattern(regexp = "\\d{3}", message = "CVC는 3자리 숫자여야 합니다.")
        String cvcNumber,
        @NotBlank(message = "카드 비밀번호를 입력해주세요.")
        @Pattern(regexp = "\\d{2}", message = "비밀번호 앞 2자리는 2자리 숫자여야 합니다.")
        String cardPassword
) {
}
