import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.sql.DriverManager;

/**
 * Класс, который является обработчиком формы Login.fxml
 * @author Akim
 * @version  1.0
 */
public class LoginController implements Initializable {

    public static java.sql.Connection con = null;
    public static java.sql.Statement stmt = null;
    public static java.sql.ResultSet rs = null;

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

    /**
     * статическое поле, которое хранит логин пользователя
     */
    public static String loginUser;
    /**
     * статическое поле, которое хранит идентифекатор пользователя
     */
    public static String idUser;
    /**
     * Вспомогательная переменная для метода slideButton
     * @see LoginController#slideButton(ActionEvent)
     */
    private int slide = 0;

    @FXML
    private Label topLabel;
    @FXML
    private Button slideBtn;
    @FXML
    private TextField loginRegister;
    @FXML
    private PasswordField passwordRegister;
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
            con = DriverManager.getConnection("jdbc:mysql://localhost/my_mail_client", "root", "1234");

        } catch (SQLException e) {
            e.printStackTrace();
        }


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
            stmt = con.createStatement();
            // исполнение запроса
            rs = stmt.executeQuery(query);

            if (rs.next()) {
                loginUser = rs.getString("login");
                idUser = rs.getString("id_account");
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
     * выполняется закрытие MySQL продключения
     * @param event событие мыши
     */
    @FXML
    private void close_app(MouseEvent event) {
        try {
            if(!con.isClosed())
                con.close();
            if(stmt!=null)
                stmt.close();
            if(rs!=null)
                rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    /**
     * Код для кнопки регистрации
     * Вставка соответствующих данных в таблицу account_table
     * @param event событие
     */
    @FXML
    private void registerButton(ActionEvent event){
        String query = "INSERT INTO account_table (login, password) " +
                "VALUES ('"+loginRegister.getText()+"', '"+passwordRegister.getText()+"');";

        try {
            stmt = con.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Обработчик для "слайдера", который позволяет выбрать функцию регистрации/входа
     * Используется вспомогательная переменная slideBtn для реализации анимации
     * @see LoginController#slideBtn
     * @param event событие
     */
    @FXML
    private void slideButton(ActionEvent event){
        if(slide==0){
            slideBtn.setLayoutX(500);
            loginRegister.setText("");
            passwordRegister.setText("");
            topLabel.setText("Регистрация");
            slide=1;
        }
        else if(slide==1){
            slideBtn.setLayoutX(0);
            loginField.setText("");
            passwordField.setText("");
            topLabel.setText("Вход");
            slide=0;
        }
    }
}
