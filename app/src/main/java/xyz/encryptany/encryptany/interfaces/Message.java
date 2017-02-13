package xyz.encryptany.encryptany.interfaces;

import java.util.Date;

/**
 * Created by dakfu on 1/26/2017.
 */

public interface Message {
    public String getMessage();
    public String getOtherParticipant();
    public String getApp();
    public Date getDate();
}
