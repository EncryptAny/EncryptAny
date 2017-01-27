package xyz.encryptany.encryptany.interfaces;

/**
 * Created by Max on 1/31/2017.
 */

public interface MessageCodecStrategy {
    Message parseText(String rawText);
    String encodeText(Message msg);
}
