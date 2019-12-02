import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Класс, который является обработчиком формы Login.fxml
 * @author Akim
 * @version  1.0
 */
public class LoginController implements Initializable {

    public static java.sql.Connection con;
    public static java.sql.Statement stmt;
    public static java.sql.ResultSet rs;

    /**
     * адресс для подключение к MySQL
     */
    private static final String url = "jdbc:mysql://localhost:3306/kindergarten";

    /**
     * имя пользователя для подключения
     */
    private static final String user = "root";

    /**
     * пароль пользователя
     */
    private static final String password = "1234";

    public static String loginUser;
    public static String idUser;

    @FXML
    public Label topLabel;
    @FXML
    private AnchorPane parent;
    @FXML
    private Pane content;
    @FXML
    private HBox top;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;

    public static  Stage stage = null;

    /**
     * Переменная, в которой хранится смещение по x
     */
    private double xOffSet = 0;

    /**
     * Переменная, в которой хранится смещение по y
     */
    private double yOffSet = 0;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        makeStageDragable();
    }

    /**
     * Метод, который реализует перемещения сцены с сажатой кнопкой мыши
     * Работа с переменными {@link LoginController#xOffSet}, {@link LoginController#yOffSet}
     */
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


    /**
     * Метод, который реализует вход пользователя
     * @param event событие
     * @throws IOException стандартное исключение
     */
    @FXML
    private void handle_login(ActionEvent event) throws IOException, ClassNotFoundException {

        String query= "SELECT account_table.id_account, account_table.login, account_table.password  FROM account_table " +
                "WHERE (account_table.login ='"+loginField.getText()+"') AND " +
                "(account_table.password ='"+passwordField.getText()+"');";

        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost/my_mail_client", "root", "1234");
            // getting Statement object to execute query
            stmt = con.createStatement();

            // executing SELECT query
            rs = stmt.executeQuery(query);

            if (rs.next()) {
                loginUser = rs.getString("login");
                idUser = rs.getString("id_account");
                Stage tmp = (Stage) parent.getScene().getWindow();

                tmp.close();

                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource("/views/Menu.fxml"));
                try {
                    loader.load();
                }catch (IOException e){
                    e.printStackTrace();
                }

                Parent root = loader.getRoot();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.initStyle(StageStyle.UNDECORATED);
                MenuController.stage = stage;
                stage.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        try {
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
