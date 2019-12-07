package WorkWithEmail;

import com.sun.mail.util.MailSSLSocketFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.mail.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
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
        IMAP_AUTH_EMAIL = "lackan1@yandex.ua";
        IMAP_AUTH_PWD   = "2813Andrei" ;
        IMAP_Server     = "imap.yandex.ua";
        IMAP_Port       = "993";
    }

    public String getIMAP_AUTH_EMAIL(){
        return IMAP_AUTH_EMAIL;
    }
    /**
     * Метод для реализации возможности чтения писем с сервера
     */
    public ObservableList<String> readEmailFromServer() {
        ObservableList<String> messageList = FXCollections.observableArrayList();
        Properties properties = new Properties();
        properties.put("mail.debug"           , "false"  );
        properties.put("mail.store.protocol"  , "imap"  );

        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        sf.setTrustAllHosts(true);
        properties.put("mail.imap.starttls.enable", "true");
        properties.put("mail.imap.ssl.socketFactory", sf);
        properties.put("mail.imaps.port"       , IMAP_Port);

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
                return null;
            // Последнее сообщение; первое сообщение под номером 1
            Message[] message = inbox.getMessages();
            for(int i =0; i<inbox.getMessageCount();i++){

                messageList.add(message[i].getSubject());
            }

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return messageList;
    }

    public ObservableList<String> getBodyMessage(){
        ObservableList<String> messageList = FXCollections.observableArrayList();
        Properties properties = new Properties();
        properties.put("mail.debug"           , "false"  );
        properties.put("mail.store.protocol"  , "imap"  );

        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        sf.setTrustAllHosts(true);
        properties.put("mail.imap.starttls.enable", "true");
        properties.put("mail.imap.ssl.socketFactory", sf);
        properties.put("mail.imaps.port"       , IMAP_Port);

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
                return null;
            // Последнее сообщение; первое сообщение под номером 1
            Message[] message = inbox.getMessages();

            Multipart mp = (Multipart) message[0].getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                BodyPart bp = mp.getBodyPart(i);
                if (bp.getFileName() == null)
                    //System.out.println("    " + i + ". сообщение : '" + bp.getContent() + "'");
                    messageList.add(bp.getContent() + "");
                else
                    //System.out.println("    " + i + ". файл : '" + bp.getFileName() + "'");
                    messageList.add(bp.getFileName() + "");
            }
        }catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messageList;

    }
}
