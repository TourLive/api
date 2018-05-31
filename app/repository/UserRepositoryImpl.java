package repository;

import models.UserAccount;
import play.db.jpa.JPAApi;
import repository.interfaces.UserRepository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.function.Function;

public class UserRepositoryImpl implements UserRepository {
    private final JPAApi jpaApi;
    private final DatabaseExecutionContext databaseExecutionContext;

    @Inject
    public UserRepositoryImpl(JPAApi jpaApi, DatabaseExecutionContext databaseExecutionContext) {
        this.jpaApi = jpaApi;
        this.databaseExecutionContext = databaseExecutionContext;
    }


    @Override
    public UserAccount getUser(String username, String password) {
        return wrap(entityManager -> getUser(entityManager, username, password));
    }

    private UserAccount getUser(EntityManager entityManager, String username, String password){
        TypedQuery<UserAccount> query = entityManager.createQuery("select u from UserAccount u where u.username = :username and u.password = :password" , UserAccount.class);
        query.setParameter("username", username);
        query.setParameter("password", password);
        return query.getSingleResult();
    }

    private <T> T wrap(Function<EntityManager, T> function) {
        return jpaApi.withTransaction(function);
    }
}
