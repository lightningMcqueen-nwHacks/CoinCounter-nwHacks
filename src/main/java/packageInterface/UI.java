package packageInterface;


import java.lang.reflect.Array;
import java.util.*;

import denominator.CoinRadiusMatrix;
import denominator.Exceptions.RatioNotFoundException;
import denominator.models.Classifier;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import java.io.File;

import openCV.CoinFinder;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import nu.pattern.OpenCV;

import static org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_COLOR;


public class UI extends Application {

    private static BorderPane backgroundPane = new BorderPane();
    public static Image coinImg;
    private static HBox userControls = new HBox(22.5);
    private static VBox result = new VBox();
    private static String imgLoc;
    private static TableView resultTable = new TableView();
    private static Integer smallestCoinValue = null;

    private static HashMap<String, Integer> coinCount = null;
    private static Integer total = null;

    public void start(Stage stage) throws Exception{


        Button uploadButton = new Button("Upload Image");
        Label uploadLabel = new Label();
        uploadLabel.setPrefWidth(50);
        Text filler = new Text();

        ComboBox smallestCoinChoices = new ComboBox();

        LinkedHashMap<String, Integer> dictionary = new LinkedHashMap<String, Integer>();

        dictionary.put("Dime - 10¢", 10);
        dictionary.put("Nickel - 5¢", 5);
        dictionary.put("Quarter - 25¢", 25);
        dictionary.put("Loonie - $1", 1);
        dictionary.put("Toonie - $2", 2);

        List keys = new ArrayList<>(dictionary.keySet());

        for(int i = 0; i<keys.size(); i++){
            smallestCoinChoices.getItems().add(keys.get(i));
        }


        uploadButton.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            FileChooser imgUploader = new FileChooser();
            imgUploader.setTitle("Upload Image");
            File selectedFile = imgUploader.showOpenDialog(null);

            if(selectedFile != null){
                OpenCV.loadShared();
                imgLoc = selectedFile.toURI().toString();
                System.out.println("img uploaded");
            }
        });
        Button runButton = new Button("Coin Count");
        runButton.addEventFilter(MouseEvent.MOUSE_CLICKED, event ->{
            if(smallestCoinChoices.getValue() != null && imgLoc != null){

                smallestCoinValue = dictionary.get(smallestCoinChoices.getValue());

                System.out.println("Success");
                //TODO: Implement other classes

                //System.out.println(imgLoc.substring(5));

                Mat src = Imgcodecs.imread(imgLoc.substring(5), CV_LOAD_IMAGE_COLOR);

                ArrayList<Integer> sizes = CoinFinder.findCoins(src);

                //System.out.println(sizes.toString());

                HashMap<Double,Integer> coins = null;

                try{
                     coins = CoinRadiusMatrix.countRatio(sizes);
                }catch (RatioNotFoundException e) {
                    //TODO
                }

                String smallestCoinName = "";

                switch (smallestCoinValue){
                    case 10: smallestCoinName = "Dime"; break;
                    case 5: smallestCoinName = "Nickel"; break;
                    case 25: smallestCoinName = "Quartrer"; break;
                    case 1: smallestCoinName = "Loonie"; break;
                    case 2: smallestCoinName = "Toonie"; break;
                }

                Classifier classifier = new Classifier(coins, smallestCoinName);


                coinCount = classifier.classify(coins);
                total = classifier.counter(coinCount);

                System.out.println();

                for(Map.Entry<String, Integer> entry : coinCount.entrySet()) {
                    String key = entry.getKey();
                    int value = entry.getValue();
                    System.out.println("Coin Type: " + key + "Amount: " + value);

                }



            }
            else if(imgLoc == null){
                final Stage popUp = new Stage();
                popUp.initModality(Modality.APPLICATION_MODAL);
                popUp.initOwner(stage);
                VBox popUpBox = new VBox(20);
                popUpBox.getChildren().add(new Text("Please upload a valid image."));
                Scene popUpScene = new Scene(popUpBox, 200, 50);
                popUp.setScene(popUpScene);
                popUp.show();

            }

            else if(smallestCoinChoices.getValue() == null){
                final Stage popUp = new Stage();
                popUp.initModality(Modality.APPLICATION_MODAL);
                popUp.initOwner(stage);
                VBox popUpBox = new VBox(20);
                popUpBox.getChildren().add(new Text("Please choose the least valued coin."));
                Scene popUpScene = new Scene(popUpBox, 200, 50);
                popUp.setScene(popUpScene);
                popUp.setResizable(false);
                popUp.show();
            }



        });




        userControls.getChildren().addAll(filler,uploadButton,smallestCoinChoices,runButton);

        result.setAlignment(Pos.CENTER);
        Label resultLabel = new Label("Result");


        resultTable.setEditable(false);
        TableColumn coinTypeCol = new TableColumn("Coin Type");
        TableColumn coinAmountCol = new TableColumn("Amount");
        resultTable.getColumns().addAll(coinTypeCol,coinAmountCol);

        coinTypeCol.setMinWidth(200);
        coinAmountCol.setMinWidth(200);

        result.getChildren().addAll(resultLabel,resultTable,userControls);

        backgroundPane.setCenter(result);

        Scene inputScene = new Scene(backgroundPane,400,500);

        stage.setScene(inputScene);
        stage.show();

    }


    public static void main(String[] args){
        launch(args);

    }

}
