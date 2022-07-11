package se.seb.incorrectpasswordblocker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.seb.incorrectpasswordblocker.utils.Constants.ACCOUNT_IS_LOCKED_MESSAGE;
import static se.seb.incorrectpasswordblocker.utils.Constants.CREDENTIALS_CANNOT_BE_NULL_MESSAGE;
import static se.seb.incorrectpasswordblocker.utils.Constants.FIVE_MINUTES_IN_SECONDS;
import static se.seb.incorrectpasswordblocker.utils.Constants.MINUTE_IN_SECONDS;
import static se.seb.incorrectpasswordblocker.utils.Constants.USERNAME_NOT_FOUND_MESSAGE;
import static se.seb.incorrectpasswordblocker.utils.Constants.WRONG_CREDENTIALS_MESSAGE;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import se.seb.incorrectpasswordblocker.db.DatabaseSimulator;
import se.seb.incorrectpasswordblocker.exceptions.AccountLockedException;
import se.seb.incorrectpasswordblocker.exceptions.UserNotFoundException;
import se.seb.incorrectpasswordblocker.exceptions.WrongCredentialsException;
import se.seb.incorrectpasswordblocker.model.User;
import se.seb.incorrectpasswordblocker.model.UserCredentials;
import se.seb.incorrectpasswordblocker.utils.InputUtil;

class AppManagerTest {

  InputUtil inputUtil;
  DatabaseSimulator dbSimulator;
  AppManager appManager;

  @BeforeEach
  void setUp() {
    inputUtil = new InputUtil(new BufferedReader(new InputStreamReader(System.in)));
    dbSimulator = new DatabaseSimulator();
    appManager = new AppManager(inputUtil, dbSimulator);

  }

  @Test
  @DisplayName("Should authenticate with correct credentials.")
  void shouldAuthenticate() {
    UserCredentials credentials = new UserCredentials("admin", "admin");

    assertThatNoException().isThrownBy(() -> appManager.authenticate(credentials));

  }

  @Test
  @DisplayName("Should not authenticate with wrong credentials.")
  void shouldNotAuthenticateWithWrongCredentials() {
    UserCredentials credentials = new UserCredentials("admin", "password");
    assertWrongCredentialsException(credentials);
  }

  @Test
  @DisplayName("Should throw NPE when there are no credentials.")
  void shouldThrowNPEWhenCredentialsAreNull() {
    assertThatThrownBy(() -> appManager.authenticate(null))
      .isInstanceOf(NullPointerException.class)
      .hasMessage(CREDENTIALS_CANNOT_BE_NULL_MESSAGE);
  }

  @Test
  @DisplayName("Should authenticate when 4 incorrect attempts were done within one minute and 1 correct attempt afterwards.")
  void userShouldBeAuthenticatedWhenMoreThanOneMinuteHasGoneAfterMultipleFailingAttempts() {

    UserCredentials fakeCredentials = new UserCredentials("admin", "wrongpassword");
    UserCredentials realCredentials = new UserCredentials("admin", "admin");

    User mockedUser = new User("admin", "admin");
    Optional<User> optionalMockedUser = Optional.of(mockedUser);

    DatabaseSimulator mockedDbSimulator = mock(DatabaseSimulator.class);
    when(mockedDbSimulator.findUserByUsername(anyString())).thenReturn(optionalMockedUser);

    appManager = new AppManager(inputUtil, mockedDbSimulator);

    // Stimulating  4 attempts with wrong credentials within "1 minute"
    for (int i = 0; i < 4; i++) {
      assertWrongCredentialsException(fakeCredentials);
    }

    // Fifth attempt after ca 1 minute
    mockedUser.setFirstLoginAttemptDate(Instant.now().minus(MINUTE_IN_SECONDS + 10, ChronoUnit.SECONDS));
    Optional<User> mockedUserWithFirstLoginDateModified = Optional.of(mockedUser);
    when(mockedDbSimulator.findUserByUsername(anyString())).thenReturn(mockedUserWithFirstLoginDateModified);
    assertWrongCredentialsException(fakeCredentials);

    //User can log in the next attempt if the credentials are correct.
    assertThatNoException().isThrownBy(() -> appManager.authenticate(realCredentials));

  }

  @Test
  @DisplayName("Should lock the account after 5 attempts within one minute.")
  void shouldLockTheUserAccount() {
    UserCredentials userCredentials = new UserCredentials("test", "wrongPassword");

    for (int i = 0; i < 5; i++) {
      assertWrongCredentialsException(userCredentials);
    }

    assertAccountLockedException(userCredentials);

  }

  @Test
  @DisplayName("Should unlock account after 5 minutes.")
  void shouldUnlockAccountWhenMoreThanFiveMinutesHaveGone() {
    UserCredentials fakeCredentials = new UserCredentials("admin", "wrongpassword");
    UserCredentials realCredentials = new UserCredentials("admin", "admin");

    User mockedUser = new User("admin", "admin");
    Optional<User> optionalMockedUser = Optional.of(mockedUser);

    DatabaseSimulator mockedDbSimulator = mock(DatabaseSimulator.class);
    when(mockedDbSimulator.findUserByUsername(anyString())).thenReturn(optionalMockedUser);

    appManager = new AppManager(inputUtil, mockedDbSimulator);

    for (int i = 0; i < 5; i++) {
      assertWrongCredentialsException(fakeCredentials);
    }

    assertAccountLockedException(fakeCredentials);

    // Stimulate that 5 minutes has gone
    mockedUser.setLastLoginAttempt(Instant.now().minus(FIVE_MINUTES_IN_SECONDS + 5, ChronoUnit.SECONDS));
    Optional<User> mockedUserWithFirstLoginDateModified = Optional.of(mockedUser);

    when(mockedDbSimulator.findUserByUsername(anyString())).thenReturn(mockedUserWithFirstLoginDateModified);

    // Try to log in again after 5 minutes with wrong credentials.
    // OBS! User state will be reset after 5 minutes thats why WrongCredentialsException will be thrown.
    assertWrongCredentialsException(fakeCredentials);

    // Login with real credentials
    assertThatNoException().isThrownBy(() -> appManager.authenticate(realCredentials));

  }

  @Test
  @DisplayName("Should get user credentials.")
  void shouldGetTheUserCredentials() {
    final String username = "fazli";
    final String password = "zekiqi";
    final String userInput = String.format("%s%n%s", username, password);
    InputUtil inputUtil = new InputUtil(new BufferedReader(new StringReader(userInput)));
    appManager = new AppManager(inputUtil, dbSimulator);

    UserCredentials userCredentials = appManager.getUserCredentials();

    assertThat(userCredentials).isNotNull();
    assertThat(userCredentials.username()).isEqualTo(username);
    assertThat(userCredentials.password()).isEqualTo(password);

  }

  @Test
  @DisplayName("Should throw an error when the user is not found.")
  void shouldNotFindUserIfItDoesntExists() {
    UserCredentials notFoundUserCredentials = new UserCredentials("notFound", "notFound");

    assertNotFoundException(notFoundUserCredentials);
  }

  private void assertWrongCredentialsException(UserCredentials fakeCredentials) {
    assertThatThrownBy(() -> appManager.authenticate(fakeCredentials))
      .isInstanceOf(WrongCredentialsException.class)
      .hasMessage(WRONG_CREDENTIALS_MESSAGE);
  }

  private void assertAccountLockedException(UserCredentials userCredentials) {
    assertThatThrownBy(() -> appManager.authenticate(userCredentials))
      .isInstanceOf(AccountLockedException.class)
      .hasMessage(ACCOUNT_IS_LOCKED_MESSAGE);
  }

  private void assertNotFoundException(UserCredentials notFoundUser) {
    assertThatThrownBy(() -> appManager.authenticate(notFoundUser))
      .isInstanceOf(UserNotFoundException.class)
      .hasMessage(String.format(USERNAME_NOT_FOUND_MESSAGE, notFoundUser.username()));
  }

}
