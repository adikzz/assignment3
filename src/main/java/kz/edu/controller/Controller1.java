package kz.edu.controller;

import kz.edu.dao.BookDAO;
import kz.edu.model.Book;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/books")
public class Controller1 {
    private final BookDAO bookDAO;
    @Autowired
    public Controller1(BookDAO bookDAO)
    {
        this.bookDAO = bookDAO;
    }

    @GetMapping()
    public String helloPage(Model model) {
        model.addAttribute("booksList", bookDAO.getBookList());
        return "page-1";
    }

    @GetMapping("/{id}")
    public String book(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", bookDAO.getBook(id));
        return "page-2";
    }

    @GetMapping("/addBook")
    public String addBookGet(@ModelAttribute("book") Book book) {
        return "page-3";
    }

    @PostMapping()
    public String addBookPost(@ModelAttribute("book") @Valid Book book,
                              BindingResult bindingResult) {
        if (bindingResult.hasErrors())
            return "page-3";

        System.out.println(book.getId()+" "+book.getName()+" "+
                book.getAuthor()+" "+book.getGenre()+" "+book.getImageURL()+" "+
                book.getCopies());

        if (bookDAO.getBook(book.getId()) == null) {
            book.setDeleted(0);
            bookDAO.addBook(book);
        } else if (bookDAO.getBook(book.getId()).isDeleted() == 1) {
            book.setDeleted(0);
            bookDAO.updateBook(book);
        } else {
            Book prevBook = bookDAO.getBook(book.getId());
            int incCopies = book.getCopies() + prevBook.getCopies();
            bookDAO.incBook(prevBook, incCopies);
        }
        return "redirect:/books/"+book.getId();
    }

    @GetMapping("/edit/{id}")
    public String updateBook(@PathVariable("id") int id, Model model) {
        model.addAttribute("book", bookDAO.getBook(id));
        return "page-4";
    }

    @PatchMapping("/{id}")
    public String updateBookPatch(@ModelAttribute("book") @Valid Book book, BindingResult bindingResult, @PathVariable("id") int id) {
        if (bindingResult.hasErrors())
            return "page-4";

        bookDAO.updateBook(book);
        return "redirect:/books/"+book.getId();
    }

    @DeleteMapping("/{id}")
    public String deleteBookPatch(@PathVariable("id") int id) {
        bookDAO.deleteBook(id);
        return "redirect:/books";
    }
}