package xyz.encryptany.encryptany.concrete;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import xyz.encryptany.encryptany.interfaces.Message;
import xyz.encryptany.encryptany.interfaces.MessageCodecStrategy;

/**
 * Created by Max on 1/31/2017.
 */

public class JSONMessageCodecStrategy implements MessageCodecStrategy {

    @Override
    public Message parseText(String rawText) {
        Gson gson = new Gson();
        EncryptedMessage rm;
        try {
            rm = gson.fromJson(rawText, EncryptedMessage.class);
        } catch (JsonSyntaxException e) {
            rm = null;
        }
        return rm;
    }

    @Override
    public String encodeText(Message msg) {
        Gson gson = new Gson();
        return gson.toJson(msg);
    }
}
