import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ThreadVisualiserApplication extends Application {



    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) {
        final ThreadVisualiserScene threadVisualiserScene = new ThreadVisualiserScene();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                threadVisualiserScene.exit();
            }
        });

        primaryStage.setScene(threadVisualiserScene.build());
        primaryStage.show();
    }


}