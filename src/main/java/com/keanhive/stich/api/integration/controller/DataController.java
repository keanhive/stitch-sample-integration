package com.keanhive.stich.api.integration.controller;

import com.keanhive.stich.api.integration.enums.ProcessTypeEnum;
import com.keanhive.stich.api.integration.restcall.request.DataRequestPojo;
import com.keanhive.stich.api.integration.service.DataService;
import com.keanhive.stich.api.integration.service.LinkPayService;
import com.keanhive.stich.api.integration.service.TokenGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/data")
public class DataController {

    @Autowired
    DataService dataService;

    @Autowired
    TokenGenerationService tokenGenerationService;

    @Autowired
    LinkPayService linkPayService;

    @GetMapping("/get-accounts")
    public ResponseEntity<String> getAccounts() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(dataService.initiateTokenSession(ProcessTypeEnum.GET_ACCOUNTS, null));
    }

    @GetMapping("/get-accounts-holders")
    public ResponseEntity<String> getAccountHolders() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(dataService.initiateTokenSession(ProcessTypeEnum.GET_ACCOUNT_HOLDERS, null));
    }

    @GetMapping("/get-accounts-balance")
    public ResponseEntity<String> getAccountBalance() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(dataService.initiateTokenSession(ProcessTypeEnum.GET_ACCOUNT_BALANCE, null));
    }

    @GetMapping("/get-transactions-by-bank-account")
    public ResponseEntity<String> getTransactionsByBankAccountId(@Valid @RequestBody DataRequestPojo dataRequestPojo) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(dataService.initiateTokenSession(ProcessTypeEnum.GET_TRANSACTION_BY_ACCOUNT, dataRequestPojo));
    }

    @GetMapping("/get-debit-order-by-bank-account")
    public ResponseEntity<String> getDebitOrderByBankAccountId(@Valid @RequestBody DataRequestPojo dataRequestPojo) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(dataService.initiateTokenSession(ProcessTypeEnum.GET_DEBIT_ORDER_BY_ACCOUNT, dataRequestPojo));
    }

    @GetMapping("/retrieve-token")
    public void retrieveToken(@RequestParam(required = false) Map<String, String> qparams) {
        tokenGenerationService.retrieveToken(qparams);
    }
}
