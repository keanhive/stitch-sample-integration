package com.keanhive.stich.api.integration.controller;

import com.keanhive.stich.api.integration.restcall.request.InstantPaymentRequestPojo;
import com.keanhive.stich.api.integration.service.InstantPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/instant-pay")
public class InstantPayController {

    @Autowired
    InstantPayService instantPayService;

    @GetMapping("")
    public void makePayment(@Valid @RequestBody InstantPaymentRequestPojo instantPayRequestPojo) {
        instantPayService.makePayment(instantPayRequestPojo);
    }

    @GetMapping("/conclude")
    public ModelAndView instantPay(@RequestParam(required = false) Map<String, String> qparams) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("instantpay.html");

        instantPayService.instantPayConclusion(qparams, modelAndView);

        return modelAndView;
    }

    @GetMapping("/web-hook")
    public void webHook(@RequestParam(required = false) Map<String, String> qparams) {
        instantPayService.handleWebHook(qparams);
    }

    @GetMapping("/subscribe/web-hook")
    public void subscribeWebHook() {
        instantPayService.subscribeToWebHook();
    }
}
