package turtleMart.member.dto.response;

import turtleMart.member.entity.BankAccount;

public record AccountResponse(
        Long accountId,
        String bankName,
        String accountNumber
) {
    public static AccountResponse from(BankAccount account) {
        return new AccountResponse(
                account.getId(),
                account.getBankName(),
                account.getAccountNumber()
        );
    }
}
