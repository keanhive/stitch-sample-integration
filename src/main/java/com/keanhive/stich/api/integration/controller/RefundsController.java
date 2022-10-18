package com.keanhive.stich.api.integration.controller;

import com.keanhive.stich.api.integration.restcall.request.RefundRequestPojo;
import com.keanhive.stich.api.integration.service.RefundService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author KeanHive
 * @Date 18/10/2022
 */



@Slf4j
@RestController
@RequestMapping("api/refunds")
public class RefundsController {

    @Autowired
    RefundService refundService;

    @GetMapping("")
    public ResponseEntity<String> getTransactionsByBankAccountId(@Valid @RequestBody RefundRequestPojo refundRequestPojo) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(refundService.createRefund(refundRequestPojo));
    }

    @GetMapping("/status")
    public ResponseEntity<String> getTransactionsByBankAccountId(@RequestParam(name="refundId", required=false) String refundId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(refundService.getRefundStatus(refundId));
    }

    @GetMapping("/subscribe/web-hook")
    public ResponseEntity<String> subscribeWebHook() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(refundService.subscribeToWebHook());
    }
}
