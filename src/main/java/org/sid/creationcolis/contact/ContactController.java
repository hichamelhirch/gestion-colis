package org.sid.creationcolis.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin("*")
public class ContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping("/send")
    public ResponseEntity<Void> sendContactEmail(@RequestBody ContactRequest contactRequest) {
        contactService.sendContactEmail(contactRequest.getSubject(), contactRequest.getMessage());
        return ResponseEntity.ok().build();
    }
}

