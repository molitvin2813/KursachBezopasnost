import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import WorkWithEmail.ReadEmail;
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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import shifr.DES;
import shifr.RSA;
import shifr.SHA2;

import javax.swing.*;


/**
 * Класс, который является обработчиком формы MainWindow.fxml
 * @author Akim
 * @version  1.0
 */
public class MainWindowController implements Initializable {
    @FXML
    private WebView messageView;
    @FXML
    private AnchorPane paneForDeleteMail;
    @FXML
    private AnchorPane paneForAddNewMail;
    @FXML
    private ChoiceBox<String> emailChoiceForDelete;
    @FXML
    private TextField passwordAddEMail;
    @FXML
    private TextField loginAddEmail;
    @FXML
    private ListView<String> emailListView;
    @FXML
    private ChoiceBox<String> emailChoice;
    @FXML
    private Label labelFolders;
    @FXML
    private AnchorPane parent;
    @FXML
    private HBox top;
    @FXML
    private ChoiceBox<String> folderChoice;

    private ReadEmail readEmail;

    public static Stage stage = null;

    private double xOffSet = 0;
    private double yOffSet = 0;

    private boolean switcher =true;
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        emailChoice.setItems(HelpClass.updateDataChoiceBox());
        emailChoiceForDelete.setItems(HelpClass.updateDataChoiceBox());
        readEmail = new ReadEmail();

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
    private void handle_logout(MouseEvent event) throws IOException {

        Stage tmp = (Stage) parent.getScene().getWindow();
        tmp.close();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/Login.fxml"));
        try {
            loader.load();
        }catch (IOException e){
            e.printStackTrace();
        }

        Parent root = loader.getRoot();
        Stage stage = new Stage();

        stage.setScene(new Scene(root));
        stage.initStyle(StageStyle.UNDECORATED);
        LoginController.stage = stage;
        stage.show();

    }

    /**
     * Метод для кнопки "свернуть"
     * @param event событие мыши
     */
    @FXML
    private void minimize_stage(MouseEvent event) {
        stage.setIconified(true);
    }

    /**
     * Метод для кнопки "закрыть"
     * @param event событие мыши
     */
    @FXML
    private void close_app(MouseEvent event) {
        Stage tmp = (Stage) parent.getScene().getWindow();
        tmp.close();
    }

    /**
     * Код для кнопки выбрать EMail
     * @param event событие
     */
    @FXML
    private void choiceEMail(ActionEvent event){
        String query = "SELECT * FROM user_email WHERE email = '" + emailChoice.getValue().toString() +"';";
        String password ="";

        try {
            LoginController.rs = LoginController.stmt.executeQuery(query);
            LoginController.rs.next();
            password= (LoginController.rs.getString("password_from_email"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        readEmail.setIMAP_AUTH_EMAIL(emailChoice.getValue().toString());
        readEmail.setIMAP_AUTH_PWD(password);
        readEmail.setIMAP_Port("993");
        readEmail.setIMAP_Server(HelpClass.getIMAPServer(readEmail.getIMAP_AUTH_EMAIL()));

        folderChoice.setItems( readEmail.getFolderList());
        emailListView.setItems(null);
    }

    /**
     * Отображает панель для добавления новой почты
     * @param event событие
     */
    @FXML
    private void showAddEMailPanel(ActionEvent event){
        paneForAddNewMail.setVisible(switcher);
        paneForDeleteMail.setVisible(switcher);
        switcher= !switcher;
    }
    JFrame frame = new JFrame("JOptionPane showMessageDialog example");
    /**
     * Код для кнопки добавить новый EMail
     * @param event событие
     */
    @FXML
    private void addNewEMail(ActionEvent event){
        String query = "INSERT INTO user_email (email, password_from_email,account_table_id_account)" +
                "VALUES ('"+loginAddEmail.getText()+"', '"+passwordAddEMail.getText()+"',"+LoginController.idUser+")";

        try {
            LoginController.stmt = LoginController.con.createStatement();
            LoginController.stmt.executeUpdate(query);
            passwordAddEMail.setText("");
            loginAddEmail.setText("");
            JOptionPane.showMessageDialog(frame, "Аккаунт успешно добавлен");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Не удалось добавить аккаунт");
        }
    }

    /**
     * Код для кнопки, котрая реализует возможность удаления почты
     * с аккаунта пользователя почтового клиента
     * @param actionEvent событие
     */
    @FXML
    private void choiceEMailForDelete(ActionEvent actionEvent) {
        String query ="DELETE FROM user_email WHERE email = '"+ emailChoiceForDelete.getValue() + "';";

        try {
            LoginController.stmt = LoginController.con.createStatement();
            LoginController.stmt.executeUpdate(query);
            JOptionPane.showMessageDialog(frame, "Аккаунт успешно удален");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Не удалось удалить аккаунт ");

        }
        emailChoice.setItems(HelpClass.updateDataChoiceBox());
        emailChoiceForDelete.setItems(HelpClass.updateDataChoiceBox());
    }

    /**
     * Код для кнопки, который реализует переход на форму отправки сообщения SendMessage.fxml
     * @param actionEvent событие
     */
    @FXML
    private void sendMessage(ActionEvent actionEvent) {
        Stage tmp = (Stage) parent.getScene().getWindow();
        tmp.close();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/SendMessage.fxml"));
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

        SendMessage.stage = stage;
        stage.show();
    }

    /**
     * Код для кнопки, который реализует выбор текущей папка на почтовом сервере
     * @param actionEvent событие кнопки
     */
    @FXML
    private void choiceFolder(ActionEvent actionEvent){
        try {
            readEmail.setCurrentFolder(folderChoice.getValue().toString());

            emailListView.setItems(readEmail.readEmailFromServer());

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,e.getMessage());
        }
    }

    @FXML
    private void showSelectedMessage(MouseEvent mouseEvent) {
        if(readEmail.getCurrentFolder()!=null) {
            int id = emailListView.getSelectionModel().getSelectedIndex();
            WebEngine webEngine = messageView.getEngine();
            if(!ShifrManager.shifrText) {
                webEngine.loadContent(readEmail.getBodyMessage(id), "text/html");
            }else{
                String text = readEmail.getBodyMessage(id);
                webEngine.loadContent(getDecodeText(text), "text/html");
            }
        }
    }

    /**
     * метод, который расшифровывает текст
     * @param text зашифрованный текст
     * @return String расшифрованный текст
     */
    private String getDecodeText(String text){
        //arr[0] - зашифрованный текст
        //arr[1] - des ключ(зашифрованный)
        //arr[2] - rsa ключ d
        //arr[3] - rsa ключ n

        String[] arr = text.split("\\*end\\*");
        List<String> key = Arrays.asList(arr[1].split(","));
        RSA rsa = new RSA();
        rsa.setInputDataForDecode(key);
        rsa.setD(Long.parseLong(arr[2]));
        rsa.setN(Long.parseLong(arr[3]));

        DES des = new DES();
        return  des.DecodeDES(arr[0],rsa.RSADecode());
    }
    @FXML
    private void shifrManagerShow(ActionEvent actionEvent) {
        Stage tmp = (Stage) parent.getScene().getWindow();
        tmp.close();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/shifrManager.fxml"));
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

        ShifrManager.stage = stage;
        stage.show();
    }
}
