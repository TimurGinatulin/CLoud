import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class App extends Application {
    static Scene sign;
    static Scene main;
    static Stage stage;


    @Override
    public void start(Stage stage) throws Exception {
        App.stage = stage;
        sign = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("signWindow.fxml"))));
        main = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("main.fxml"))));
        App.stage.setScene(main);
        App.stage.show();
    }

    public static void setScene(Window scene) {
        switch (scene) {
            case MAIN:
                stage.setScene(main);
                break;
            case SIGN: {
                stage.setScene(sign);
                break;
            }
        }
    }
}
