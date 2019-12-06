package shifr;


import java.math.BigInteger;
import java.util.List;

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


    public RSA(){}

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
        List<String> result = null;
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
    
}
