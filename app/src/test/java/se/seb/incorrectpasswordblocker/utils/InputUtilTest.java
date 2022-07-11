package se.seb.incorrectpasswordblocker.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class InputUtilTest {

  @Test
  @DisplayName("Should read line")
  void getUserCredentials() {
    final String apple = "apple";
    InputUtil inputUtil = new InputUtil(new BufferedReader(new  StringReader(apple)));
    String line = inputUtil.readLine();

    assertThat(line).isEqualTo(apple);
  }

}
