package com.keanhive.stich.api.integration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author KeanHive
 * @Date 18/10/2022
 */


@Slf4j
@Service
public class WebHookService {
    public void handleWebHook(Map<String, String> qparams) {
        log.debug("handleWebHook details {}", qparams);
    }
}
