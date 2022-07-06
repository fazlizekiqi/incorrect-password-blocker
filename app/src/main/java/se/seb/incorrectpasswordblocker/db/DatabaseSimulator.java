package se.seb.incorrectpasswordblocker.db;

import java.util.List;
import java.util.Optional;
import se.seb.incorrectpasswordblocker.model.User;

public class DatabaseSimulator {

  private final List<User> users = List.of(
    new User("admin", "admin"),
    new User("test", "test")
  );

  public Optional<User> findUserByUsername(final String username){
    return users.stream().filter(user -> user.getUsername().equals(username)).findAny();
  }

}
