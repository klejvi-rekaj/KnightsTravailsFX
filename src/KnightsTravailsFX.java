
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class TestJavaFX extends Application {
    @Override
    public void start(Stage stage) {
        Label label = new Label("JavaFX is installed and working!");
        Scene scene = new Scene(label, 300, 200);
        stage.setScene(scene);
        stage.setTitle("JavaFX Test");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
