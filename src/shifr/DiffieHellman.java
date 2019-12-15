package shifr;

import java.math.BigInteger;

/**
 * Класс для реализации алгоритма Diffie-Hellman
 * @author Akim
 * @version 1.0
 */
public class DiffieHellman {
    /**
     * простой модуль
     */
    private BigInteger p;
    /**
     * генератор
     */
    private BigInteger q;
    /**
     * приватное число пользователя
     */
    private int privateNumber;

    /**
     * Вычисление открытого ключа, который после будет отправлен по почте
     * @return возвращает публичный ключ
     */
    public BigInteger calculatePublicKey(){
        BigInteger publicKey;
        publicKey= q.pow(privateNumber);
        publicKey = publicKey.mod(p);
        return publicKey;
    }

    public BigInteger calculatePrivateKey(BigInteger publicKey){
        BigInteger privateKey;
        privateKey = publicKey.pow(privateNumber);
        privateKey = privateKey.mod(p);
        return privateKey;
    }
}
