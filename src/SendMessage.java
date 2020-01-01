import WorkWithEmail.SendEmail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import shifr.DES;
import shifr.DiffieHellman;
import shifr.RSA;
import shifr.SHA2;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Класс, который является обработчиком формы SendMessage.fxml
 * @author Akim
 * @version 1.0
 */
public class SendMessage implements Initializable {
    @FXML
    private HTMLEditor htmlEditorTextMessage;
    @FXML
    private TextField txtFieldEmailTo;
    @FXML
    private TextField txtFieldHeader;
    @FXML
    private Label topLabel1;
    @FXML
    private Label topLabel11;
    @FXML
    private Label topLabel12;
    @FXML
    private ChoiceBox choiceEmailFrom;

    @FXML
    private Label topLabel;
    @FXML
    private HBox top;
    @FXML
    private AnchorPane parent;

    private double xOffSet = 0;
    private double yOffSet = 0;
    public static Stage stage = null;

    @FXML
    private void minimize_stage(MouseEvent mouseEvent) {
        this.stage.setIconified(true);
    }
    @FXML
    private void close_app(MouseEvent mouseEvent) {
        Stage tmp = (Stage) parent.getScene().getWindow();
        tmp.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        choiceEmailFrom.setItems(updateDataChoiceBox());
        makeStageDragable();
    }

    private ObservableList<String> updateDataChoiceBox(){
        String query = "SELECT user_email.email FROM user_email " +
                "WHERE account_table_id_account =" + LoginController.idUser+";";

        ObservableList<String> emailList = FXCollections.observableArrayList();
        try {
            LoginController.rs = LoginController.stmt.executeQuery(query);
            while(LoginController.rs.next()){
                emailList.add(LoginController.rs.getString("email"));

            }

            return emailList;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void makeStageDragable() {
        parent.setOnMousePressed((event) -> {
            xOffSet = event.getSceneX();
            yOffSet = event.getSceneY();
        });
        parent.setOnMouseDragged((event) -> {
            this.stage.setX(event.getScreenX() - xOffSet);
            this.stage.setY(event.getScreenY() - yOffSet);
            this.stage.setOpacity(0.8f);
        });
        parent.setOnDragDone((event) -> {
            this.stage.setOpacity(1.0f);
        });
        parent.setOnMouseReleased((event) -> {
            this.stage.setOpacity(1.0f);
        });
    }

    @FXML
    private void handle_logout(MouseEvent mouseEvent) {
        Stage tmp = (Stage) parent.getScene().getWindow();
        tmp.close();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/MainWindow.fxml"));
        try {
            loader.load();
        }catch (IOException e){
            e.printStackTrace();
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        scene.setFill(null);
        stage.setScene(scene);

        stage.initStyle(StageStyle.TRANSPARENT);

        MainWindowController.stage = stage;
        stage.show();
    }

    /**
     * Метод, реализующий отправку сообщение на почтовый сервер
     * Получаем данные об отправителе, получателе и текст сообщения
     * @see SendMessage#htmlEditorTextMessage - текст сообщения
     * @see SendMessage#choiceEmailFrom - отправитель
     * @see SendMessage#txtFieldEmailTo -получатель
     * @param actionEvent событие кнопки
     */
    @FXML
    private void btnSendMessage(ActionEvent actionEvent) {

        String query = "SELECT * FROM user_email WHERE email = '" + choiceEmailFrom.getValue().toString() + "';";
        String password = "";

        try {
            LoginController.rs = LoginController.stmt.executeQuery(query);
            LoginController.rs.next();
            password = (LoginController.rs.getString("password_from_email"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SendEmail.SMTP_SERVER = HelpClass.getSMTPServer(choiceEmailFrom.getValue().toString());

        SendEmail.SMTP_Port = "465";
        SendEmail.EMAIL_FROM = choiceEmailFrom.getValue().toString();
        SendEmail.SMTP_AUTH_USER = choiceEmailFrom.getValue().toString();
        SendEmail.SMTP_AUTH_PWD = password;
        SendEmail.REPLY_TO = choiceEmailFrom.getValue().toString();


        SendEmail se = new SendEmail(txtFieldEmailTo.getText(), txtFieldHeader.getText());
        if (!ShifrManager.shifrText && !ShifrManager.ECP)
            se.sendMessage(htmlEditorTextMessage.getHtmlText());
        else if (ShifrManager.shifrText) {
            DES des = new DES(htmlEditorTextMessage.getHtmlText(), ShifrManager.DESEncryptKey);
            RSA rsa = new RSA();

            String text = des.EncodeDES(); // зашифрованнный текст
            String key = des.getDecodeKeyWord(); // ключ для расшифрования

            // теперь шифруем этот ключ
            rsa.setP(ShifrManager.RSAP);
            rsa.setQ(ShifrManager.RSAQ);
            rsa.setInputData(key);
            List<String> desKeyEncrypted = rsa.RSAEncode();

            se.sendMessage(text, desKeyEncrypted, rsa.getD(), rsa.getN());
        } else if (ShifrManager.ECP) {
            DiffieHellman diffieHellman = new DiffieHellman(ShifrManager.DHP, ShifrManager.DHQ, ShifrManager.privateNumber);
            BigInteger privateKey = diffieHellman.calculatePrivateKey(new BigInteger(ShifrManager.ECPParam[0]));
            byte b;
            byte[] hash = SHA2.hash(htmlEditorTextMessage.getHtmlText().getBytes());
            String hashECP ="";
            for (byte s : hash){
                b= (byte) (s^Byte.parseByte(privateKey.toString()));
                hashECP += b+",";
            }

            se.sendMessage(htmlEditorTextMessage.getHtmlText() + "*end*" + hashECP);
            SendEmail.FILE_PATH = null;
            JOptionPane.showMessageDialog(new JFrame(""), "Сообщение отправлено");
        }
    }

    /**
     * Метод, который реализует получение пути к файлу на машине
     * @param actionEvent событие кнопки
     */
    @FXML
    private void getFile(ActionEvent actionEvent) {
        FileDialog fdlg = new FileDialog(new JFrame(""), "Open file", FileDialog.LOAD);
        fdlg.show();
        SendEmail.FILE_PATH      = fdlg.getDirectory() + fdlg.getFile();
    }

    /**
     * Отправляем публичный ключ, простой модуль, генератор от Диффи-Хеллмана
     * @param actionEvent событие кнопки
     */
    @FXML
    private void sendPublicKeyDH(ActionEvent actionEvent) {
        String query = "SELECT * FROM user_email WHERE email = '" + choiceEmailFrom.getValue().toString() +"';";
        String password ="";

        try {
            LoginController.rs = LoginController.stmt.executeQuery(query);
            LoginController.rs.next();
            password= (LoginController.rs.getString("password_from_email"));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        SendEmail.SMTP_SERVER    = HelpClass.getSMTPServer(choiceEmailFrom.getValue().toString());

        SendEmail.SMTP_Port      = "465";
        SendEmail.EMAIL_FROM     = choiceEmailFrom.getValue().toString();
        SendEmail.SMTP_AUTH_USER = choiceEmailFrom.getValue().toString();
        SendEmail.SMTP_AUTH_PWD  = password;
        SendEmail.REPLY_TO       = choiceEmailFrom.getValue().toString();

        DiffieHellman diffieHellman = new DiffieHellman(ShifrManager.DHP, ShifrManager.DHQ,ShifrManager.privateNumber);
        SendEmail se = new SendEmail(txtFieldEmailTo.getText(), "DH_KEY");
        se.sendDHPublicKey(diffieHellman.calculatePublicKey(),ShifrManager.DHP,ShifrManager.DHQ);
    }
}
