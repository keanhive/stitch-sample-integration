package com.keanhive.stich.api.integration.akka;

import akka.actor.UntypedAbstractActor;
import com.keanhive.stich.api.integration.pojos.UserSessionInfo;
import com.keanhive.stich.api.integration.service.DataService;
import com.keanhive.stich.api.integration.service.LinkPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UserSessionActor extends UntypedAbstractActor {

    private final LinkPayService linkPayService;
    private final DataService dataService;

    public UserSessionActor(LinkPayService linkPayService, DataService dataService) {
        this.linkPayService = linkPayService;
        this.dataService = dataService;
    }

    @Override
    public void onReceive(Object message) {
        log.debug("message recieved {}", message);

        if (!(message instanceof UserSessionInfo)) {
            unhandled(message);
            return;
        }

        UserSessionInfo userSessionInfo = (UserSessionInfo) message;

        switch (userSessionInfo.getProcessTypeEnum()) {
            case GET_ACCOUNTS: {
                getSender().tell(dataService.getAccounts((UserSessionInfo) message), getSelf());
                return;
            }
            case GET_ACCOUNT_HOLDERS: {
                getSender().tell(dataService.getAccountHolders((UserSessionInfo) message), getSelf());
                return;
            }
            case GET_ACCOUNT_BALANCE: {
                getSender().tell(dataService.getAccountBalance((UserSessionInfo) message), getSelf());
                return;
            }
            case GET_TRANSACTION_BY_ACCOUNT: {
                getSender().tell(dataService.getTransactionsByBankAccountId((UserSessionInfo) message), getSelf());
                return;
            }
            case GET_DEBIT_ORDER_BY_ACCOUNT: {
                getSender().tell(dataService.getDebitOrderByBankAccountId((UserSessionInfo) message), getSelf());
                return;
            }
            case LINK_PAY: {
                getSender().tell(linkPayService.handleStep2((UserSessionInfo) message), getSelf());
                break;
            }
            default: {
                unhandled(message);
                break;
            }
        }

    }

}
