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
     * Переменная, которая хранит текст, который поступает для последующего шифрования
     */
    private String inputData;
    /**
     * Переменная для хранения байт исходного текста
     */
    private byte[] byteData;
    /**
     * ключ для шифрования
     */
    private String key;
    /**
     * Ключ в байтах
     */
    private byte[] byteKey;

    /**
     * конструктор класса DES без параметров
     * @see DES#DES(String, String)
     */
    public DES(){
        inputData = "";
        byteData= new byte[inputData.length()];
        key = "";
        byteKey = new byte[key.length()];
    }

    /**
     * конструктор класса DES с параметрами
     * @see DES#DES()
     * @param inputData исходные данные для шифврования
     * @param key ключ для шифрования
     */
    public DES(String inputData,String key){
        this.inputData = inputData;
        this.key = key;
        this.byteData = convertToByte(inputData.toCharArray());
        this.byteKey = convertToByte(key.toCharArray());
    }

    /**
     * Вспомогательная функция, которая преобразует строку в массив байтов
     * @param text массив символов
     * @return byte temp
     */
    private byte[] convertToByte(char[] text){
        byte[] temp = new byte[text.length];
        for(int i = 0; i < temp.length; i++){
            temp[i] = (byte)text[i];
        }
        return temp;
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
