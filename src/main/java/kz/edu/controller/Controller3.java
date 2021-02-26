package kz.edu.controller;

import kz.edu.dao.ActionsDAO;
import kz.edu.dao.BookDAO;
import kz.edu.dao.BorrowingDAO;
import kz.edu.dao.UserDAO;
import kz.edu.model.Action;
import kz.edu.model.Book;
import kz.edu.model.Borrowing;
import kz.edu.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/users")
public class Controller3 {
    private final UserDAO userDAO;
    private final BorrowingDAO borrowingDAO;
    private final BookDAO bookDAO;
    private final ActionsDAO actionsDAO;
    @Autowired
    public Controller3(@Qualifier("userDAO") UserDAO userDAO,
                       @Qualifier("borrowingDAO") BorrowingDAO borrowingDAO,
                       @Qualifier("bookDAO") BookDAO bookDAO,
                       @Qualifier("actionsDAO") ActionsDAO actionsDAO) {
        this.userDAO = userDAO;
        this.borrowingDAO = borrowingDAO;
        this.bookDAO = bookDAO;
        this.actionsDAO = actionsDAO;
    }

    @GetMapping()
    public String userList(Model model) {
        model.addAttribute("usersList", userDAO.getUserList());
        return "users-list";
    }

    @GetMapping("/{id}")
    public String book(@ModelAttribute("borrowing") Borrowing borrowing, @PathVariable("id") int id, Model model) {
        User user = userDAO.findByUserId(id);
        model.addAttribute("user", user);
        model.addAttribute("borrowingList", borrowingDAO.getTakenBookList(user));
        return "user-page";
    }

    @GetMapping("/edit/{id}")
    public String updateUser(@PathVariable("id") int id, Model model) {
        model.addAttribute("user", userDAO.findByUserId(id));
        return "user-edit";
    }

    @PatchMapping("/{id}")
    public String updateUserPatch(@ModelAttribute("user") @Valid User user, BindingResult bindingResult, @PathVariable("id") int id) {
        if (bindingResult.hasErrors())
            return "user-edit";

        userDAO.updateUser(user);
        return "redirect:/users/"+user.getId();
    }

    @DeleteMapping("/{id}")
    public String deleteUserPatch(@PathVariable("id") int id) {
        User user = userDAO.findByUserId(id);
        System.out.println(borrowingDAO.getTakenBookList(user).isEmpty());
        if (borrowingDAO.getTakenBookList(user).isEmpty()) {
            userDAO.deleteUser(id);
        } else {
            System.out.println("User cannot be deleted");
        }
        return "redirect:/users";
    }

    @PutMapping("/{id}")
    public String addBookToUser(@ModelAttribute("borrowing") @Valid Borrowing borrowing, @PathVariable("id") int id, Model model) {

        if (borrowing.getBook_id() == 0) {
            return "redirect:/users/"+id;
        }

        Book book1 = bookDAO.getBook(borrowing.getBook_id());
        if (book1.getCopies() == 0) {
            return "redirect:/users/"+id;
        }

        borrowing.setUser_id(id);
        borrowing.setReturned(0);
        borrowingDAO.addBookToUser(borrowing);

        Book book = bookDAO.getBook(borrowing.getBook_id());
        int copies = book.getCopies();
        book.setCopies(copies-1);
        bookDAO.updateBook(book);

        System.out.println("Action start");
        Action action = new Action();
        action.setAction_message("User "+ userDAO.findByUserId(borrowing.getUser_id()).getId()+" took book with isbn="+book.getId());
        actionsDAO.addAction(action);

        return "redirect:/users/"+id;
    }

    @DeleteMapping("/{id1}/{id2}")
    public String returnBook(@PathVariable("id1") int id1, @PathVariable("id2") int id2) {
        System.out.println(id1+" "+id2);
        borrowingDAO.returnedBook(id2);

        Borrowing borrowing = borrowingDAO.getBorrowing(id2);

        Book book = bookDAO.getBook(borrowing.getBook_id());
        int copies = book.getCopies();
        book.setCopies(copies+1);
        bookDAO.updateBook(book);

        Action action = new Action();
        action.setAction_message("User "+ userDAO.findByUserId(borrowing.getUser_id()).getId()+" retrieved book with isbn="+book.getId());
        actionsDAO.addAction(action);

        return "redirect:/users/"+id1;
    }
}
