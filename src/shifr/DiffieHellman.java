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
     * Конструкор
     */
    public DiffieHellman(){
        p=null;
        q=null;
        privateNumber = 0;
    }

    /**
     * Конструкктор для класс DiffieHellman с параметрами
     * @see DiffieHellman#DiffieHellman()
     * @param p простой модуль
     * @param q генератор
     */
    public DiffieHellman(BigInteger p, BigInteger q, int privateNumber){
        this.p = p;
        this.q = q;
        this.privateNumber = privateNumber;
    }
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

    public BigInteger getP() {
        return p;
    }

    public BigInteger getQ() {
        return q;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public void setQ(BigInteger q) {
        this.q = q;
    }

    public void setPrivateNumber(int privateNumber) {
        this.privateNumber = privateNumber;
    }
}
