package com.keanhive.stich.api.integration.controller;

import com.keanhive.stich.api.integration.service.WebHookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author KeanHive
 * @Date 18/10/2022
 */



@Slf4j
@RestController
@RequestMapping("api/web-hooks")
public class WebHookController {

    @Autowired
    WebHookService webHookService;

    @GetMapping("")
    public void getTransactionsByBankAccountId(@RequestParam(required = false) Map<String, String> qparams) {
        webHookService.handleWebHook(qparams);
    }

}
