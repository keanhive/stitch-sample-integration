package com.keanhive.stich.api.integration.controller;

import com.keanhive.stich.api.integration.service.InstantPayService;
import com.keanhive.stich.api.integration.service.LinkPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/link-pay")
public class LinkPayController {

    @Autowired
    InstantPayService instantPayService;

    @Autowired
    LinkPayService linkPayService;

    @GetMapping("")
    public void linkPay(@RequestParam(required = false) Map<String, String> qparams) {
        linkPayService.handleStep1(qparams);
    }


    @GetMapping("/multifactor")
    public void linkPayMultifactor(@RequestParam(required = false) Map<String, String> qparams) {
        linkPayService.linkPayMultifactor(qparams);
    }

    @GetMapping("/cancel")
    public String linkPayCancelPayment(@RequestParam(required = false) Map<String, String> qparams) {
        return linkPayService.cancelPayment(qparams);
    }

    @GetMapping("/status")
    public String linkPayGetStatus(@RequestParam(required = false) Map<String, String> qparams) {
        return linkPayService.getStatus(qparams);
    }

    @GetMapping("/status/all")
    public String getAllTransactionStatus() {
        return linkPayService.getAllTransactionStatus();
    }

    @GetMapping("/subscribe")
    public String getSubscribeToLinkPayUpdates() {
        return linkPayService.handleSubscribeToLinkPayUpdates();
    }

    @GetMapping("/get-subscription")
    public void handleSubscriptionsToLinkPayUpdates(@RequestParam(required = false) Map<String, String> qparams) {
        linkPayService.handleSubscriptionsToLinkPayUpdates(qparams);
    }
}
