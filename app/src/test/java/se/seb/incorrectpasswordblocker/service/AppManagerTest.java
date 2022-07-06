package se.seb.incorrectpasswordblocker.service;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static se.seb.incorrectpasswordblocker.utils.Constants.CREDENTIALS_CANNOT_BE_NULL_MESSAGE;
import static se.seb.incorrectpasswordblocker.utils.Constants.MINUTE_IN_SECONDS;
import static se.seb.incorrectpasswordblocker.utils.Constants.WRONG_CREDENTIALS_MESSAGE;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import se.seb.incorrectpasswordblocker.db.DatabaseSimulator;
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
  @DisplayName("User should be authenticated with correct credentials.")
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
  @DisplayName("User should be authenticated when 4 wrong attempts are done within one minute and 1 is done after one minute.")
  void userShouldBeAuthenticatedWhenMoreThanOneMinuteHasGone() {

    UserCredentials fakeCredentials = new UserCredentials("admin", "wrongpassword");
    UserCredentials realCredentials = new UserCredentials("admin", "admin");

    User mockedUser = new User("admin", "admin");
    Optional<User> optionalMockedUser = Optional.of(mockedUser);

    DatabaseSimulator mockedDbSimulator = mock(DatabaseSimulator.class);
    when(mockedDbSimulator.findUserByUsername(anyString())).thenReturn(optionalMockedUser);

    appManager = new AppManager(inputUtil, mockedDbSimulator);

    // 4 attempts within 1 minute
    for (int i = 0; i < 4; i++) {
      assertWrongCredentialsException(fakeCredentials);
    }

    // Fifth attempt after ca 1 minute
    mockedUser.setFirstLoginAttemptDate(Instant.now().minus(MINUTE_IN_SECONDS + 10, ChronoUnit.SECONDS));
    Optional<User> mockedUserWithFirstLoginDateModified = Optional.of(mockedUser);
    when(mockedDbSimulator.findUserByUsername(anyString())).thenReturn(mockedUserWithFirstLoginDateModified);

    assertWrongCredentialsException(fakeCredentials);

    //User can log in
    assertThatNoException().isThrownBy(() -> appManager.authenticate(realCredentials));

  }


  @Test
  void shouldBlockTheUser() {

  }

  private void assertWrongCredentialsException(UserCredentials fakeCredentials) {
    assertThatThrownBy(() -> appManager.authenticate(fakeCredentials))
      .isInstanceOf(WrongCredentialsException.class)
      .hasMessage(WRONG_CREDENTIALS_MESSAGE);
  }

}
