package turtleMart.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turtleMart.global.exception.ErrorCode;
import turtleMart.global.exception.NotFoundException;
import turtleMart.member.dto.request.AccountRegisterRequest;
import turtleMart.member.dto.response.AccountResponse;
import turtleMart.member.entity.BankAccount;
import turtleMart.member.repository.AccountRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {
    private final AccountRepository accountRepository;

    public AccountResponse accountRegister(AccountRegisterRequest request) {
        BankAccount bankAccount = BankAccount.of(request);
        accountRepository.save(bankAccount);
        return AccountResponse.from(bankAccount);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> findMyAccountList() {
        List<BankAccount> accountList = accountRepository.findAll();
        if (accountList.isEmpty()) {
            throw new NotFoundException(ErrorCode.ACCOUNT_NOT_REGISTER);
        }
        return accountList.stream().map(AccountResponse::from).toList();
    }

    public String deleteAccount(Long accountId) {
        BankAccount bankAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ACCOUNT_NOT_REGISTER));
        accountRepository.delete(bankAccount);
        return "계좌가 삭제되었습니다.";
    }
}
