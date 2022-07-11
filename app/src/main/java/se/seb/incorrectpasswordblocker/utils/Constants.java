package se.seb.incorrectpasswordblocker.utils;

public class Constants {

  public final static Integer MAXIMUM_LOG_IN_ATTEMPTS = 5;

  public final static int MINUTE_IN_SECONDS = 60;
  public final static int FIVE_MINUTES_IN_SECONDS = MINUTE_IN_SECONDS * 5;

  public final static String CREDENTIALS_CANNOT_BE_NULL_MESSAGE  = "User credentials cannot be null.";
  public final static String USERNAME_NOT_FOUND_MESSAGE  = "Username [%s] is not found.";
  public final static String ACCOUNT_IS_LOCKED_MESSAGE  = "Account is blocked for 5 minutes since you've entered wrong credentials multiple times.";
  public final static String WRONG_CREDENTIALS_MESSAGE   = "Wrong credentials entered! Please try again.";
  public final static String GET_USERNAME_MESSAGE = "Please input your username: ";
  public final static String GET_PASSWORD_MESSAGE = "Please input your password: ";
  public final static String ADD_VALID_INPUT_MESSAGE = "Add a valid input: ";

  public final static String ERROR_READING_LINE = "Error reading a line.";
  public final static String ERROR_CLOSING_BUFFERED_READER = "Error closing buffered reader!";
  public final static String BUFFERED_READER_IS_REQUIRED = "Buffered reader is required";

  public final static String WELCOME_TO_APPLICATION_MESSAGE = "WELCOME TO APPLICATION.";
  public final static String RUN_APPLICATION_AGAIN_QUESTION= "Do you want to run the application again! [y/n]:";

}
