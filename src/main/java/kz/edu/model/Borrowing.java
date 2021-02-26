package kz.edu.model;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "BorrowingEntity")
@Table(name = "borrowings")
public class Borrowing {

    private int borrowing_id;
    private int user_id;
    private int book_id;
    private int returned;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId()
    {
        return this.borrowing_id;
    }
    public void setId(int borrowing_id)
    {
        this.borrowing_id = borrowing_id;
    }

    @Column(name = "user_id")
    public int getUser_id() { return user_id; }
    public void setUser_id(int user_id) { this.user_id = user_id; }

    @Column(name = "book_id")
    public int getBook_id() { return book_id; }
    public void setBook_id(int book_id) { this.book_id = book_id; }

    @Column(name = "returned")
    public int getReturned() { return returned; }
    public void setReturned(int returned) { this.returned = returned; }
}
