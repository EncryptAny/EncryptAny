package xyz.encryptany.encryptany.concrete;

import android.util.Base64;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.sql.Time;
import java.util.Date;

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
    private BigInteger dh_g;
    private BigInteger dh_p;
    private BigInteger dh_a;
    private BigInteger dh_A;
    private EncryptionListener encryptionListener;

    public Crypto() {
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(),1);
    }

    @Override
    public void initialization(Message message) {
        // This gets called to begin handshake
        dh_g = new BigInteger(get_g());
        dh_p = new BigInteger(get_p());
        SecureRandom secureRandom = new SecureRandom();
        dh_a = new BigInteger(2048,7,secureRandom);
        dh_A = new BigInteger(dh_g.modPow(dh_a,dh_p).toString());

    }

    @Override
    public void encryptMessage(Message message) {

    }

    @Override
    public void decryptMessage(Message message) {

    }

    @Override
    public void setEncryptionListener(EncryptionListener listener) {
        this.encryptionListener = listener;
    }
    private String get_g()
    { // RFC 5114 2048b MODP Group w/ 256-bit Prime Order Subgroup
        return  "8041367327046189302693984665026706374844608289874374425728797669509435881459140" +
                "6626502158328334713284703340646285086922319994018403320461925692873519916899632" +
                "7965689256248477327858420804098763156962852046406953236127404737444434499665183" +
                "2979378318849943741662110395995778429270819222431610927356005913836932462099770" +
                "0762395540428552871380268069604702773262294828180039620044537644009957909740426" +
                "6367569212075872614586906123644389350913614794241444555184816239146854144435570" +
                "7785697825741856849161233887307017428371823608125699892904960841221593344499088" +
                "996021883972185241854777608212592397013510086894908468466292313";
    }
    private String get_p()
    {
        return "17125458317614137930196041979257577826408832324037508573393292981642667139747621" +
                "7788024387752387285929683446135893799323484756135034769321631669738132186983438" +
                "1646328914418536291260252254049498309053149723296582953652450726984882565831142" +
                "0299335922295709743267508322525966773950394919257576842038771632742044142471053" +
                "5098501236058838158571626669177751934961573726561955583057270098912760065140004" +
                "0936587721817138831992389630937779176259061431184964296138022485194046042171044" +
                "9368927252974870395873936387909672274883295377481008150475878590270591798350563" +
                "488168080923804611822387520198054002990623911454389104774092183";
    }
-
    private static class AESkey
    {
        private byte[] key;
        public AESkey(AESkey ak){this.key = ak.getKey();}
        public AESkey(
                BigInteger dh_a,
                BigInteger dh_B,
                BigInteger dh_p)
        {
            byte[] dh_ab = dh_B.modPow(dh_a,dh_p).toByteArray();
            this.key = new byte[32];
            SHA256Digest sha256 = new SHA256Digest();
            sha256.update(dh_ab,0,dh_ab.length);
            sha256.doFinal(this.key,0);
        }
        public String encryptText(String txt)
        {

        }
        public byte[] getKey(){return this.key;}
        /**
         * Encrypt the given plaintext bytes using the given key
         * @param data The plaintext to encrypt
         * @param key The key to use for encryption
         * @return The encrypted bytes
         */
        private static byte[] encrypt(byte[] data, byte[] key)
        {
            // 16 bytes is the IV size for AES256
            try
            {
                PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
                // Random iv
                SecureRandom rng = new SecureRandom();
                byte[] ivBytes = new byte[16];
                rng.nextBytes(ivBytes);

                cipher.init(true, new ParametersWithIV(new KeyParameter(key), ivBytes));
                byte[] outBuf   = new byte[cipher.getOutputSize(data.length)];

                int processed = cipher.processBytes(data, 0, data.length, outBuf, 0);
                processed += cipher.doFinal(outBuf, processed);

                byte[] outBuf2 = new byte[processed + 16];        // Make room for iv
                System.arraycopy(ivBytes, 0, outBuf2, 0, 16);    // Add iv
                System.arraycopy(outBuf, 0, outBuf2, 16, processed);    // Then the encrypted data

                return outBuf2;
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Decrypt the given data with the given key
         * @param data The data to decrypt
         * @param key The key to decrypt with
         * @return The decrypted bytes
         */
        private static byte[] decrypt(byte[] data, byte[] key)
        {
            // 16 bytes is the IV size for AES256
            try
            {
                PaddedBufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESFastEngine()));
                byte[] ivBytes = new byte[16];
                System.arraycopy(data, 0, ivBytes, 0, ivBytes.length); // Get iv from data
                byte[] dataonly = new byte[data.length - ivBytes.length];
                System.arraycopy(data, ivBytes.length, dataonly, 0, data.length    - ivBytes.length);

                cipher.init(false, new ParametersWithIV(new KeyParameter(key), ivBytes));
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
