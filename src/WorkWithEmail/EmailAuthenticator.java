package WorkWithEmail;

import javax.mail.PasswordAuthentication;

/**
 * Класс, реализующий подключение к почтовому серверу
 * Поля:
 * <b>login</b>
 * <b>password</b>
 * @author Akim
 * @version  1.0
 */
public class EmailAuthenticator extends javax.mail.Authenticator
{
    private String login   ;
    private String password;
    public EmailAuthenticator(final String login, final String password)
    {
        this.login    = login;
        this.password = password;
    }
    public PasswordAuthentication getPasswordAuthentication()
    {
        return new PasswordAuthentication(login, password);
    }
}
