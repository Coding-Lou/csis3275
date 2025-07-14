package com.example.csis3275.web;

import com.example.csis3275.entities.BookOrder;
import com.example.csis3275.entities.ExperienceInstance;
import com.example.csis3275.entities.User;
import com.example.csis3275.repositories.BookOrderRepository;
import com.example.csis3275.repositories.ExperienceInstanceRepository;
import com.example.csis3275.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@AllArgsConstructor
public class BookOrderController {
    @Autowired
    private BookOrderRepository bookOrderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExperienceInstanceRepository experienceInstanceRepository;

    @GetMapping(path = "/index")
    public String bookOrders(Model model,@RequestParam(name = "keyword",defaultValue = "")String keyword) {

        List<BookOrder> bookOrders;
        if (keyword.isEmpty()) {
            bookOrders = bookOrderRepository.findAll();
        } else {
            try {
                Long key = Long.parseLong(keyword);
                bookOrders = bookOrderRepository.findBookOrdersByOrderId(key);
            } catch (NumberFormatException e) {
                bookOrders = bookOrderRepository.findAll();
                model.addAttribute("searchError", "Invalid keyword. Please enter a valid Order ID.");
            }
        }
        model.addAttribute("listBookOrders", bookOrders);
        return "bookOrders";
    }
    @GetMapping("/delete")
    public String delete(Long id) {
        bookOrderRepository.deleteById(id);
        return "redirect:/index";
    }
    @GetMapping("/formBookOrders")
    public String formBookOrders(Model model) {
        BookOrder bookOrder = new BookOrder();
        bookOrder.setUser(new User());
        bookOrder.setExperienceInstance(new ExperienceInstance());
        model.addAttribute("bookOrder", new BookOrder());
        return "formBookOrders";
    }
    @PostMapping(path="/saveBookOrder")
    public String saveBookOrder(
            @Valid @ModelAttribute("bookOrder") BookOrder bookOrder,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            if (bookOrder.getUser() == null) {
                bookOrder.setUser(new User());}
            if (bookOrder.getExperienceInstance() == null) {bookOrder.setExperienceInstance(new ExperienceInstance());}
            return "formBookOrders";
        }
        User user = null;
        if (bookOrder.getUser() != null && bookOrder.getUser().getId() != null) {
            user = userRepository.findById(bookOrder.getUser().getId())
                    .orElse(null);
        }
        ExperienceInstance experienceInstance = null;
        if (bookOrder.getExperienceInstance() != null && bookOrder.getExperienceInstance().getId() != null) {
            experienceInstance = experienceInstanceRepository.findById(bookOrder.getExperienceInstance().getId()).orElse(null);
        }
        if (user == null) {
            bindingResult.rejectValue("user.id", "error.bookOrder","Invalid user id");
            model.addAttribute("bookOrder", bookOrder);
            if (bookOrder.getUser() == null) {bookOrder.setUser(new User());}
            if (experienceInstance != null) {bookOrder.setExperienceInstance(new ExperienceInstance());
            return "formBookOrders";}
        }
        if (experienceInstance == null) {
            bindingResult.rejectValue("experienceInstance.id", "error.bookOrder","Invalid experience instance id");
            model.addAttribute("bookOrder", bookOrder);
            if (bookOrder.getUser() == null) {bookOrder.setUser(new User());}
            if (experienceInstance != null) {bookOrder.setExperienceInstance(new ExperienceInstance());}
            return "formBookOrders";
        }
        bookOrder.setUser(user);
        bookOrder.setExperienceInstance(experienceInstance);

        if (bookOrder.getUser() == null) {
            bookOrder.setCreatedAt(LocalDateTime.now());
            redirectAttributes.addFlashAttribute("message", "Book Order updated successfully");
            redirectAttributes.addFlashAttribute("messageType", "sucess");
        }
        bookOrder.setUpdatedAt(LocalDateTime.now());
        return "redirect:/index";
    }

    @GetMapping("/editBookOrders")
    public String editStudents(@RequestParam("id") Long id, Model model, RedirectAttributes redirectAttributes){
        BookOrder bookOrder = bookOrderRepository.findById(id).orElse(null);
        if(bookOrder==null) {
            redirectAttributes.addFlashAttribute("message", "Book Order with id "+id+" not found");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/index";
        }
        model.addAttribute("bookOrder", bookOrder);
        return "editBookOrders";
    }

}

