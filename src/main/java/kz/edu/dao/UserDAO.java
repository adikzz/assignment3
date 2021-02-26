package kz.edu.dao;

import kz.edu.model.Authority;
import kz.edu.model.Book;
import kz.edu.model.Role;
import kz.edu.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Set;

@Repository
public class UserDAO {
    private final SessionFactory sessionFactory;

    @Autowired
    public UserDAO(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    public List<User> getUserList() {
        List<User> usersList;
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<User> criteria = builder.createQuery(User.class);
            Root<User> root = criteria.from(User.class);

            Predicate predicateUser = builder.equal(root.get("role"), 2);

            criteria.select(root).where(predicateUser);
            Query<User> query = session.createQuery(criteria);
            usersList = query.getResultList();
            session.getTransaction().commit();
        } finally {
            session.close();
        }
        return usersList;
    }

    public User findByUserName(String username) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        User user;
        try {
            CriteriaBuilder builder1 = session.getCriteriaBuilder();
            CriteriaQuery<User> q1 = builder1.createQuery(User.class);
            Root<User> root1 = q1.from(User.class);

            Predicate predicateUsername = builder1.equal(root1.get("email"), username);
            user = session.createQuery(q1.where(predicateUsername)).getSingleResult();
            //System.out.println("available authorities:"+user.getRole().getAuthorities());
            //System.out.println("USER DAO. Email:"+user.getEmail()+". Role: "+user.getRole().getId()+". Password: "+user.getPassword());
        } catch (NoResultException noResultException) {
            user = null;
        } finally {
            //session.close();
        }
        return user;
    }

    public User findByUserId(int id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        User user;
        try {
            CriteriaBuilder builder1 = session.getCriteriaBuilder();
            CriteriaQuery<User> q1 = builder1.createQuery(User.class);
            Root<User> root1 = q1.from(User.class);

            Predicate predicateId = builder1.equal(root1.get("id"), id);
            user = session.createQuery(q1.where(predicateId)).getSingleResult();
            session.getTransaction().commit();
            //System.out.println("available authorities:"+user.getRole().getAuthorities());
            //System.out.println("USER DAO. Email:"+user.getEmail()+". Role: "+user.getRole().getId()+". Password: "+user.getPassword());
        } catch (NoResultException noResultException) {
            user = null;
        } finally {
            //session.close();
        }
        return user;
    }

    public Role returnRole(String roleString) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        CriteriaBuilder builder1 = session.getCriteriaBuilder();
        CriteriaQuery<Role> q1 = builder1.createQuery(Role.class);
        Root<Role> root1 = q1.from(Role.class);

        Predicate predicateRole = builder1.equal(root1.get("name"), roleString);
        //Predicate predicateRole = builder1.equal(root1.get("name"), "ROLE_ADMIN");
        Role role = session.createQuery(q1.where(predicateRole)).getSingleResult();
        return role;
    }

    public void addUser(User user) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            CriteriaBuilder builder1 = session.getCriteriaBuilder();
            CriteriaQuery<Role> q1 = builder1.createQuery(Role.class);
            Root<Role> root1 = q1.from(Role.class);

            Predicate predicateRole = builder1.equal(root1.get("name"), "ROLE_USER");
            //Predicate predicateRole = builder1.equal(root1.get("name"), "ROLE_ADMIN");
            Role role = session.createQuery(q1.where(predicateRole)).getSingleResult();
            user.setRole(role);

            session.persist(user);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    public void updateUser(User user) {
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            user.setRole(returnRole("ROLE_USER"));
            session.merge(user);
            session.getTransaction().commit();
        } finally {
            //session.close();
        }
    }

    public void recreateUser(User user) {
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            session.merge(user);
            session.getTransaction().commit();
        } finally {
            //session.close();
        }
    }

    public void deleteUser(int id) {
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            User user = session.find(User.class, (long) id);
            System.out.println("The email of the user to be deleted: " + user.getEmail());
            user.setActive(false);
            session.merge(user);
            //session.remove(user);
            session.getTransaction().commit();
        } finally {
            //session.close();
        }
    }
}