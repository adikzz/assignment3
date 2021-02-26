package kz.edu.controller;

import kz.edu.dao.ActionsDAO;
import kz.edu.dao.BorrowingDAO;
import kz.edu.dao.UserDAO;
import kz.edu.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class Controller2 {
    private final UserDAO userDAO;
    private final BorrowingDAO borrowingDAO;
    private final ActionsDAO actionsDAO;
    @Autowired
    public Controller2(@Qualifier("userDAO") UserDAO userDAO,
                       @Qualifier("borrowingDAO") BorrowingDAO borrowingDAO,
                       @Qualifier("actionsDAO") ActionsDAO actionsDAO) {
        this.userDAO = userDAO;
        this.borrowingDAO = borrowingDAO;
        this.actionsDAO = actionsDAO;
    }

    PasswordEncoder passwordEncoder;

    @Autowired
    public void PasswordEncoder(PasswordEncoder passwordEncoder) { this.passwordEncoder = passwordEncoder;}

    @RequestMapping(value={"", "/", "home"})
    public String home()
    {
        return "home";
    }

    @GetMapping("/arrivals")
    public String arrivals(Model model)
    {
        model.addAttribute("actionList", actionsDAO.getAllActions());
        return "arrivals";
    }

    @GetMapping("/login")
    public String login()
    {
        return "login";
    }

    @GetMapping("/registration")
    public String registration()
    {
        return "registration";
    }

    @PostMapping("/registration")
    public String addUser(User user, @RequestParam("username") String email, Model model) {
        System.out.println("REGISTRATION:" + email);

        if (userDAO.findByUserName(email) != null && userDAO.findByUserName(email).isActive()) {
            model.addAttribute("message", "User with such email exists!");
            return "registration";
        } else if (userDAO.findByUserName(email) != null && !userDAO.findByUserName(email).isActive()) {
            User userTemp = userDAO.findByUserName(email);
            userTemp.setActive(true);
            userDAO.recreateUser(userTemp);
            return "home";
        } else {
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userDAO.addUser(user);
            return "redirect:/home";
        }
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        String currentUserName = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            currentUserName = authentication.getName();
        }
        User user = userDAO.findByUserName(currentUserName);
        model.addAttribute("user", user);
        model.addAttribute("borrowingList", borrowingDAO.getTakenBookList(user));
        return "profile";
    }
}