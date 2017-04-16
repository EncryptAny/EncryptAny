package xyz.encryptany.encryptany.concrete;

import android.util.Base64;
import android.util.Log;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.security.Security;

import xyz.encryptany.encryptany.interfaces.Encryptor;
import xyz.encryptany.encryptany.interfaces.Message;
import xyz.encryptany.encryptany.listeners.EncryptionListener;

import org.spongycastle.crypto.engines.AESFastEngine;
import org.spongycastle.crypto.modes.CBCBlockCipher;
import org.spongycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.spongycastle.crypto.params.KeyParameter;
import org.spongycastle.crypto.params.ParametersWithIV;
import org.spongycastle.crypto.digests.SHA256Digest;

/**
 * Created by Cory on 4/15/2017.
 */

public class Crypto implements Encryptor {
    private EncryptionListener encryptionListener;
    private DHKeyExchanger dhKeyExchanger;
    private AESmodule aesmodule;

    private static final String TAG = "Crypto";


    public Crypto() {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(),1);
        this.dhKeyExchanger = new DHKeyExchanger();
    }

    @Override
    public void initialization(Message message) {
        // This gets called to begin handshake

        String bigIntValue = "ENCRYPTANYEXCHANGE?:";
        bigIntValue += dhKeyExchanger.getPublicValue().toString(Character.MAX_RADIX);

        encryptionListener.sendEncryptedMessage(bigIntValue,message.getOtherParticpant(),message.getApp(),null);
        //optional: Can preserve key exchange by saving key value
        //aesmodule.getKey();
    }

    @Override
    public void encryptMessage(Message message) {
        IV iv = new IV();
        String s ="ENCRYPTANYEXCHANGE@:";
                s += this.aesmodule.encrypt(message.getMessage(),iv);

        encryptionListener.sendEncryptedMessage(s,message.getOtherParticpant(),message.getApp(),iv.get_s());
    }

    @Override
    public void decryptMessage(Message message) {
        String msg = message.getMessage();

        String preText = msg.substring(0,20);
        String content = msg.substring(20);
        if(preText.equals("ENCRYPTANYEXCHANGE?:")){
            BigInteger theirPublicValue = new BigInteger(content,Character.MAX_RADIX);
            this.aesmodule = new AESmodule(
                    dhKeyExchanger.getSecretValue(),
                    theirPublicValue,
                    dhKeyExchanger.getP());
            String bigIntValue = "ENCRYPTANYEXCHANGE!:";
            bigIntValue += dhKeyExchanger.getPublicValue().toString(Character.MAX_RADIX);

            encryptionListener.sendEncryptedMessage(bigIntValue,message.getOtherParticpant(),message.getApp(),null);
            encryptionListener.handshakeComplete();

        }else if(preText.equals("ENCRYPTANYEXCHANGE!:")){
            BigInteger theirPublicValue = new BigInteger(content, Character.MAX_RADIX);
            this.aesmodule = new AESmodule(
                    dhKeyExchanger.getSecretValue(),
                    theirPublicValue,
                    dhKeyExchanger.getP());

            encryptionListener.handshakeComplete();
        }else if(preText.equals("ENCRYPTANYEXCHANGE@:")){
            IV iv = new IV(message.getIV());
            String s = this.aesmodule.decrypt(content,iv);
            encryptionListener.messageDecrypted(s,message.getOtherParticpant(),message.getApp());
        }else{
            //not actually an encryptany message?
        }
    }

    @Override
    public void setEncryptionListener(EncryptionListener listener) {
        this.encryptionListener = listener;
    }

    private class DHKeyExchanger
    {
        private BigInteger dh_g;
        private BigInteger dh_p;
        private BigInteger dh_mySecret;
        private BigInteger dh_myPublic;

        public DHKeyExchanger()
        { // RFC 5114 2048b MODP Group w/ 256-bit Prime Order Subgroup
            dh_g = new BigInteger(
                "8041367327046189302693984665026706374844608289874374425728797669509435881459140" +
                "6626502158328334713284703340646285086922319994018403320461925692873519916899632" +
                "7965689256248477327858420804098763156962852046406953236127404737444434499665183" +
                "2979378318849943741662110395995778429270819222431610927356005913836932462099770" +
                "0762395540428552871380268069604702773262294828180039620044537644009957909740426" +
                "6367569212075872614586906123644389350913614794241444555184816239146854144435570" +
                "7785697825741856849161233887307017428371823608125699892904960841221593344499088" +
                "996021883972185241854777608212592397013510086894908468466292313");

            dh_p = new BigInteger(
                "1712545831761413793019604197925757782640883232403750857339329298164266713974762" +
                "1778802438775238728592968344613589379932348475613503476932163166973813218698343" +
                "8164632891441853629126025225404949830905314972329658295365245072698488256583114" +
                "2029933592229570974326750832252596677395039491925757684203877163274204414247105" +
                "3509850123605883815857162666917775193496157372656195558305727009891276006514000" +
                "4093658772181713883199238963093777917625906143118496429613802248519404604217104" +
                "4936892725297487039587393638790967227488329537748100815047587859027059179835056" +
                "3488168080923804611822387520198054002990623911454389104774092183");


            SecureRandom secureRandom = new SecureRandom();
            dh_mySecret = new BigInteger(2048,7,secureRandom);
            dh_myPublic = new BigInteger(dh_g.modPow(dh_mySecret,dh_p).toString());
        }
        public BigInteger getPublicValue()  {return dh_myPublic;}
        public BigInteger getSecretValue()  {return dh_mySecret;}
        public BigInteger getP()            {return dh_p;}
    }

    private class IV
    {
        private byte[] iv;
        public IV(String s_iv)
        { // Get iv from a message (message receive)
            this.iv = new byte[16];
            this.iv = Base64.decode(s_iv,0);
        }
        public IV()
        { // Create iv (message send)
            SecureRandom rng = new SecureRandom();
            iv = new byte[16];
            rng.nextBytes(iv);
        }
        public byte[] get()
        {
            return this.iv;
        }
        public String get_s()
        {
            return Base64.encodeToString(this.iv,0);
        }
    }

    private class AESmodule
    {
        private byte[] key;

        public AESmodule(
                BigInteger dh_mySecret,
                BigInteger dh_B,
                BigInteger dh_p)
        {
            /*
            Encapsulates key gen, encryption, decryption, iv handling
            Key is gen'd as SHA256 hash of DH common value
             */
            byte[] dh_ab = dh_B.modPow(dh_mySecret,dh_p).toByteArray();
            this.key = new byte[32];
            SHA256Digest sha256 = new SHA256Digest();
            sha256.update(dh_ab,0,dh_ab.length);
            sha256.doFinal(this.key,0);
        }
        public AESmodule(byte[] key)
        {
            this.key = key;
        }

        public byte[] getKey()
        {
            return key;
        }

        public String encrypt(String data, IV iv) {return Base64.encodeToString(encrypt(data.getBytes(),iv),0);}
        private byte[] encrypt(byte[] data, IV iv)
        {
            Log.d(TAG, "encrypt: Got iv: " + iv.toString());
            Log.d(TAG, "encrypt: Got data: " + data.toString());
            try
            {
                PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));

                cipher.init(true, new ParametersWithIV(new KeyParameter(key), iv.get()));
                byte[] outBuf   = new byte[cipher.getOutputSize(data.length)];

                int processed = cipher.processBytes(data, 0, data.length, outBuf, 0);
                processed += cipher.doFinal(outBuf, processed);

                byte[] outBuf2 = new byte[processed + 16];              // Make room for iv
                System.arraycopy(iv.get(), 0, outBuf2, 0, 16);          // Add iv
                System.arraycopy(outBuf, 0, outBuf2, 16, processed);    // Then the encrypted data

                return outBuf2;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        public String decrypt(String data, IV iv) {return new String(decrypt(Base64.decode(data,0),iv));}
        private byte[] decrypt(byte[] data, IV iv)
        {
            Log.d(TAG, "decrypt: Got iv: " + iv.get().toString());
            Log.d(TAG, "decrypt: Got data: " + data.toString());
            try
            {
                PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
                System.arraycopy(data, 0, iv.get(), 0, iv.get().length); // Get iv from data
                byte[] dataonly = new byte[data.length - iv.get().length];
                System.arraycopy(data, iv.get().length, dataonly, 0, data.length    - iv.get().length);

                cipher.init(false, new ParametersWithIV(new KeyParameter(key), iv.get()));
                byte[] decrypted = new byte[cipher.getOutputSize(dataonly.length)];
                int len = cipher.processBytes(dataonly, 0, dataonly.length, decrypted,0);
                len += cipher.doFinal(decrypted, len);

                return decrypted;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

    }

}
