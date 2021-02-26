package kz.edu.dao;

import kz.edu.model.Book;
import kz.edu.model.Borrowing;
import kz.edu.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Repository
public class BorrowingDAO {

    private SessionFactory sessionFactory;
    Session session;
    List<Borrowing> borrowingList;

    @Autowired
    public BorrowingDAO(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    public List<Borrowing> getTakenBookList(User user) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Borrowing> criteria = builder.createQuery(Borrowing.class);
            Root<Borrowing> root = criteria.from(Borrowing.class);

            Predicate predicateBorrowing = builder.equal(root.get("returned"), 0);
            Predicate predicateUser = builder.equal(root.get("user_id"), user.getId());
            Predicate predicateSearch = builder.and(predicateBorrowing, predicateUser);

            criteria.select(root).where(predicateSearch);
            Query<Borrowing> query = session.createQuery(criteria);
            borrowingList = query.getResultList();
            session.getTransaction().commit();
        } finally {
            session.close();
        }
        return borrowingList;
    }

    public Borrowing getBorrowing(int id) {
        session = sessionFactory.openSession();
        session.beginTransaction();
        Borrowing borrowing;
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Borrowing> q1 = builder.createQuery(Borrowing.class);
            Root<Borrowing> root = q1.from(Borrowing.class);

            Predicate predicateBorrowing = builder.equal(root.get("id"), id);
            borrowing = session.createQuery(q1.where(predicateBorrowing)).getSingleResult();
            session.getTransaction().commit();
        } finally {
            session.close();
        }
        return borrowing;
    }

    public void addBookToUser(Borrowing borrowing) {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(borrowing);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    public void returnedBook(int id) {
        System.out.println("delete " + id);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            Borrowing borrowing = session.find(Borrowing.class, id);
            borrowing.setReturned(1);
            session.merge(borrowing);
            //session.remove(book);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

}
