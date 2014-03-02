package by.bsu.kurs.stepanov.visualisation;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Created by IntelliJ IDEA.
 * User: Stepanov Dmitriy
 * Date: 22.02.13
 * Time: 17:44
 * To change this template use File | Settings | File Templates.
 */
public class Main extends Application {

    private GoogleMap map;

    public static void main(String[] args) {
        //Application.launch(args);
        Application.launch(MapScene.class, args);

    }

    /* private void draw(GoogleMap map) throws InterruptedException {
         System.out.println("Start drawing");
         int delay = 10000;
         while (true) {
             map.startJumping();
             wait(delay);
             map.switchHybrid();
             wait(delay);
             map.switchRoadmap();
             wait(delay);
             map.switchSatellite();
             wait(delay);
             map.switchTerrain();
             wait(delay);
             map.stopJumping();
             wait(delay);
             map.setMapCenter(55.5, 55.5);
             wait(delay);
             map.setMarkerPosition(55.5, 55.5);
             wait(delay);

         }
     }      */
    public MapEvent prevMap;

    @Override
    public void start(Stage stage) throws Exception {
        StackPane root = new StackPane();
        map = new GoogleMap();
        /*map.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {

            }
        });   */
        root.getChildren().add(map);
        map.setOnMapLatLngChanged(new EventHandler<MapEvent>() {
            @Override
            public void handle(MapEvent mapEvent) {
                if (prevMap != null) {
                    map.addRoad(mapEvent.getLat(), mapEvent.getLng(), prevMap.getLat(), prevMap.getLng());
                }
                prevMap = mapEvent;
            }
        });
        stage.setScene(new Scene(root));
        stage.show();

    }
    /*@Override
       public void start(Stage primaryStage) {
           Group root = new Group();
           Scene scene = new Scene(root, 800, 600, Color.BLACK);
           primaryStage.setScene(scene);
           Group circles = new Group();
           for (int i = 0; i < 30; i++) {
               Circle circle = new Circle(150, Color.web("white", 0.05));
               circle.setStrokeType(StrokeType.OUTSIDE);
               circle.setStroke(Color.web("white", 0.16));
               circle.setStrokeWidth(4);
               circles.getChildren().add(circle);
           }
           Rectangle colors = new Rectangle(scene.getWidth(), scene.getHeight(),
                   new LinearGradient(0f, 1f, 1f, 0f, true, CycleMethod.NO_CYCLE, new Stop[]{
                       new Stop(0, Color.web("#f8bd55")),
                       new Stop(0.14, Color.web("#c0fe56")),
                       new Stop(0.28, Color.web("#5dfbc1")),
                       new Stop(0.43, Color.web("#64c2f8")),
                       new Stop(0.57, Color.web("#be4af7")),
                       new Stop(0.71, Color.web("#ed5fc2")),
                       new Stop(0.85, Color.web("#ef504c")),
                       new Stop(1, Color.web("#f2660f")),}));
           colors.widthProperty().bind(scene.widthProperty());
           colors.heightProperty().bind(scene.heightProperty());
           Group blendModeGroup =
                   new Group(new Group(new Rectangle(scene.getWidth(), scene.getHeight(),
                        Color.BLACK), circles), colors);
           colors.setBlendMode(BlendMode.OVERLAY);
           root.getChildren().add(blendModeGroup);
           circles.setEffect(new BoxBlur(10, 10, 3));
           Timeline timeline = new Timeline();
           for (Node circle : circles.getChildren()) {
               timeline.getKeyFrames().addAll(
                       new KeyFrame(Duration.ZERO, // set start position at 0
                       new KeyValue(circle.translateXProperty(), random() * 800),
                       new KeyValue(circle.translateYProperty(), random() * 600)),
                       new KeyFrame(new Duration(40000), // set end position at 40s
                       new KeyValue(circle.translateXProperty(), random() * 800),
                       new KeyValue(circle.translateYProperty(), random() * 600)));
           }
           // play 40s of animation
           timeline.play();
           primaryStage.show();
       }*/
}
