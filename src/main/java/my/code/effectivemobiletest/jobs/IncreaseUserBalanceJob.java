package my.code.effectivemobiletest.jobs;

import lombok.RequiredArgsConstructor;
import my.code.effectivemobiletest.entities.BankAccountEntity;
import my.code.effectivemobiletest.repositories.BankAccountRepo;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class IncreaseUserBalanceJob {
    private final BankAccountRepo bankAccountRepo;

    @Scheduled(cron = "0 * * * * *")
    void increaseBalance() {
        List<BankAccountEntity> bankAccountEntityList = bankAccountRepo.findAll();

        for(BankAccountEntity bankAccount : bankAccountEntityList){
            BigDecimal startBalance = bankAccount.getStartBalance();
            BigDecimal currentBalance = bankAccount.getCurrentBalance();

            BigDecimal increasedBalance = currentBalance.multiply(BigDecimal.valueOf(1.05));
            BigDecimal maxAvailableBalance = startBalance.multiply(BigDecimal.valueOf(2.07));

            if(increasedBalance.compareTo(maxAvailableBalance) <= 0){
                bankAccount.setCurrentBalance(increasedBalance);
                bankAccountRepo.save(bankAccount);
            }
        }
    }
}
