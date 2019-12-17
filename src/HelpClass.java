import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

/**
 * Класс со вспомогательнвми методами
 * @author Akim
 * @version 1.0
 */
public class HelpClass {

    /**
     * Вспомогательный метод, который выполняет запрос на получение email адресов,
     * а после формирует из них список, который необходим для ChoiceBox
     * @return ObservableList<String> список адресов
     */
    public static  ObservableList<String> updateDataChoiceBox(){
        String query = "SELECT user_email.email FROM user_email " +
                "WHERE account_table_id_account =" + LoginController.idUser+";";

        ObservableList<String> emailList = FXCollections.observableArrayList();
        try {
            LoginController.rs = LoginController.stmt.executeQuery(query);
            while(LoginController.rs.next())
                emailList.add(LoginController.rs.getString("email"));

            return emailList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Вспомогательная метод для получения сервера IMAP
     * @param mail почта пользователя, которую он выбрал
     * @return адрес сервера IMAP
     */
    public static String getIMAPServer(String mail){
        String[] array = mail.split("@");
        return "imap."+array[1];
    }

    /**
     * Вспомогательный метод для получения необходимого сервера SMTP
     * @param mail почта пользователя, которую он выбрад
     * @return String возвращает SMTP сервер для той почты, которую выбрал пользователь
     */
    public static String getSMTPServer(String mail){
        String[] arr = mail.split("@");
        return "smtp."+arr[1];
    }

    /**
     * Вспомогательный метод, который проверяет явлеется ли число простым
     * @param number число для проверки
     * @return boolean возвращает результат проверки
     */
    public static boolean isPrime(int number){
        for (int i=2; i<=(sqrt(abs(number))); i++)
            if (number%i==0)
                return false;

        return true;
    }
}
