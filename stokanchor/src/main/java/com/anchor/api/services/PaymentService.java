package com.anchor.api.services;


import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class PaymentService {
    public static final Logger LOGGER = Logger.getLogger(PaymentService.class.getSimpleName());

    public PaymentService() {
        LOGGER.info("\uD83C\uDF0D \uD83C\uDF0D PaymentService Constructor fired ... \uD83C\uDF0D");
    }
}
