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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import shifr.DES;


/**
 * Класс, который является обработчиком формы Menu.fxml
 * @author Akim
 * @version  1.0
 */
public class MenuController implements Initializable {


    public AnchorPane pane2;

    @FXML
    private TextField passwordAddEMail;
    @FXML
    private TextField loginAddEmail;
    @FXML
    private ListView emailListView;
    @FXML
    public ChoiceBox emailChoice;
    @FXML
    public Label labelFolders;
    @FXML
    private AnchorPane parent;
    @FXML
    private HBox top;

    private ReadEmail readEmail;

    public static Stage stage = null;

    private double xOffSet = 0;
    private double yOffSet = 0;

    private boolean switcher =true;
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //readEmail = new ReadEmail();
        //emailListView.setItems(readEmail.readEmailFromServer());

        String query = "SELECT user_email.email FROM user_email " +
                "WHERE account_table_id_account =" + LoginController.idUser+";";

        ObservableList<String> emailList = FXCollections.observableArrayList();
        try {
            LoginController.rs = LoginController.stmt.executeQuery(query);
            while(LoginController.rs.next()){
                emailList.add(LoginController.rs.getString("email"));
            }
            emailChoice.setItems(emailList);

        } catch (SQLException e) {
            e.printStackTrace();
        }
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
    private void handle_logout(ActionEvent event) throws IOException {
        /*
        Parent menu = FXMLLoader.load(getClass().getResource("/views/Login.fxml"));
        content.getChildren().removeAll();
        content.getChildren().setAll(menu);
         */

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
        labelFolders.setText(emailChoice.getValue().toString());
    }

    /**
     * Отображает панель для добавления новой почты
     * @param event событие
     */
    @FXML
    private void showAddEMailPanel(ActionEvent event){
        pane2.setVisible(switcher);
        if(switcher)
            switcher=false;
        else
            switcher=true;
    }

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
