package shifr;

import com.sun.mail.iap.ByteArray;
import org.omg.IOP.Encoding;

/**
 *  Класс для реализации шифрования данных методом
 *  симметрического шиврования DES
 *  @author Akim
 *  @version  1.0
 */
public class DES {
    /**
     * Исходный текст, который поступает для последующего шифрования
     */
    private String inputData;
    /**
     * ключ для шифрования
     */
    private String key;
    /**
     * в DES размер блока 64 бит, но поскольку в unicode символ в два раза длинее, то увеличим блок тоже в два раза
     */
    private final int sizeOfBlock = 128;
    /**
     * размер одного символа (in Unicode 16 bit)
     */
    private final int sizeOfChar = 16;
    /**
     * сдвиг ключа
     */
    private final int shiftKey = 2;
    /**
     * количество раундов
     */
    private final int quantityOfRounds = 16;
    /**
     * сами блоки в двоичном формате
     */
    private String[] Blocks;

    /**
     * конструктор класса DES без параметров
     * @see DES#DES(String, String)
     */
    public DES(){
        inputData = "";
        key = "";
    }

    /**
     * конструктор класса DES с параметрами
     * @see DES#DES()
     * @param inputData исходные данные для шифврования
     * @param key ключ для шифрования
     */
    public DES(String inputData,String key){
        this.inputData = StringToRightLength(inputData);
        this.key = key;

    }

    /**
     * Метод, который делит исходный текстк на блоки заданного размера(128 бит)
     * @param input строка для преобразования
     */
    private void CutStringIntoBlocks(String input) {
        Blocks = new String[(input.length() * sizeOfChar) / sizeOfBlock];

        int lengthOfBlock = input.length() / Blocks.length;

        for (int i = 0; i < Blocks.length; i++)
        {
            Blocks[i] = input.substring(i * lengthOfBlock, lengthOfBlock);
            Blocks[i] = StringToBinaryFormat(Blocks[i]);
        }
    }

    /**
     * Метод, доводящий строку до такого размера, чтобы она делилась на sizeOfBlock
     * @see DES#sizeOfBlock
     * @param input входная строка
     * @return String возвращает строку необходимого размера
     */
    private String StringToRightLength(String input) {
        StringBuilder inputBuilder = new StringBuilder(input);
        while (((inputBuilder.length() * sizeOfChar) % sizeOfBlock) != 0)
            inputBuilder.append("#");
        input = inputBuilder.toString();

        return input;
    }

    /**
     * Метод, доводящий строку до такого размера, чтобы она делилась на sizeOfBlock
     * @see DES#sizeOfBlock
     * @param input входная строка в двоичном формате
     */
    private void CutBinaryStringIntoBlocks(String input) {
        Blocks = new String[input.length() / sizeOfBlock];

        int lengthOfBlock = input.length() / Blocks.length;

        for (int i = 0; i < Blocks.length; i++)
            Blocks[i] = input.substring(i * lengthOfBlock, lengthOfBlock);
    }

    /**
     * Метод, который переводит строку в двоичный формат
     * @param input строка в обычном формате
     * @return String возвращает строку уже в двоичном формате
     */
    private String StringToBinaryFormat(String input) {
        StringBuilder output = new StringBuilder();

        for (int i = 0; i < input.length(); i++)
        {
            String char_binary = Integer.toBinaryString(input.charAt(i));

            while (char_binary.length() < sizeOfChar)
                char_binary = "0" + char_binary;

            output.append(char_binary);
        }

        return output.toString();
    }

    /**
     * Метод, который дополняет символами ключ, если его длина не соответствует
     * @param input ключ для шифрования
     * @param lengthKey длина ключа
     * @return String возвращает ключ необходимой длины
     */
    private String CorrectKeyWord(String input, int lengthKey) {
        if (input.length() > lengthKey)
            input = input.substring(0, lengthKey);
        else
            while (input.length() < lengthKey)
                input = "0" + input;

        return input;
    }

    /**
     * Один раунд сети Фейстеля(шифрование)
     * @param input исходные данные
     * @param key ключ для шифрования
     * @return String результат работы алгоритма
     */
    private String EncodeDES_One_Round(String input, String key) {
        String L = input.substring(0, input.length() / 2);
        String R = input.substring(input.length() / 2, input.length() / 2);

        return (R + XOR(L, f(R, key)));
    }

    /**
     * Один раунд сети Фейстеля(расшифровка)
     * @param input зашифрованные текст
     * @param key ключ для расшифрования
     * @return String результат работы алгоритма
     */
    private String DecodeDES_One_Round(String input, String key) {
        String L = input.substring(0, input.length() / 2);
        String R = input.substring(input.length() / 2, input.length() / 2);

        return (XOR(f(L, key), R) + L);
    }

    /**
     * XOR двух строк с двоичными данными
     * @param s1 первая строка в двоичном формате
     * @param s2 вторая строка в двоичном формате
     * @return String результат xor двух строк(s1,s2)
     */
    private String XOR(String s1, String s2) {
        String result = "";

        for (int i = 0; i < s1.length(); i++)
        {
            boolean a;
            if ((Integer.parseInt(s1.charAt(i) + ""))==1) a = true;
            else a = false;

            boolean b;
            if ((Integer.parseInt(s2.charAt(i) + ""))==1) b = true;
            else b = false;

            if (a ^ b)
                result += "1";
            else
                result += "0";
        }
        return result;
    }

    /**
     * Функция сети Фейстеля
     * Здесь так же применяется xor
     * @param s1 первая строка в двоичном формате
     * @param s2 вторая строка в двоичном формате
     * @return String результат xor двух строк(s1,s2)
     */
    private String f(String s1, String s2) {
        return XOR(s1, s2);
    }

    /**
     * Метод, генерирующий ключ для следующего раунда шифрования
     * @param key ключ
     * @return String возвращает ключ для следующего раунда шифрования
     */
    private String KeyToNextRound(String key) {
        StringBuilder keyBuilder = new StringBuilder(key);
        for (int i = 0; i < shiftKey; i++)
        {
            keyBuilder.insert(0, keyBuilder.charAt(keyBuilder.length() - 1));
            keyBuilder.deleteCharAt(keyBuilder.length() - 1);
        }
        key = keyBuilder.toString();

        return key;
    }

    /**
     * Метод, генерирующий ключ для следующего раунда расшифровки
     * @param key ключ
     * @return String возвращает ключ для следующего раунда расшифровки
     */
    private String KeyToPrevRound(String key) {
        StringBuilder keyBuilder = new StringBuilder(key);
        for (int i = 0; i < shiftKey; i++)
        {
            keyBuilder.append(keyBuilder.charAt(0));
            keyBuilder.deleteCharAt(0);

        }
        key = keyBuilder.toString();

        return key;
    }
    






    /**
     * геттер для ключа
     * @see DES#key
     * @return key
     */
    public String getKey() {
        return key;
    }
    /**
     * геттер для исходного текста
     * @see DES#inputData
     * @return inputData
     */
    public String getInputData() {
        return inputData;
    }

    /**
     * сеттер для исходного текста
     * @see DES#inputData
     * @param inputData исходный текст для шифрования
     */
    public void setInputData(String inputData) {
        this.inputData = inputData;
    }
    /**
     * сеттер для ключа
     * @see DES#key
     * @param key ключ для шифрования
     */
    public void setKey(String key) {
        this.key = key;
    }
}
