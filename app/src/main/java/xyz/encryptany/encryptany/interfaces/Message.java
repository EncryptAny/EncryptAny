package xyz.encryptany.encryptany.interfaces;

import java.util.Date;

/**
 * Created by dakfu on 1/26/2017.
 */

public interface Message {
    String getMessage();
    String getOtherParticpant();
    String getApp();
    long getDate();
    String uuid();
    String getIV();
}
