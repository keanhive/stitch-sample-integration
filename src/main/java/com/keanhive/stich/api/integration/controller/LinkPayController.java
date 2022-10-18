package com.keanhive.stich.api.integration.controller;

import com.keanhive.stich.api.integration.cache.CacheService;
import com.keanhive.stich.api.integration.restcall.request.LinkPaymentRequestPojo;
import com.keanhive.stich.api.integration.service.InstantPayService;
import com.keanhive.stich.api.integration.service.LinkPayService;
import com.keanhive.stich.api.integration.service.TokenGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/link-pay")
public class LinkPayController {

    @Autowired
    InstantPayService instantPayService;

    @Autowired
    TokenGenerationService tokenGenerationService;

    @Autowired
    LinkPayService linkPayService;

    @Autowired
    CacheService cacheService;

    @GetMapping("")
    public void linkPay(@Valid @RequestBody LinkPaymentRequestPojo linkPaymentRequestPojo) {
        linkPayService.handleStep1(linkPaymentRequestPojo);
    }

    @GetMapping("/update")
    public ModelAndView greeting(@RequestParam(name="update", required=false) String update,
                                 @RequestParam(name="cachekey", required=false) String cacheKey) {

//        log.debug("cacheKey data: {}", cacheService.getItem(cacheKey));
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("update.html");
        modelAndView.addObject("update", StringUtils.isNotBlank(cacheKey) ? cacheService.getItem(cacheKey) : update);
        return modelAndView;
    }

    @GetMapping("/multifactor")
    public ModelAndView linkPayMultifactor(@RequestParam(required = false) Map<String, String> qparams) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("result.html");

        linkPayService.linkPayMultifactor(qparams, modelAndView);

        return modelAndView;
    }

    @GetMapping("/cancel")
    public String linkPayCancelPayment(@RequestParam(required = false) Map<String, String> qparams) {
        return linkPayService.cancelPayment(qparams);
    }

    @GetMapping("/status")
    public String linkPayGetStatus(@RequestParam(required = false) Map<String, String> qparams) {
        return linkPayService.getStatus(qparams);
    }

    @GetMapping("/retrieve-token")
    public void retrieveToken(@RequestParam(required = false) Map<String, String> qparams) {
        tokenGenerationService.retrieveToken(qparams);
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
