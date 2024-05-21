package my.code.effectivemobiletest;

import my.code.effectivemobiletest.entities.BankAccountEntity;
import my.code.effectivemobiletest.entities.UserEntity;
import my.code.effectivemobiletest.exceptions.InsufficientFundsException;
import my.code.effectivemobiletest.exceptions.SameUserTransactionException;
import my.code.effectivemobiletest.repositories.UserRepo;
import my.code.effectivemobiletest.services.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EffectiveMobileTestApplicationTests {

	@Mock
	private UserRepo userRepo;
	@InjectMocks
	private UserServiceImpl userService;
	private UserEntity userFrom;
	private UserEntity userTo;
	private BankAccountEntity bankAccountFrom;
	private BankAccountEntity bankAccountTo;

	@BeforeEach
	void setUp() {
		bankAccountFrom = new BankAccountEntity();
		bankAccountFrom.setId(11111L);
		bankAccountFrom.setCurrentBalance(BigDecimal.valueOf(100));

		userFrom = new UserEntity();
		userFrom.setId(11111L);
		userFrom.setUsername("userFrom");
		userFrom.setBankAccount(bankAccountFrom);

		bankAccountTo = new BankAccountEntity();
		bankAccountTo.setId(22222L);
		bankAccountTo.setCurrentBalance(BigDecimal.valueOf(50));

		userTo = new UserEntity();
		userTo.setId(22222L);
		userTo.setUsername("userTo");
		userTo.setBankAccount(bankAccountTo);
	}

	@Test
	void testSuccessfulTransaction() {
		when(userRepo.findByUsername("userFrom")).thenReturn(userFrom);
		when(userRepo.findById(22222L)).thenReturn(Optional.of(userTo));

		String result = userService.transaction("userFrom", 22222L, BigDecimal.valueOf(50));

		assertEquals("Transaction has gone successfully!", result);
		assertEquals(BigDecimal.valueOf(50), bankAccountFrom.getCurrentBalance());
		assertEquals(BigDecimal.valueOf(100), bankAccountTo.getCurrentBalance());
	}

	@Test
	void testTransactionToSameUser() {
		when(userRepo.findByUsername("userFrom")).thenReturn(userFrom);
		when(userRepo.findById(11111L)).thenReturn(Optional.of(userFrom));

		SameUserTransactionException exception = assertThrows(SameUserTransactionException.class,
				() -> userService.transaction("userFrom", 11111L, BigDecimal.valueOf(50)));

		assertEquals("User cannot transfer money to their own account!", exception.getMessage());
	}

	@Test
	void testTransactionWithInsufficientFunds() {
		when(userRepo.findByUsername("userFrom")).thenReturn(userFrom);
		when(userRepo.findById(22222L)).thenReturn(Optional.of(userTo));

		InsufficientFundsException exception = assertThrows(InsufficientFundsException.class,
				() -> userService.transaction("userFrom", 22222L, BigDecimal.valueOf(150)));

		assertEquals("User doesn't have enough money for transaction! Please top up your balance.", exception.getMessage());
	}

	@Test
	void testTransactionWithNegativeAmount() {
		when(userRepo.findByUsername("userFrom")).thenReturn(userFrom);
		when(userRepo.findById(22222L)).thenReturn(Optional.of(userTo));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> userService.transaction("userFrom", 22222L, BigDecimal.valueOf(-50)));

		assertEquals("Amount to transfer must be greater than zero!", exception.getMessage());
	}

	@Test
	void testTransactionWithNonexistentUser() {
		when(userRepo.findByUsername("userFrom")).thenReturn(userFrom);
		when(userRepo.findById(22222L)).thenReturn(Optional.empty());

		NullPointerException exception = assertThrows(NullPointerException.class,
				() -> userService.transaction("userFrom", 22222L, BigDecimal.valueOf(50)));

		assertEquals("User with id 22222 is not found!", exception.getMessage());
	}
}
