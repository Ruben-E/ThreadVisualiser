
import javafx.application.Platform;
import javafx.collections.*;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by RubenE on 1-3-2015.
 */
public class ThreadVisualiserScene {
    private Map<String, ObservableList<Integer>> threadTimes;
    private ExecutorService executorService;
    private ScrollPane scrollPane;
    private Group root;

    public ThreadVisualiserScene() {
        threadTimes = new LinkedHashMap<String, ObservableList<Integer>>();
        executorService = Executors.newFixedThreadPool(20);
        scrollPane = new ScrollPane();
        root = new Group();
        scrollPane.setContent(root);

        Scale scale = new Scale();
        root.getTransforms().add(scale);
        scale.setX(2);
    }

    public void draw() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(root);

        int widthScale = 5;
        int margin = 5;
        int y = margin;
        int x = margin;
        int height = 20;
        for (Map.Entry<String, ObservableList<Integer>> entry : threadTimes.entrySet()) {
            for (int time : entry.getValue()) {
                int width = time * widthScale;
                Rectangle rectangle = new Rectangle(x, y, width, height);
                root.getChildren().add(rectangle);
                x = x + width + margin;
            }

            x = margin;
            y = y + height + margin;
        }
    }

    public void redraw() {
        root.getChildren().removeAll();
        draw();
    }

    public Scene build() {
        runThreads();

        return new Scene(scrollPane, 500, 400);
    }

    private synchronized void addThreadTime(String threadId, int time) {
        ObservableList<Integer> threadTime = threadTimes.get(threadId);
        if (threadTime != null) {
            threadTime.add(time);
        } else {
            threadTime = FXCollections.observableArrayList();
            threadTime.add(time);
            threadTime.addListener(new ListChangeListener<Integer>() {
                @Override
                public void onChanged(Change<? extends Integer> c) {
                    redraw();
                }
            });
            threadTimes.put(threadId, threadTime);
        }
    }

    public void runThreads() {
        System.out.println("START!");

        for(int i = 1; i < 100; i++) {
            Runnable task = new Runnable() {
                public void run() {
                    try {
                        final int sleep = (int) Math.round(Math.random() * 10);
                        final String threadId = String.valueOf(Thread.currentThread().getId());
                        TimeUnit.SECONDS.sleep(sleep);

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                addThreadTime(threadId, sleep);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

            executorService.submit(task);
        }
    }

    public void exit() {
        executorService.shutdown();
    }
}
