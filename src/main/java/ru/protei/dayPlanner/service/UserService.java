package ru.protei.dayPlanner.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.protei.dayPlanner.model.PasswordResetToken;
import ru.protei.dayPlanner.model.Privilege;
import ru.protei.dayPlanner.model.Role;
import ru.protei.dayPlanner.model.User;
import ru.protei.dayPlanner.repository.PasswordTokenRepository;
import ru.protei.dayPlanner.repository.RoleRepository;
import ru.protei.dayPlanner.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    @Autowired
    public RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Qualifier("messageSource") // todo verify qualifier
    @Autowired
    private MessageSource messages;

    @Autowired
    private Environment env;

    @Transactional
    public boolean checkPrivilege(User user, String privilege) {
        return user.getRoles()
                .stream().filter(role -> role.getPrivileges()
                        .stream().filter(privilegeFromRole -> privilegeFromRole.getName().equals(privilege)).findFirst().isPresent())
                .findFirst().isPresent();
    }


    @Transactional
    public Collection<Privilege> getAllPrivilegesFromUser(User user) {

        User userFromRepo = userRepository.findByUsernameEquals(user.getUsername());
        if (userFromRepo == null) {
            return null;
        }
        Collection<Privilege> privilegeCollection = new ArrayList<>();
        for (Role role : userFromRepo.getRoles()) {
            for (Privilege privilege : role.getPrivileges()) {
                privilegeCollection.add(privilege);
            }
        }
        return privilegeCollection;
    }

    @Transactional
    public void createUserIfNotFound(
            User user) {

        User userFromRepo = userRepository.findByUsernameEquals(user.getUsername());
        if (userFromRepo == null) {
            userRepository.save(user);
        }
    }

    @Transactional
    public User createUser(String username, String password) {
        User userToCreate = new User();
        userToCreate.setUsername(username);
        userToCreate.setPassword(passwordEncoder.encode(password));
        userToCreate.setLdapName(username);
        userToCreate.setEmail(username + "@protei.ru");
        userToCreate.setEnabled(true);

        User userFromRepo = userRepository.findByUsernameEquals(userToCreate.getUsername());
        if (userFromRepo == null) {
            return userRepository.save(userToCreate);
        } else {
            return null;
        }
    }

    public void changeUserPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Transactional
    public void deleteAll() {
        logger.warn("Removing all users!!!");

        userRepository.deleteAll();
    }

    @Transactional
    public User getUserByEmail(String email) {
        return userRepository.findByEmailEquals(email);
    }

    @Transactional
    public User getUserByName(String username) {
        return userRepository.findByUsernameEquals(username);
    }

    @Transactional
    public Optional<User> getUserByPasswordResetToken(String token) {
        return Optional.ofNullable(passwordTokenRepository.findByTokenEquals(token).getUser());
    }

    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }

    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByTokenEquals(token);
        return !isTokenFound(passToken) ? "invalidToken"
                : isTokenExpired(passToken) ? "expired"
                : null;
    }

    @Transactional
    public void removePasswordResetToken(String token) {
        passwordTokenRepository.deleteByTokenEquals(token);
    }

    private boolean isTokenFound(PasswordResetToken passToken) {
        return passToken != null;
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        final Calendar cal = Calendar.getInstance();
        return passToken.getExpiryDate().before(cal.getTime());
    }

    public SimpleMailMessage constructResetTokenEmail(
            String contextPath, Locale locale, String token, User user) {
        String url = contextPath + "/user/changePassword?token=" + token;
        String message = messages.getMessage("message.resetPassword",
                null, locale);
        return constructEmail(message, message + " \r\n" + url, user);
    }

    private SimpleMailMessage constructEmail(String subject, String body,
                                             User user) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(user.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }


}
