package com.keanhive.stich.api.integration.akka;

import akka.actor.UntypedAbstractActor;
import com.keanhive.stich.api.integration.pojos.UserSessionInfo;
import com.keanhive.stich.api.integration.service.LinkPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class LinkPayActor extends UntypedAbstractActor {

    private final LinkPayService linkPayService;

    public LinkPayActor(LinkPayService linkPayService) {
        this.linkPayService = linkPayService;
    }

    @Override
    public void onReceive(Object message) {
        log.debug("message recieved {}", message);

        if (message instanceof UserSessionInfo) {
            getSender().tell(linkPayService.handleStep2((UserSessionInfo) message), getSelf());
        } else {
            unhandled(message);
        }
    }

}
