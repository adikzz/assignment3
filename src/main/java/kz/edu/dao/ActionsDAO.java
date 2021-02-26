package kz.edu.dao;

import kz.edu.model.Action;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Repository
public class ActionsDAO {
    private SessionFactory sessionFactory;
    Session session;
    List<Action> actionList;

    @Autowired
    public ActionsDAO(SessionFactory sessionFactory)
    {
        this.sessionFactory = sessionFactory;
    }

    public List<Action> getAllActions() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        try {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<Action> criteria = builder.createQuery(Action.class);
            Root<Action> root = criteria.from(Action.class);

            criteria.select(root);
            Query<Action> query = session.createQuery(criteria);
            actionList = query.getResultList();
            session.getTransaction().commit();
        } finally {
            session.close();
        }

        Collections.sort(actionList, new Comparator<Action>() {
            @Override
            public int compare(Action o1, Action o2) {
                return o1.getCreateTime().compareTo(o2.getCreateTime());
            }
        });

        return actionList;
    }

    public void addAction(Action action) {
        try {
            session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(action);
            session.getTransaction().commit();
        } finally {
            session.close();
        }
    }

}
