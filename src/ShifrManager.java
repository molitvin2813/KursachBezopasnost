import WorkWithEmail.ReadEmail;
import helpers.MyException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.swing.*;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

/**
 * Класс контроллер для shifrManager.fxml
 * @author Akim
 * @version 1.0
 */
public class ShifrManager implements Initializable {

    public Pane ECPPane2;
    public ListView listViewDHMail;
    public ChoiceBox mailChoice;
    @FXML
    private TextField textDHPrivateKey;
    @FXML
    private TextField textDHP;
    @FXML
    private TextField textDHQ;
    @FXML
    private TextField desKey;
    @FXML
    private TextField textRSAQ;
    @FXML
    private TextField textRSAP;
    @FXML
    private Pane ECPPane;
    @FXML
    private Pane shifrTextPane;
    @FXML
    private AnchorPane parent;


    /**
     * Переменная, которая показывает, что включен/отключет алгоритм шифрования текста DES
     */
    public static boolean shifrText = false;
    /**
     * Переменная, которая показывает, что включена/отключена ЭЦП
     */
    public static boolean ECP = false;
    /**
     * Ключ для DES
     */
    public static String DESEncryptKey="";
    /**
     * Простое чилсо q для RSA
     */
    public static int RSAQ=0;
    /**
     * Простое число p для RSA
     */
    public static int RSAP=0;
    /**
     * простой модуль для Диффи-Хеллмана
     */
    public static BigInteger DHP=new BigInteger("0");
    /**
     * генератор для Диффи-Хеллмана
     */
    public static BigInteger DHQ=new BigInteger("0");
    /**
     * Приватное число для Диффи-Хеллмана
     */
    public static int privateNumber=0;


    private String [] ECPParam;
    private ReadEmail readEmail;
    private double xOffSet = 0;
    private double yOffSet = 0;
    public static Stage stage = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        shifrTextPane.setVisible(shifrText);
        desKey.setText(DESEncryptKey);
        textRSAP.setText(RSAP+"");
        textRSAQ.setText(RSAQ+"");

        ECPPane.setVisible(ECP);
        textDHQ.setText(DHQ.toString());
        textDHP.setText(DHP.toString());
        textDHPrivateKey.setText(privateNumber+"");

        readEmail = new ReadEmail();
        mailChoice.setItems(HelpClass.updateDataChoiceBox());
        makeStageDragable();
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
    @FXML
    private void minimize_stage(MouseEvent mouseEvent) {
        this.stage.setIconified(true);
    }
    @FXML
    private void close_app(MouseEvent mouseEvent) {
        Stage tmp = (Stage) parent.getScene().getWindow();
        tmp.close();
    }

    /**
     * Включаем шифрование текста и показываем форму с доп параметрами
     * @param actionEvent событие мыши
     */
    @FXML
    private void onShifrText(ActionEvent actionEvent) {
        shifrText= !shifrText;
        shifrTextPane.setVisible(shifrText);
    }

    /**
     * Включаем ЭЦП и показываем форму с доп параметрами
     * @param actionEvent событие мыши
     */
    @FXML
    private void onECP(ActionEvent actionEvent) {
        ECP= !ECP;
        ECPPane.setVisible(ECP);
    }


    /**
     * Метод, который задает параметры для RSA и DES
     * @param actionEvent событие кнопки
     */
    @FXML
    private void addParamShifrText(ActionEvent actionEvent) {
        try {
            if (desKey.getText().length() > 4)
                throw new MyException.DesKeyLengthException("Слишком длинный ключ! ", desKey.getText());
            else
                DESEncryptKey = desKey.getText();

            if(HelpClass.isPrime(Integer.parseInt(textRSAP.getText())))
                RSAP = Integer.parseInt(textRSAP.getText());
            else
                throw new MyException.PrimeNumberException("Не простое число!\n",
                        Integer.parseInt(textRSAP.getText()));

            if(HelpClass.isPrime(Integer.parseInt(textRSAQ.getText())))
                RSAQ = Integer.parseInt(textRSAQ.getText());
            else
                throw new MyException.PrimeNumberException("Не простое число!\n",
                        Integer.parseInt(textRSAQ.getText()));

            JOptionPane.showMessageDialog(new JFrame(""),
                    "Параметры для RSA и DES обновлены");
        } catch (MyException.DesKeyLengthException e) {
            JOptionPane.showMessageDialog(new JFrame(""),
                    e.getMessage() + desKey.getText()+ " Длина ключа не должна превышать " + e.getLengthKey());
        } catch (MyException.PrimeNumberException e) {
            JOptionPane.showMessageDialog(new JFrame(""),
                    e.getMessage() + "Число "+e.getNumber()+" не является простым");
        }
    }
    
    /**
     * Метод, который задает параметры для ECP
     * @param actionEvent событие кнопки
     */
    @FXML
    private void addParamECP(ActionEvent actionEvent) {
        DHP = new BigInteger(textDHP.getText());
        DHQ = new BigInteger(textDHQ.getText());
        privateNumber = Integer.parseInt(textDHPrivateKey.getText());
        JOptionPane.showMessageDialog(new JFrame(""), "Параметры для ECP обновлены");
    }


    public void getECPParam(MouseEvent actionEvent) {

        if(readEmail.getCurrentFolder()!=null) {
            int id = listViewDHMail.getSelectionModel().getSelectedIndex();

            // ECPParam[0] публичный ключ
            // ECPParam[1] простой модуль
            // ECPParam[2] генератор
            ECPParam =  readEmail.getBodyMessage(id).split("\\*end\\*");

            textDHP.setText(ECPParam[1]);
            DHP = new BigInteger(ECPParam[1]);
            textDHQ.setText(ECPParam[2]);
            DHQ = new BigInteger(ECPParam[2]);
        }

    }

    public void setEmail(ActionEvent actionEvent) {
        String query = "SELECT * FROM user_email WHERE email = '" + mailChoice.getValue().toString() +"';";
        String password ="";

        try {
            LoginController.rs = LoginController.stmt.executeQuery(query);
            LoginController.rs.next();
            password= (LoginController.rs.getString("password_from_email"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        readEmail.setIMAP_AUTH_EMAIL(mailChoice.getValue().toString());
        readEmail.setIMAP_AUTH_PWD(password);
        readEmail.setIMAP_Port("993");
        readEmail.setIMAP_Server(HelpClass.getIMAPServer(readEmail.getIMAP_AUTH_EMAIL()));
        readEmail.setCurrentFolder("INBOX");
        listViewDHMail.setItems(readEmail.readEmailFromServer());
    }


}
