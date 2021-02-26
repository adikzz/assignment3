package kz.edu.dao;

import com.google.gson.Gson;
import kz.edu.model.Book;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

@Component
public class BookDAO {

    private SessionFactory sessionFactory;
    Session session;
    List<Book> booksList;

    @Autowired
    public BookDAO(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    public List<Book> getBookList() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Book> criteria = builder.createQuery(Book.class);
            Root<Book> root = criteria.from(Book.class);

            Predicate predicateBook = builder.equal(root.get("deleted"), 0);

            criteria.select(root).where(predicateBook);
            Query<Book> query = session.createQuery(criteria);
            booksList = query.getResultList();
            session.getTransaction().commit();
        } finally {
            session.close();
        }
        return booksList;
    }

    public Book getBook(int id) {
        session = sessionFactory.openSession();
        session.beginTransaction();
        Book book;
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Book> q1 = builder.createQuery(Book.class);
            Root<Book> root = q1.from(Book.class);

            Predicate predicateBook = builder.equal(root.get("id"), id);
            book = session.createQuery(q1.where(predicateBook)).getSingleResult();
            session.getTransaction().commit();
        } catch (NoResultException noResultException) {
            book = null;
        } finally {
            session.close();
        }
        return book;
    }

    public void incBook(Book book, int copies) {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            book.setCopies(copies);
            session.merge(book);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    public void addBook(Book book) {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(book);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    public void updateBook(Book book) {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.merge(book);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

    public void deleteBook(int bookId) {
        System.out.println("delete " + bookId);
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            Book book = session.find(Book.class, bookId);
            System.out.println("The author of the book to be deleted: " + book.getAuthor());
            book.setDeleted(1);
            session.merge(book);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }
}
