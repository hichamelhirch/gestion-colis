package org.sid.creationcolis.contact;

import org.sid.creationcolis.repository.UserRepository;
import org.sid.creationcolis.suivi.notifications.EmailNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    @Autowired
    private EmailNotificationService emailNotificationService;

    @Autowired
    private UserRepository userRepository;

    public void sendContactEmail(String subject, String messageContent) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        String to = "hichamelhirchgmi@gmail.com";
        String message = "Email de l'utilisateur : " + userEmail + "\n\n" + messageContent;

        emailNotificationService.sendEmail(to, subject, message);
    }
}

