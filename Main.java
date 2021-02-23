package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ContentHandler;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class Main extends Application {

    File file;
    String [] names;
    String [] data;
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Tika Song to Database");

        GridPane gp = new GridPane();

        Button select = new Button("Select");
        GridPane.setConstraints(select, 0,0);
        select.setOnAction(event -> {
            FileChooser fc = new FileChooser();
             file = fc.showOpenDialog(primaryStage);

            Parser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();

            try {
                InputStream IS = new FileInputStream(file);

                parser.parse(IS, handler, metadata, context);
            }catch(Exception e){
                e.printStackTrace();
            }
             names = metadata.names();
            data = new String[names.length];

            for(int i = 0; i <=names.length-1; i++){
                if(data[i] == null || data[i] == ""){
                    data[i] = metadata.get(names[i]);
                }
            }
        });

        Button writetoDatabase = new Button("Write to Database");
        GridPane.setConstraints(writetoDatabase, 0,1);
        writetoDatabase.setOnAction(event->{
            String url = "jdbc:sqlite:C://sqlite/db/Songs.db";

            // SQL statement for creating a new table
            String sql = "CREATE TABLE IF NOT EXISTS SongData (\n"
                    + names[0] + " text PRIMARY KEY,\n"
                    + names[1]+  " text NOT NULL,\n"
                    + names[2] +   " capacity real\n"
                    + ");";

            try (Connection conn = DriverManager.getConnection(url);
                 Statement stmt = conn.createStatement()) {
                // create a new table
                stmt.execute(sql);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

        });

        gp.getChildren().addAll(select, writetoDatabase);

        primaryStage.setScene(new Scene(gp, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
