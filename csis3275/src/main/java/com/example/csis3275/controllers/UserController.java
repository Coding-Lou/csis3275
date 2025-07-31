package com.example.csis3275.controllers;

import com.example.csis3275.entities.Experience;
import com.example.csis3275.entities.Order;
import com.example.csis3275.entities.User;
import com.example.csis3275.entities.dto.UserDTO;
import com.example.csis3275.repositories.ExperienceRepository;
import com.example.csis3275.repositories.OrderRepository;
import com.example.csis3275.repositories.UserRepository;
import com.example.csis3275.services.AuthenticationService;
import com.example.csis3275.services.JwtService;
import com.example.csis3275.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/user")
@Controller
@AllArgsConstructor
public class UserController {
    @Autowired
    ExperienceRepository experienceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;


    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute UserDTO loginUserDto, Model model, HttpServletResponse response, HttpSession session, HttpServletRequest request) {
        try {
            User authenticatedUser = authenticationService.authenticate(loginUserDto);

            String jwtToken = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                    authenticatedUser.getUsername(),
                    authenticatedUser.getPasswordHash(),
                    java.util.Collections.emptyList()
            ));

            deleteAllPreviousCookies(request, response);

            Cookie cookie = new Cookie("user-token-" + authenticatedUser.getUsername(), jwtToken);

            cookie.setHttpOnly(true); 
            cookie.setSecure(false); 
            cookie.setPath("/");

            response.addCookie(cookie);

            Optional<User> optionalUser = userRepository.findByUsername(authenticatedUser.getUsername());

            if (optionalUser.isPresent()) {
                User user =  optionalUser.get();
                Cookie roleCookie = new Cookie("user-role-" + authenticatedUser.getUsername(), loginUserDto.getSessionRole());
                roleCookie.setHttpOnly(true);
                roleCookie.setSecure(false);
                roleCookie.setPath("/");
                roleCookie.setMaxAge(24 * 60 * 60); // 24 hours, same as JWT token
                response.addCookie(roleCookie);

                if (loginUserDto.getSessionRole().equals("admin")) {
                    if (user.isAdmin())
                        return "redirect:/admin";
                    else {
                        model.addAttribute("errorMessage", "User doesn't have admin role");
                        return "error";
                    }
                }

                if (loginUserDto.getSessionRole().equals("traveler"))
                    return "redirect:/user/traveler/" + authenticatedUser.getUsername();

                if (loginUserDto.getSessionRole().equals("guide"))
                    return "redirect:/user/guide/" + authenticatedUser.getUsername();

            }

            model.addAttribute("error", "Error in login");
            return "error";
        } catch (Exception e) {
            model.addAttribute("error", "Invalid credentials");
            return "error";
        }
    }


    @GetMapping("/traveler/{username}")
    public String getTravelerHome(HttpServletRequest request, @PathVariable String username, Model model) {

        if(!checkValidToken(request, username)) {
            model.addAttribute("errorMessage", "User with username '" + username + "' not logged in");
            return "error";
        }

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            model.addAttribute("errorMessage", "User with username '" + username + "' was not found.");
            return "error";
        }
        User user = userOptional.get();
        model.addAttribute("user", user);

        List<Order> orders = orderRepository.findOrdersByUserId(user.getId());

        model.addAttribute("orders", orders);

        List<Experience> experiences = experienceRepository.findAll();
        model.addAttribute("experiences", experiences);
        return "travellerHome";
    }

    @PostMapping("/traveler/{username}")
    public String postTravelerHome(HttpServletRequest request, @PathVariable String username, Model model, @RequestParam String search) {

        if(!checkValidToken(request, username)) {
            model.addAttribute("errorMessage", "User with username '" + username + "' not logged in");
            return "error";
        }

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            model.addAttribute("errorMessage", "User with username '" + username + "' was not found.");
            return "error";
        }
        User user = userOptional.get();
        model.addAttribute("user", user);

        List<Order> orders = orderRepository.findOrdersByUserId(user.getId());

        model.addAttribute("orders", orders);


        List<Experience> experiences = experienceRepository.findByTitleContainingIgnoreCase(search);
        model.addAttribute("experiences", experiences);
        return "travellerHome";
    }

    @GetMapping("/profile/{username}")
    public String getUserProfile(HttpServletRequest request, @PathVariable String username, Model model) {

        if(!checkValidToken(request, username)) {
            model.addAttribute("errorMessage", "User with username '" + username + "' not logged in");
            return "error";
        }
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            model.addAttribute("errorMessage", "User with username '" + username + "' was not found.");
            return "error";
        }
        model.addAttribute("user", userOptional.get());
        return "profile";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerUser", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String createUser(@ModelAttribute UserDTO userDTO, Model model) {
        String username = userDTO.getUsername();

        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            model.addAttribute("errorMessage", "User with '" + username + "' has already been created.");
            return "error";
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        userDTO.setPassword( encoder.encode(userDTO.getPassword()) );
        userRepository.save(userDTO.toUser());
        return "redirect:/";
    }


    @PostMapping("/delete")
    public String deleteUser(HttpServletRequest request, Model model, @RequestParam(name = "username", defaultValue = "") String username) {

        if(!checkValidToken(request, username)) {
            model.addAttribute("errorMessage", "User not logged in.");
            return "error";
        }
        if (!username.isEmpty()) {
            userRepository.deleteByUsername(username);
        }
        return "redirect:/getUser";
    }


    @GetMapping("/update/{username}")
    public String showUpdateForm(HttpServletRequest request, @PathVariable String username, Model model) {

        if(!checkValidToken(request, username)) {
            model.addAttribute("errorMessage", "User with username '" + username + "' not logged in.");
            return "error";
        }

        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "User not found.");
            return "error";
        }
        model.addAttribute("user", userOpt.get());
        return "profile-update";
    }



    @PostMapping("/update")
    public String updateUser(HttpServletRequest request, @ModelAttribute User updatedUser, Model model) {

        if(!checkValidToken(request, updatedUser.getUsername())) {
            model.addAttribute("errorMessage", "User with username '" + updatedUser.getUsername() + "' not logged in.");
            return "error";
        }

        Optional<User> existingUserOpt = userRepository.findById(updatedUser.getId());
        if (existingUserOpt.isEmpty()) {
            model.addAttribute("error", "User not found.");
            return "error";
        }

        User user = existingUserOpt.get();

        user.setEmail(updatedUser.getEmail());
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setPhone(updatedUser.getPhone());
        user.setCountry(updatedUser.getCountry());

        userRepository.save(user);
        return "redirect:/user/" + user.getUsername();
    }

    @GetMapping("/logout/{username}")
    public String logoutUser(@PathVariable String username, HttpServletResponse response, HttpSession session, HttpServletRequest request) {
        deleteAllPreviousCookies(request, response);
        return "redirect:/";
    }

    private void deleteAllPreviousCookies(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().startsWith("user-token")) {
                    Cookie deleteCookie = new Cookie(cookie.getName(), "");
                    deleteCookie.setPath("/");
                    deleteCookie.setMaxAge(0);
                    deleteCookie.setHttpOnly(true);
                    deleteCookie.setSecure(false);
                    response.addCookie(deleteCookie);
                }

                if( cookie.getName().startsWith("user-role")) {
                    Cookie deleteCookie = new Cookie(cookie.getName(), "");
                    deleteCookie.setPath("/");
                    deleteCookie.setMaxAge(0);
                    deleteCookie.setHttpOnly(true);
                    deleteCookie.setSecure(false);
                    response.addCookie(deleteCookie);
                }
            }
        }
    }

    private boolean checkValidToken(HttpServletRequest request, String username) {
        String token = "";
        for (Cookie cookie : request.getCookies()) {
            if (("user-token-" + username).equals(cookie.getName())) {
                token = cookie.getValue();
            }
            if (cookie.getName().equals("user-role-admin"))
                return true;
        }

        if(token.isEmpty()) return false;

        try {
            jwtService.extractUsername(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
