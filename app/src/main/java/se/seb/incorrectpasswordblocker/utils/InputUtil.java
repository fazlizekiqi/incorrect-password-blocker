package se.seb.incorrectpasswordblocker.utils;

import static se.seb.incorrectpasswordblocker.utils.Constants.ADD_VALID_INPUT_MESSAGE;
import static se.seb.incorrectpasswordblocker.utils.Constants.BUFFERED_READER_IS_REQUIRED;
import static se.seb.incorrectpasswordblocker.utils.Constants.ERROR_CLOSING_BUFFERED_READER;
import static se.seb.incorrectpasswordblocker.utils.Constants.ERROR_READING_LINE;

import com.google.common.base.Strings;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

public class InputUtil {

  private final BufferedReader reader;

  public InputUtil(BufferedReader reader) {
    this.reader = Objects.requireNonNull(reader, BUFFERED_READER_IS_REQUIRED);
  }

  public String readLine() {
    try {
      String input = reader.readLine();
      while (Strings.isNullOrEmpty(input)) {
        System.out.println(ADD_VALID_INPUT_MESSAGE);
        input = reader.readLine();
      }

      return input;
    } catch (IOException e) {
      System.out.println(ERROR_READING_LINE);
    }

    return null;
  }

  public void closeReader() {
    try {
      reader.close();
    } catch (IOException e) {
      System.out.println(ERROR_CLOSING_BUFFERED_READER);
    }
  }

}
