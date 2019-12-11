import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Класс контроллер для shifrManager.fxml
 * @author Akim
 * @version 1.0
 */
public class ShifrManager implements Initializable {

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
    public static String DESEncryptKey;
    /**
     * Простое чилсо q для RSA
     */
    public static int RSAQ;
    /**
     * Простое число p для RSA
     */
    public static int RSAP;
    private double xOffSet = 0;
    private double yOffSet = 0;
    public static Stage stage = null;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
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

    @FXML
    private void onShifrText(ActionEvent actionEvent) {
        shifrTextPane.setVisible(shifrText);
        shifrText= !shifrText;
    }
    @FXML
    private void onECP(ActionEvent actionEvent) {
        ECPPane.setVisible(ECP);
        ECP= !ECP;
    }

}
