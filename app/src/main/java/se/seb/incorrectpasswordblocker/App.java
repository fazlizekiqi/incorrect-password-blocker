package se.seb.incorrectpasswordblocker;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import se.seb.incorrectpasswordblocker.db.DatabaseSimulator;
import se.seb.incorrectpasswordblocker.exceptions.AccountBlockedException;
import se.seb.incorrectpasswordblocker.exceptions.UserNotFoundException;
import se.seb.incorrectpasswordblocker.exceptions.WrongCredentialsException;
import se.seb.incorrectpasswordblocker.model.UserCredentials;
import se.seb.incorrectpasswordblocker.service.AppManager;
import se.seb.incorrectpasswordblocker.utils.InputUtil;

public class App {

  private static InputUtil inputUtil = new InputUtil(new BufferedReader(new InputStreamReader(System.in)));
  private static DatabaseSimulator dbSimulator = new DatabaseSimulator();
  private static AppManager appManager = new AppManager(inputUtil, dbSimulator);

  public static void main(String[] args) {
    start();
  }

  private static void start() {
    try {
      UserCredentials userCredentials = appManager.getUserCredentials();

      appManager.authenticate(userCredentials);
      System.out.println("Welcome to our application.");
      askUserToRunApplicationAgain();
    } catch (UserNotFoundException | AccountBlockedException | WrongCredentialsException e) {
      System.out.println(e.getMessage());
      start();
    }
  }

  private static void askUserToRunApplicationAgain() {
    System.out.print("Do you want to run the application again! [y/n]:");
    String answer = inputUtil.readLine();
    boolean isAnswerYes = answer.equals("y");
    boolean isAnswerNo = answer.equals("n");

    if (isAnswerYes || isAnswerNo) {
      if (isAnswerYes) {
        start();
      }

      inputUtil.closeReader();
      System.exit(0);
    }
  }
}
