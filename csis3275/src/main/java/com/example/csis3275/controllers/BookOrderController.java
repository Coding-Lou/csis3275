package com.example.csis3275.controllers;

import com.example.csis3275.entities.BookOrder;
import com.example.csis3275.entities.ExperienceInstance;
import com.example.csis3275.entities.User;
import com.example.csis3275.repositories.BookOrderRepository;
import com.example.csis3275.repositories.ExperienceInstanceRepository;
import com.example.csis3275.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping(path =  {"/", "/index" })
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

    @PostMapping(path="/saveBookOrder")
    public String saveBookOrder(
            @Valid @ModelAttribute("bookOrder") BookOrder bookOrder,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bookOrder.getOrderId() == null) {
            redirectAttributes.addFlashAttribute("message", "Unable to create new book order.");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/index";
        }

        if (bookOrder.getUser() == null) {
            bookOrder.setUser(new User());}
        if (bookOrder.getExperienceInstance() == null) {
            bookOrder.setExperienceInstance(new ExperienceInstance());
        }

        if (bindingResult.hasErrors()) {
            return "editBookOrders";
        }

        BookOrder existingBookOrder = bookOrderRepository.findById(bookOrder.getOrderId()).orElse(null);
        if (existingBookOrder == null) {
            redirectAttributes.addFlashAttribute("message", "record not found");
            redirectAttributes.addFlashAttribute("messageType", "error");
            return "redirect:/index";
        }
        bookOrder.setCreatedAt(existingBookOrder.getCreatedAt());

        User userToSet = null;
        if (bookOrder.getUser().getId() != null) {
            userToSet = userRepository.findById(bookOrder.getUser().getId()).orElse(null);
        } else {
            bindingResult.rejectValue("user.id", "error.bookOrder","User id not found");
        }
        if (userToSet == null && bookOrder.getUser().getId() != null) {
            bindingResult.rejectValue("user.id", "error.bookOrder","User id invalid");
        }

        ExperienceInstance experienceInstanceToSet = null;
        if (bookOrder.getExperienceInstance().getId() != null) {
            experienceInstanceToSet = experienceInstanceRepository.findById(bookOrder.getExperienceInstance().getId()).orElse(null);
        } else {
            bindingResult.rejectValue("experienceInstance.id", "error.bookOrder","ExperienceInstance id not found");
        }
        if (experienceInstanceToSet == null && bookOrder.getExperienceInstance().getId() != null) { // 如果ID不为空但未找到体验实例
            bindingResult.rejectValue("experienceInstance.id", "error.bookOrder", "invalid experienceInstance id。");
        }

        if (bindingResult.hasErrors()) {
            if (bookOrder.getUser().getId() != null) {bookOrder.setUser(new User());}
            if (bookOrder.getExperienceInstance() == null) {bookOrder.setExperienceInstance(new ExperienceInstance());}
            return "editBookOrders";
        }

        bookOrder.setUser(userToSet);
        bookOrder.setExperienceInstance(experienceInstanceToSet);
        bookOrder.setUpdatedAt(LocalDateTime.now());

        bookOrderRepository.save(bookOrder);

        redirectAttributes.addFlashAttribute("message","Updated Book Order successfully.");
        redirectAttributes.addFlashAttribute("messageType", "success");

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

