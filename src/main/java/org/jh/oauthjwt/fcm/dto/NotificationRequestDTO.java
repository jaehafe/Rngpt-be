package org.jh.oauthjwt.fcm.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class NotificationRequestDTO {

    private String token;
    private String title;
    private String body;
}
