package repository.interfaces;

import com.google.inject.ImplementedBy;
import models.UserAccount;
import repository.UserRepositoryImpl;

@ImplementedBy(UserRepositoryImpl.class)
public interface UserRepository {
    UserAccount getUser(String username, String password);
}
