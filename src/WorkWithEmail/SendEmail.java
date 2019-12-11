package WorkWithEmail;

import com.sun.mail.util.MailSSLSocketFactory;
import shifr.DES;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

/**
 * Класс, который реализует отправку сообщений на почтовый сервер по протоколу SMTP
 *  @author Akim
 *  @version  1.0
 */
public class SendEmail
{
    public  Message message        = null;
    public  static  String   SMTP_AUTH_USER = null;
    public  static  String   SMTP_AUTH_PWD  = null;
    public  static  String   EMAIL_FROM     = null;
    public  static  String   SMTP_SERVER    = null;
    public  static  String   SMTP_Port      = null;
    public  static  String   REPLY_TO       = null;
    public  static  String   FILE_PATH      = null;

    public SendEmail(final String emailTo, final String thema)
    {
        Properties properties = new Properties();
        MailSSLSocketFactory sf = null;
        try {
            sf = new MailSSLSocketFactory();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        sf.setTrustAllHosts(true);

        properties.put("mail.smtp.host"               , SMTP_SERVER                     );
        properties.put("mail.smtp.port"               , SMTP_Port                       );
        properties.put("mail.smtp.auth"               , "true"                          );
        properties.put("mail.smtp.starttls.enable"    , "true"                          );
        properties.put("mail.smtp.socketFactory", sf);

        try {
            Authenticator auth = new EmailAuthenticator(SMTP_AUTH_USER, SMTP_AUTH_PWD);
            Session session = Session.getDefaultInstance(properties, auth);
            session.setDebug(false);

            InternetAddress email_from = new InternetAddress(EMAIL_FROM);
            InternetAddress email_to   = new InternetAddress(emailTo   );
            InternetAddress reply_to   = (REPLY_TO != null) ?
                    new InternetAddress(REPLY_TO) : null;
            message = new MimeMessage(session);

            message.setFrom(email_from);
            message.setRecipient(Message.RecipientType.TO, email_to);
            message.setSubject(thema);

            if (reply_to != null)
                message.setReplyTo (new Address[] {reply_to});
        } catch (AddressException e) {
            System.err.println(e.getMessage());
        } catch (MessagingException e) {
            System.err.println(e.getMessage());
        }
    }

    /**
     *  Метод для реализации возможности, отправки сообщения на почтовый сервер
     * @param text текст сообщения
     * @return возвращает подтверждения того, что письмо было успешно отправленно
     */
    public boolean sendMessage (final String text) {
        boolean result = false;
        try {
            // Содержимое сообщения
            Multipart mmp = new MimeMultipart();
            // Текст сообщения
            MimeBodyPart bodyPart = new MimeBodyPart();
            //DES des = new DES(text);
            bodyPart.setContent(text, "text/html; charset=utf-8");
            mmp.addBodyPart(bodyPart);
            // Вложение файла в сообщение
            if (FILE_PATH != null) {
                MimeBodyPart mbr = createFileAttachment(FILE_PATH);
                mmp.addBodyPart(mbr);
            }
            // Определение контента сообщения
            message.setContent(mmp);
            // Отправка сообщения
            Transport.send(message);
            result = true;
        } catch (MessagingException e){
            // Ошибка отправки сообщения
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Функция создания файлового вложения
     * @param filepath путь к файлу
     * @return MimeBodyPart
     * @throws MessagingException
     */
    private MimeBodyPart createFileAttachment(String filepath) throws MessagingException
    {
        // Создание MimeBodyPart
        MimeBodyPart mbp = new MimeBodyPart();

        // Определение файла в качестве контента
        FileDataSource fds = new FileDataSource(filepath);
        mbp.setDataHandler(new DataHandler(fds));
        mbp.setFileName(fds.getName());
        return mbp;
    }

    /**
     * Метод для отправки зашифрованного текста
     * @param text зашифрованный текст
     * @param desKeyEncrypted зашифрованный ключ
     * @param d ключ от RSA
     * @param n ключ от RSA
     */
    public void sendMessage(String text, List<String> desKeyEncrypted, long d, long n) {
        try {
            // Содержимое сообщения
            Multipart mmp = new MimeMultipart();
            // Текст сообщения
            MimeBodyPart bodyPart = new MimeBodyPart();

            text+="*end*";
            for(String s:desKeyEncrypted){
                text+=s+",";
            }
            bodyPart.setContent(text +"*end*"+
                    +d+"*end*"+n, "text/html; charset=utf-8");
            mmp.addBodyPart(bodyPart);



            // Вложение файла в сообщение
            if (FILE_PATH != null) {
                MimeBodyPart mbr = createFileAttachment(FILE_PATH);
                mmp.addBodyPart(mbr);
            }
            // Определение контента сообщения
            message.setContent(mmp);
            // Отправка сообщения
            Transport.send(message);
        } catch (MessagingException e){
            // Ошибка отправки сообщения
            e.printStackTrace();
        }
    }
}