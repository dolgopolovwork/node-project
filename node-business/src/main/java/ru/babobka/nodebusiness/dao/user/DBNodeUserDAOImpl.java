package ru.babobka.nodebusiness.dao.user;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import ru.babobka.nodebusiness.model.User;
import ru.babobka.nodeutils.container.Container;

import java.util.List;
import java.util.UUID;

public class DBNodeUserDAOImpl implements NodeUsersDAO {
    private static final Logger logger = Logger.getLogger(DBNodeUserDAOImpl.class);
    private final SessionFactory sessionFactory = Container.getInstance().get(SessionFactory.class);

    @Override
    public User get(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(User.class, id.toString());
        }
    }

    @Override
    public User get(String login) {
        try (Session session = sessionFactory.openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE name=:login", User.class);
            query.setParameter("login", login);
            return query.uniqueResult();
        }
    }

    @Override
    public List<User> getList() {
        try (Session session = sessionFactory.openSession()) {
            Query query = session.createQuery("FROM User");
            List<User> users = query.list();
            return users;
        }
    }

    @Override
    public boolean add(User user) {
        logger.info("Create user " + user);
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                NativeQuery sqlQuery = session.createSQLQuery("INSERT INTO NODE_USER(USER_ID,USER_NAME,USER_PUBLIC_KEY_BASE64,USER_EMAIL) VALUES(:id,:name,:public_key,:email) ON CONFLICT DO NOTHING");
                sqlQuery.setParameter("id", user.getId());
                sqlQuery.setParameter("name", user.getName());
                sqlQuery.setParameter("public_key", user.getPublicKeyBase64());
                sqlQuery.setParameter("email", user.getEmail());
                boolean inserted = sqlQuery.executeUpdate() > 0;
                transaction.commit();
                return inserted;
            } catch (Exception ex) {
                logger.error("Cannot add user " + user, ex);
                transaction.rollback();
                return false;
            }
        }
    }

    @Override
    public boolean exists(String login) {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(1) FROM User WHERE name=:login", Long.class);
            query.setParameter("login", login);
            long count = query.uniqueResult();
            return count > 0;
        }
    }

    @Override
    public boolean remove(UUID id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Query sqlQuery = session.createQuery("DELETE FROM User WHERE id=:user_id");
                sqlQuery.setParameter("user_id", id.toString());
                int removedUsers = sqlQuery.executeUpdate();
                transaction.commit();
                return removedUsers > 0;
            } catch (Exception ex) {
                logger.error("Cannot remove the user with id " + id, ex);
                transaction.rollback();
                return false;
            }
        }
    }

    @Override
    public boolean update(User user) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            StringBuilder updateQuery = new StringBuilder("UPDATE User SET ");
            try {
                boolean updatable = false;
                if (user.getName() != null) {
                    updatable = true;
                    updateQuery.append("name=:name ");
                }
                if (user.getEmail() != null) {
                    updatable = true;
                    updateQuery.append("email=:email ");
                }
                if (user.getPublicKeyBase64() != null) {
                    updatable = true;
                    updateQuery.append("publicKeyBase64=:publicKey ");
                }
                if (!updatable) {
                    transaction.commit();
                    return false;
                }
                updateQuery.append("WHERE id=:id");
                Query query = session.createQuery(updateQuery.toString());
                if (user.getName() != null) {
                    query.setParameter("name", user.getName());
                }
                if (user.getEmail() != null) {
                    query.setParameter("email", user.getEmail());
                }
                if (user.getPublicKeyBase64() != null) {
                    query.setParameter("publicKey", user.getPublicKeyBase64());
                }
                query.setParameter("id", user.getId());
                int updated = query.executeUpdate();
                transaction.commit();
                return updated > 0;
            } catch (Exception ex) {
                logger.error("Cannot update user " + user, ex);
                transaction.rollback();
                return false;
            }
        }
    }
}
