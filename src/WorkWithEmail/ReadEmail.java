package WorkWithEmail;

import javax.mail.*;
import java.io.IOException;
import java.util.Properties;

/**
 *  Класс для реализации получения сообщений с почтового сервера по протоколу IMAP
 *  @author Akim
 *  @version  1.0
 */
public class ReadEmail
{

    /**
     * почта для подключения
     */
    private String   IMAP_AUTH_EMAIL;

    /**
     * пароль от почты
     */
    private String   IMAP_AUTH_PWD;

    /**
     * сервер IMAP
     */
    private String   IMAP_Server;

    /**
     * порт IMAP
     */
    private String   IMAP_Port;

    /**
     * Конструктор для класса ReadMail с параметрами
     * @see ReadEmail#ReadEmail()
     * @param IMAP_AUTH_EMAIL   почта пользователя
     * @param IMAP_AUTH_PWD     пароль
     * @param IMAP_Server       сервер IMAP
     * @param IMAP_Port         порт IMAP
     */
    public ReadEmail(String IMAP_AUTH_EMAIL, String IMAP_AUTH_PWD, String IMAP_Server, String IMAP_Port){
        this.IMAP_AUTH_EMAIL = IMAP_AUTH_EMAIL;
        this.IMAP_AUTH_PWD = IMAP_AUTH_PWD;
        this.IMAP_Server = IMAP_Server;
        this.IMAP_Port = IMAP_Port;
    }

    /**
     * Конструктор для класса ReadMail без параметров
     * @see ReadEmail#ReadEmail(String, String, String, String)
     */
    public ReadEmail(){
        IMAP_AUTH_EMAIL = "molitvin1999@gmail.com";
        IMAP_AUTH_PWD   = "1728Lack@n1425" ;
        IMAP_Server     = "imap.gmail.com";
        IMAP_Port       = "993";
    }

    /**
     * Метод для реализации возможности чтения писем с сервера
     */
    public void readEmailFromServer()
    {
        Properties properties = new Properties();
        properties.put("mail.debug"           , "false"  );
        properties.put("mail.store.protocol"  , "imaps"  );
        properties.put("mail.imap.ssl.enable" , "true"   );
        properties.put("mail.imap.port"       , IMAP_Port);

        Authenticator auth = new EmailAuthenticator(IMAP_AUTH_EMAIL, IMAP_AUTH_PWD);
        Session session = Session.getDefaultInstance(properties, auth);
        session.setDebug(false);

        try {
            Store store = session.getStore();

            // Подключение к почтовому серверу
            store.connect(IMAP_Server, IMAP_AUTH_EMAIL, IMAP_AUTH_PWD);


            Folder inbox = store.getFolder("INBOX");

            // Открываем папку в режиме только для чтения
            inbox.open(Folder.READ_ONLY);

            //System.out.println("Количество сообщений : " + String.valueOf(inbox.getMessageCount()));
            if (inbox.getMessageCount() == 0)
                return;
            // Последнее сообщение; первое сообщение под номером 1
            Message message = inbox.getMessage(inbox.getMessageCount());
            Multipart mp = (Multipart) message.getContent();
            // Вывод содержимого в консоль
            for (int i = 0; i < mp.getCount(); i++){
                BodyPart  bp = mp.getBodyPart(i);
                if (bp.getFileName() == null)
                    System.out.println("    " + i + ". сообщение : '" + bp.getContent() + "'");
                else
                    System.out.println("    " + i + ". файл : '" + bp.getFileName() + "'");
            }
        } catch (NoSuchProviderException e) {
            System.err.println(e.getMessage());
        } catch (MessagingException e) {
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
