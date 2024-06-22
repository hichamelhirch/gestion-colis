package org.sid.creationcolis.payment;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class TokenService {

    public String generatePaymentToken() {
        Random random = new Random();
        int randomNumber = 10000000 + random.nextInt(90000000);
        return "cp" + randomNumber;
    }
}
