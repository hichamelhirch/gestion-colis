package org.sid.creationcolis.paymentStripe;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @GetMapping("/success")
    public String paymentSuccess() {

        return "payment_success";
    }

    @GetMapping("/cancel")
    public String paymentCancel() {
        return "payment_cancel";
    }
}
