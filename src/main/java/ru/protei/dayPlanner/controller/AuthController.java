package ru.protei.dayPlanner.controller;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.protei.dayPlanner.Utils.Utils;
import ru.protei.dayPlanner.exception.UserNotFoundException;
import ru.protei.dayPlanner.model.DTO.PasswordDto;
import ru.protei.dayPlanner.model.User;
import ru.protei.dayPlanner.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Controller
public class AuthController {
    Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    UserService userService;
    @Qualifier("messageSource") // todo verify qualifier
    @Autowired
    private MessageSource messages;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Utils utils;

    @RequestMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        if (request.getParameter("error") != null) {
            model.addAttribute("error", true);
        }
        if (request.getParameter("message") != null) {
            model.addAttribute("message", request.getParameter("message"));
        }
        if (request.getParameter("messageSuccess") != null) {
            model.addAttribute("messageSuccess", request.getParameter("messageSuccess"));
        }

        return "login";
    }

    @SneakyThrows
    @PostMapping("/user/resetPassword")
    public String resetPassword(HttpServletRequest request,
                                @RequestParam("email") String userEmail) {
        User user = userService.getUserByEmail(userEmail);
        if (user == null) {
            throw new UserNotFoundException(userEmail);
        }
        String token = UUID.randomUUID().toString();
        logger.info("Sending email with password reset token to '" + userEmail + "'");
        userService.createPasswordResetTokenForUser(user, token);
        mailSender.send(userService.constructResetTokenEmail(utils.getAppUrl(request),
                request.getLocale(), token, user));
        logger.info("Email sent successfully");
        return "/login";
    }

    @SneakyThrows
    @PostMapping("/user/resetSelfPassword")
    public String resetSelfPassword(HttpServletRequest request) {
        User user = userService.getUserByName(request.getRemoteUser());
        if (user == null) {
            throw new UserNotFoundException("");
        }
        return resetPassword(request, user.getEmail());
    }

    @GetMapping("/user/changePassword")
    public String showChangePasswordPage(Locale locale, Model model,
                                         @RequestParam("token") String token) {
        String result = userService.validatePasswordResetToken(token);
        if (result != null) {
            String message = messages.getMessage("auth.message." + result, null, locale);
            return "redirect:/login.html?lang="
                    + locale.getLanguage() + "&message=" + message;
        } else {
            model.addAttribute("token", token);
            return "changePassword";
        }
    }

    @PostMapping("/user/savePassword")
    public String savePassword(final Locale locale, @Valid PasswordDto passwordDto) {
        String result = userService.validatePasswordResetToken(passwordDto.getToken());

        if (result != null) {
            return "redirect:/login.html?lang="
                    + locale.getLanguage() + "&message=" + messages.getMessage(
                    "auth.message." + result, null, locale);
        }

        Optional user = userService.getUserByPasswordResetToken(passwordDto.getToken());
        if (user.isPresent()) {
            userService.changeUserPassword((User) user.get(), passwordDto.getNewPassword());
            userService.removePasswordResetToken(passwordDto.getToken());
            return "redirect:/login.html?lang="
                    + locale.getLanguage() + "&messageSuccess=" + messages.getMessage(
                    "message.resetPasswordSuc", null, locale);
        } else {
            return "redirect:/login.html?lang="
                    + locale.getLanguage() + "&message=" + messages.getMessage(
                    "auth.message.invalid", null, locale);
        }
    }


}
