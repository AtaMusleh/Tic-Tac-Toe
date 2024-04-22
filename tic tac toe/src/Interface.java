import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.*;

public class Interface extends Application {
	Stage mainStage = new Stage();

	@Override
	public void start(Stage stage) throws Exception {
		menu();
	}

	public static void main(String[] args) {
		launch(args);
	}

	public void menu() {
		Label l = new Label("TIC TAC TOE");
		l.setFont(Font.font("Times New Roman",FontWeight.BOLD, 36));
		l.setStyle("-fx-text-fill: white");
		Button easy = new Button("Easy");
		Button hard = new Button("Hard");
		Button human = new Button("1v1");
		human.setPrefHeight(45);
		human.setPrefWidth(80);
		easy.setStyle("-fx-background-radius: 50;-fx-padding: 10 20;-fx-background-color:white");
		hard.setStyle("-fx-background-radius: 50;-fx-padding: 10 20;-fx-background-color:white");
		human.setStyle("-fx-background-radius: 50;-fx-padding: 10 20;-fx-background-color:white");
		easy.setOnMouseEntered(e -> {
			easy.setStyle(
					"-fx-background-radius: 50;-fx-padding: 10 20;-fx-background-color:linear-gradient(to bottom,white,grey);");
			easy.setTextFill(easy.getTextFill());
		});
		easy.setOnMouseExited(e -> {
			easy.setStyle("-fx-background-radius: 50;-fx-padding: 10 20;-fx-background-color:white;");
			easy.setTextFill(easy.getTextFill());
		});
		hard.setOnMouseEntered(e -> {
			hard.setStyle(
					"-fx-background-radius: 50;-fx-padding: 10 20;-fx-background-color:linear-gradient(to bottom,white,grey);");
			hard.setTextFill(hard.getTextFill());
		});
		hard.setOnMouseExited(e -> {
			hard.setStyle("-fx-background-radius: 50;-fx-padding: 10 20;-fx-background-color:white;");
			hard.setTextFill(hard.getTextFill());
		});
		human.setOnMouseEntered(e -> {
			human.setStyle(
					"-fx-background-radius: 50;-fx-padding: 10 20;-fx-background-color:linear-gradient(to bottom,white,grey);");
			human.setTextFill(human.getTextFill());
		});
		human.setOnMouseExited(e -> {
			human.setStyle("-fx-background-radius: 50;-fx-padding: 10 20;-fx-background-color:white;");
			human.setTextFill(human.getTextFill());
		});
		easy.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
		hard.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
		human.setFont(Font.font("Times New Roman", FontWeight.BOLD, 20));
		easy.setOnAction(e -> {
			easy es = new easy();
			try {
				es.start(mainStage);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		hard.setOnAction(e -> {
			hard h = new hard();
			try {
				h.start(mainStage);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		human.setOnAction(e -> {
			human h = new human();
			try {
				h.start(mainStage);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		VBox v = new VBox(15, l, easy, hard, human);
		v.setAlignment(Pos.CENTER);
		v.setStyle("-fx-background-color: dimgrey;");
		Scene s = new Scene(v, 400, 400);
		mainStage.setScene(s);
		mainStage.setTitle("Tic Tac Toe");
		mainStage.show();

	}

}
