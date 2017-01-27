package xyz.encryptany.encryptany.concrete;

import com.google.gson.Gson;

import xyz.encryptany.encryptany.interfaces.Message;
import xyz.encryptany.encryptany.interfaces.MessageCodecStrategy;

/**
 * Created by Max on 1/31/2017.
 */

public class JSONMessageCodecStrategy implements MessageCodecStrategy {

    private static String exampleJSON = "{\"encryptedTxt\":\"secret text\",\"hash\":\"0235\",\"sender\":\"Max Fresonke\",\"unixTS\":1485845807}";
    @Override
    public Message parseText(String rawText) {
        Gson gson = new Gson();
        EncryptedMessage rm = gson.fromJson(rawText, EncryptedMessage.class);
        return rm;
    }

    @Override
    public String encodeText(Message msg) {
        Gson gson = new Gson();
        return gson.toJson(msg);
    }


}
