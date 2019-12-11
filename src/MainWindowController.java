import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
    private ChoiceBox emailChoiceForDelete;
    @FXML
    private TextField passwordAddEMail;
    @FXML
    private TextField loginAddEmail;
    @FXML
    private ListView emailListView;
    @FXML
    private ChoiceBox emailChoice;
    @FXML
    private Label labelFolders;
    @FXML
    private AnchorPane parent;
    @FXML
    private HBox top;
    @FXML
    private ChoiceBox folderChoice;

    private ReadEmail readEmail;

    public static Stage stage = null;

    private double xOffSet = 0;
    private double yOffSet = 0;

    private boolean switcher =true;
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //readEmail = new ReadEmail();
        //emailListView.setItems(readEmail.readEmailFromServer());

        emailChoice.setItems(updateDataChoiceBox());
        emailChoiceForDelete.setItems(updateDataChoiceBox());
        readEmail = new ReadEmail();

        makeStageDragable();
    }

    /**
     * Вспомогательный метод, который выполняет запрос на получение email адресов,
     * а после формирует из них список, который необходим для ChoiceBox
     * @return ObservableList<String> список адресов
     */
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
        this.stage.setIconified(true);
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
        readEmail.setIMAP_Server(getIMAPServer(readEmail.getIMAP_AUTH_EMAIL()));

        folderChoice.setItems(readEmail.getFolderList());
    }

    /**
     * Вспомогательная функция для получения сервера IMAP
     * @param mail почта пользователя, которую он выбрал
     * @return адрес сервера IMAP
     */
    private String getIMAPServer(String mail){
        String[] array = mail.split("@");
        return "imap."+array[1];
    }

    /**
     * Отображает панель для добавления новой почты
     * @param event событие
     */
    @FXML
    private void showAddEMailPanel(ActionEvent event){
        paneForAddNewMail.setVisible(switcher);
        paneForDeleteMail.setVisible(switcher);
        if(switcher)
            switcher=false;
        else
            switcher=true;
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        emailChoice.setItems(updateDataChoiceBox());
        emailChoiceForDelete.setItems(updateDataChoiceBox());
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
        readEmail.setCurrentFolder(folderChoice.getValue().toString());
        emailListView.setItems(readEmail.readEmailFromServer());
    }


    public void showSelectedMessage(MouseEvent mouseEvent) {
        if(readEmail.getCurrentFolder()!=null) {
            int id = emailListView.getSelectionModel().getSelectedIndex();
            WebEngine webEngine = messageView.getEngine();
            webEngine.loadContent(readEmail.getBodyMessage(id), "text/html");
        }
    }
}
