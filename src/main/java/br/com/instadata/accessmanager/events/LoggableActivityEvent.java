package br.com.instadata.accessmanager.events;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;

@Getter
public class LoggableActivityEvent extends ApplicationEvent {

    private final String actionKey;
    private final String description;
    private final Integer userId;
    private final Integer companyId;
    private final String ipAddress;

    public LoggableActivityEvent(
            Object source,
            String actionKey,
            String description,
            Integer userId,
            Integer companyId,
            String ipAddress) {

        super(source);
        this.actionKey = actionKey;
        this.description = description;
        this.userId = userId;
        this.companyId = companyId;
        this.ipAddress = ipAddress;
    }
}