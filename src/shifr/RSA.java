package shifr;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Класс, реализующий шифрование данных ассиметричным методом RSA
 * @author Akim
 * @version 1.0
 */
public class RSA {
    /**
     * алфавит для шифрования
     * Число M(i) для конкретной буквы будет равно её номеру в массиве characters[]
     */
    private final char[] characters = new char[]{ '#', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И',
            'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С',
            'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ь', 'Ы', 'Ъ',
            'Э', 'Ю', 'Я', ' ', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', '0' };

    /**
     * простое число p, необходимое для последующего шифрования
     */
    long p;
    /**
     * простое число q, необходимое для последующего шифрования
     */
    long q;
    /**
     * секретный ключ параметр d
     */
    long d;
    /**
     * секретный ключ параметр n
     */
    long n;
    /**
     * исходные данные для шифрования
     */
    String inputData;

    List<String> inputDataForDecode = new ArrayList<>();

    public RSA(){
        inputData ="";
        p=0;
        q=0;
        d=0;
        n=0;
    }

    /**
     * Метод, который проверяет является ли число простым
     * @param n число для проверки
     * @return boolean возвращает результат проверки
     */
    private boolean isTheNumberSimple(long n) {
        if (n < 2)
            return false;

        if (n == 2)
            return true;

        for (long i = 2; i < n; i++)
            if (n % i == 0)
                return false;

        return true;
    }

    /**
     * Метод, реализующий шифрования данных по алгоритму RSA
     * @param s входные данные
     * @param e ключ е
     * @param n ключ n
     * @return
     */
    private List<String> RSAEncode(String s, long e, long n) {
        List<String> result = new ArrayList<>();
        BigInteger bi;

        for (int i = 0; i < s.length(); i++)
        {
            int index = findIndex(characters, s.charAt(i));

            bi = new BigInteger(Integer.toString(index));

            bi=bi.pow((int)e);


            BigInteger N = new BigInteger(Long.toString(n));

            bi = bi.mod(N);

            result.add(bi.toString());
        }
        return result;
    }

    /**
     * Вспомогательная функция для поиска индекса элемента массива
     * @param arr массив
     * @param t элемент массива
     * @return int возвращает индекс элемента массива
     */
    private int findIndex(char arr[], char t) {

        // если массив пуст возвращаем -1
        if (arr == null)
            return -1;

        // выполняем поиск элемента и возвращаем его индекс, если находим его в массиве
        for ( int i = 0; i < arr.length; i++)
            if (arr[i] == t)
                return i;

        //если не нашли, то возвращаем -1
        return -1;
    }

    /**
     * Метод, реализующий расшифрования данных по аллгоритму RSA
     * @param input зашифрованные данные
     * @param d ключ d
     * @param n ключ n
     * @return расшифрованный текст
     */
    private String RSADecode(List<String> input, long d, long n) {
        String result = "";

        BigInteger bi;

        for (String item : input)
        {
            bi = new BigInteger(item);
            bi = bi.pow((int)d);

            BigInteger N = new BigInteger(Long.toString(n));

            bi = bi.mod(N);

            int index = Integer.parseInt(bi.toString());

            result += characters[index];
        }

        return result;
    }

    /**
     * Вычисление параметра d (d должно быть взаимно простым с m)
     * @param m параметр m
     * @return long возвращает d
     */
    private long calculateD(long m) {
        long d = m - 1;

        for (long i = 2; i <= m; i++)
            if ((m % i == 0) && (d % i == 0)) //если имеют общие делители
            {
                d--;
                i = 1;
            }

        return d;
    }

    /**
     * Метод, вычисляющий значение параметра e
     * @param d ключ d
     * @param m параметр m
     * @return long e
     */
    private long calculateE(long d, long m) {
        long e = 10;

        while (true)
        {
            if ((e * d) % m == 1)
                break;
            else
                e++;
        }

        return e;
    }

    /**
     * Шифрование данных
     */
    public List<String> RSAEncode() {

        if (isTheNumberSimple(p) && isTheNumberSimple(q)) {
            String s = inputData;
            s = s.toUpperCase();

            long n = p * q;
            long m = (p - 1) * (q - 1);
            long d = calculateD(m);
            long e_ = calculateE(d, m);

            this.d = d;
            this.n = n;
            return RSAEncode(s, e_, n);
        }
        return null;
    }

    /**
     * Расшифрование данных
     */
    public String RSADecode(){
        return RSADecode(inputDataForDecode, d, n);
    }

    public long getD() {
        return d;
    }
    public long getN() {
        return n;
    }
    public long getP() {
        return p;
    }
    public long getQ() {
        return q;
    }
    public String getInputData() {
        return inputData;
    }
    public List<String> getInputDataForDecode() {
        return inputDataForDecode;
    }

    public void setD(long d) {
        this.d = d;
    }
    public void setN(long n) {
        this.n = n;
    }
    public void setP(long p) {
        this.p = p;
    }
    public void setQ(long q) {
        this.q = q;
    }
    public void setInputData(String inputData) {
        this.inputData = inputData;
    }
    public void setInputDataForDecode(List<String> inputDataForDecode) {
        this.inputDataForDecode = inputDataForDecode;
    }
}
