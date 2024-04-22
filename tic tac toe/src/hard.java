import java.util.Optional;
import java.util.Random;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class hard extends Application {
	private Stage stage;
	private char board[][] = { { ' ', ' ', ' ' }, { ' ', ' ', ' ' }, { ' ', ' ', ' ' } };
	private char currentPlayer;
	private Label player1Name;
	private Label player2Name;
	private Label tie;
	private int player1Score = 0;
	private int player2Score = 0;
	private int tieS = 0;
	private Label turnLabel, roundsLabel;
	private Button[][] buttons = new Button[3][3];
	private String player1, player2;
	private int currentRound = 0;
	private int numberOfRounds;
	private boolean showMessages;
	Interface i = new Interface();

	@Override
	public void start(Stage stage) throws Exception {

		this.stage = stage;

		String[] playerInfo = getPlayerInfo();
		if (playerInfo == null) {
			stage.close();
			i.start(stage);
			return;
		}

		if (playerInfo[0].isEmpty()) {
			player1 = "Player";
		} else {
			player1 = playerInfo[0];
		}

		player2 = "AI";
		if (playerInfo[1].isEmpty()) {
			numberOfRounds = 5;
		} else {
			numberOfRounds = Integer.parseInt(playerInfo[1]);
		}

		showMessages = Boolean.parseBoolean(playerInfo[2]);

		player1Name = new Label(player1 + "(X): " + player1Score);
		player2Name = new Label(player2 + "(O): " + player2Score);
		tie = new Label("Tie: " + tieS);
		player1Name.setStyle("-fx-font-size: 24; -fx-text-fill: white");
		player2Name.setStyle("-fx-font-size: 24; -fx-text-fill: white");
		tie.setStyle("-fx-font-size: 24; -fx-text-fill: white");
		turnLabel = new Label();
		turnLabel.setStyle("-fx-font-size:24; -fx-text-fill: white");
		updateTurnLabel();
		roundsLabel = new Label("Round: " + (currentRound + 1) + "/" + numberOfRounds);
		roundsLabel.setStyle("-fx-font-size:24; -fx-text-fill: white");
		VBox vb = new VBox(20, player1Name, tie, player2Name, roundsLabel);
		vb.setAlignment(Pos.CENTER);
		GridPane gp = createBoard();
		HBox h = new HBox(20, vb, gp);
		h.setAlignment(Pos.CENTER);
		Image image = new Image("back.png");
		ImageView imageView = new ImageView(image);
		ColorAdjust colorAdjust = new ColorAdjust();
		colorAdjust.setContrast(-1.0);
		colorAdjust.setBrightness(100.0);
		imageView.setEffect(colorAdjust);
		imageView.setFitWidth(50);
		imageView.setFitHeight(50);
		Button back = new Button();
		back.setGraphic(imageView);
		back.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

		back.setOnAction(e -> {

			try {
				i.start(stage);
				stage.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		HBox hb = new HBox(back);
		hb.setAlignment(Pos.TOP_LEFT);
		StackPane sp = new StackPane(hb, turnLabel);
		sp.setAlignment(hb, Pos.TOP_LEFT);
		sp.setAlignment(turnLabel, Pos.TOP_CENTER);
		VBox v = new VBox(10, sp, h);
		v.setStyle("-fx-background-color: dimgrey;");
		Scene scene = new Scene(v, 500, 400);
		stage.setScene(scene);
		stage.show();

		currentPlayer = whoStarts();

		updateTurnLabel();
		if (currentPlayer == 'O') {
			computerMove();
		}
		updateTurnLabel();
	}

	public static void main(String[] args) {
		launch(args);
	}

	private String[] getPlayerInfo() {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 20, 20, 20));

		TextField playerNameField = new TextField();
		Spinner<Integer> roundsInputSpinner = new Spinner<>();
		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 5);
		roundsInputSpinner.setValueFactory(valueFactory);
		CheckBox messagesCheckBox = new CheckBox("Show AI Messages");

		grid.add(new Label("Enter Your Name:"), 0, 0);
		grid.add(playerNameField, 1, 0);
		grid.add(new Label("Enter the number of rounds:"), 0, 1);
		grid.add(roundsInputSpinner, 1, 1);
		grid.add(messagesCheckBox, 0, 2, 2, 1);

		Dialog<String[]> dialog = new Dialog<>();
		dialog.setTitle("Player Information");
		dialog.getDialogPane().setContent(grid);

		ButtonType okButton = new ButtonType("OK", ButtonData.OK_DONE);
		ButtonType cancelButton = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
		dialog.getDialogPane().getButtonTypes().addAll(okButton, cancelButton);

		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == okButton) {
				return new String[] { playerNameField.getText(), String.valueOf(roundsInputSpinner.getValue()),
						String.valueOf(messagesCheckBox.isSelected()) };
			}
			return null;
		});

		Optional<String[]> result = dialog.showAndWait();
		return result.orElse(null);
	}

	private void showMoveMessages() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("AI Move");
		alert.setHeaderText(null);

		StringBuilder message = new StringBuilder("(1: Winning Move, 0: Draw, -1: Blocking Move)\n");
		message.append("Possible moves for AI:\n");

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board[i][j] == ' ') {
					board[i][j] = 'O';

					int score = minimax(false);

					message.append("Move (").append(i + 1).append(", ").append(j + 1).append(") - Score: ")
							.append(score);

					board[i][j] = ' ';

					message.append("\n");
				}
			}
		}

		int[] bestAiMove = bestAiMove();
		
		message.append("AI has chosen to go to: (").append(bestAiMove[0] + 1).append(", ").append(bestAiMove[1] + 1 + ")");

		alert.setContentText(message.toString());
		alert.showAndWait();
	}

	private char whoStarts() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Game Start");
		alert.setHeaderText(null);
		alert.setContentText("Who starts the game?");

		ButtonType player1Button = new ButtonType(player1);
		ButtonType player2Button = new ButtonType(player2);
		ButtonType randomButton = new ButtonType("Random");
		alert.getButtonTypes().setAll(player1Button, player2Button, randomButton);

		Optional<ButtonType> result = alert.showAndWait();

		if (result.isPresent()) {
			if (result.get() == player1Button) {
				return 'X';
			} else if (result.get() == player2Button) {
				return 'O';
			} else if (result.get() == randomButton) {
				return new Random().nextBoolean() ? 'X' : 'O';
			}
		}
		return new Random().nextBoolean() ? 'X' : 'O';
	}

	private GridPane createBoard() {
		GridPane gridPane = new GridPane();
		gridPane.setAlignment(Pos.CENTER);

		for (int row = 0; row < 3; row++) {
			for (int col = 0; col < 3; col++) {
				Button button = createCell(row, col);
				button.setOnMouseEntered(e -> {
					button.setStyle(
							"-fx-background-color:linear-gradient(to bottom,white,dimgrey,grey);-fx-font-size:45;-fx-border-color: white;-fx-border-width: 2px;");
					button.setTextFill(button.getTextFill());
				});
				button.setOnMouseExited(e -> {
					button.setStyle(
							"-fx-background-color:dimgrey;-fx-font-size:45;-fx-border-color: white;-fx-border-width: 2px;");
					button.setTextFill(button.getTextFill());
				});
				buttons[row][col] = button;
				gridPane.add(button, col, row);
			}
		}

		return gridPane;
	}

	private Button createCell(int row, int col) {
		Button button = new Button();
		button.setMinSize(100, 100);
		button.setStyle("-fx-background-color: transparent; " + "-fx-border-color: white; " + "-fx-border-width: 2px; "
				+ "-fx-text-fill: white; " + "-fx-font-size: 45px;");
		button.setOnAction(e -> clicks(button, row, col));

		return button;
	}

	private void clicks(Button button, int row, int col) {
		if (button.getText().isEmpty()) {
			button.setText(String.valueOf(currentPlayer));
			board[row][col] = currentPlayer;

			if (checkWinner()) {
				announceWinner("Player " + currentPlayer + " wins!");
				resetGame();
			} else if (isFull()) {
				announceWinner("It's a tie!");
				resetGame();
			} else {
				switchPlayer();
			}
		}
	}

	private void switchPlayer() {
		if (currentPlayer == 'O') {
			currentPlayer = 'X';
		} else {
			currentPlayer = 'O';
		}

		if (currentPlayer == 'O') {
			computerMove();
		}
		updateTurnLabel();
	}

	private boolean checkWinner() {
		for (int i = 0; i < 3; i++) {
			if ((board[i][0] == board[i][1] && board[i][1] == board[i][2] && board[i][0] != ' ')
					|| (board[0][i] == board[1][i] && board[1][i] == board[2][i] && board[0][i] != ' ')) {
				return true;
			}
		}

		return (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != ' ')
				|| (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != ' ');
	}

	private boolean isFull() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board[i][j] == ' ') {
					return false;
				}
			}
		}
		return true;
	}

	private void announceWinner(String message) {
		if (message.startsWith("Player X")) {
			player1Score++;
			player1Name.setText(player1 + "(X): " + player1Score);
		} else if (message.startsWith("Player O")) {
			player2Score++;
			player2Name.setText(player2 + "(O): " + player2Score);
		} else {
			tieS++;
			tie.setText("Tie: " + tieS);
		}

	}

	private void resetGame() {
		currentRound++;

		if (currentRound == numberOfRounds) {
			if (player1Score > player2Score) {
				showWinnerAlert(player1 + " (X) wins!");
			} else if (player2Score > player1Score) {
				showWinnerAlert(player2 + " (O) wins!");
			} else {
				showWinnerAlert("It's a tie!");
			}

			try {
				stage.close();
				i.start(stage);
			} catch (Exception e) {

				e.printStackTrace();
			}
		} else if (player1Score >= numberOfRounds / 2 + 1 || player2Score >= numberOfRounds / 2 + 1) {
			String alertMessage;
			if (player1Score > player2Score) {
				alertMessage = player1 + " (X) is Winning. Do you want to continue or end the game?";
			} else {
				alertMessage = player2 + " (O) is Winning. Do you want to continue or end the game?";
			}

			if (showContinueGameAlert(alertMessage)) {
				clearBoard();
				roundsLabel.setText("Round: " + (currentRound + 1) + "/" + numberOfRounds);
				currentPlayer = whoStarts();
				updateTurnLabel();
			} else {
				try {
					stage.close();
					i.start(stage);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} else {
			String resultMessage;
			if (checkWinner()) {
				resultMessage = "Player " + currentPlayer + " wins!";
			} else if (isFull()) {
				resultMessage = "It's a tie!";
			} else {
				resultMessage = "";
			}

			if (!resultMessage.isEmpty()) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Game Over");
				alert.setHeaderText(null);
				alert.setContentText(resultMessage);
				alert.showAndWait();
			}
			clearBoard();
			roundsLabel.setText("Round: " + (currentRound + 1) + "/" + numberOfRounds);
			currentPlayer = whoStarts();
			updateTurnLabel();

			if (currentPlayer == 'O') {
				computerMove();
			}
		}

	}

	private boolean showContinueGameAlert(String message) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Continue Game");
		alert.setHeaderText(null);
		alert.setContentText(message);

		ButtonType continueButton = new ButtonType("Continue");
		ButtonType endButton = new ButtonType("End Game", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(continueButton, endButton);

		Optional<ButtonType> result = alert.showAndWait();
		return result.isPresent() && result.get() == continueButton;
	}

	private void computerMove() {
		if (currentPlayer == 'O') {
			if (showMessages) {
				showMoveMessages();
			}
			int[] bestAiMove = bestAiMove();
			clicks(buttons[bestAiMove[0]][bestAiMove[1]], bestAiMove[0], bestAiMove[1]);
		}
	}

	private int[] bestAiMove() {
		int highScore = Integer.MIN_VALUE;
		int[] bestAiMove = { -1, -1 };

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board[i][j] == ' ') {
					board[i][j] = 'O';
					int score = minimax(false);
					board[i][j] = ' ';

					if (score > highScore) {
						highScore = score;
						bestAiMove[0] = i;
						bestAiMove[1] = j;
					}
				}
			}
		}

		return bestAiMove;
	}

	private int minimax(boolean maximize) {
		if (checkWin('O')) {
			return 1;
		} else if (checkWin('X')) {
			return -1;
		} else if (isFull()) {
			return 0;
		}

		int highScore;
		if (maximize) {
			highScore = Integer.MIN_VALUE;
		} else {
			highScore = Integer.MAX_VALUE;
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board[i][j] == ' ') {
					if (maximize) {
						board[i][j] = 'O';
					} else {
						board[i][j] = 'X';
					}

					int score = minimax(!maximize);
					board[i][j] = ' ';

					if (maximize) {
						highScore = Math.max(score, highScore);
					} else {
						highScore = Math.min(score, highScore);
					}
				}
			}
		}

		return highScore;
	}

	private boolean checkWin(char player) {
		for (int i = 0; i < 3; i++) {
			if ((board[i][0] == player && board[i][1] == player && board[i][2] == player)
					|| (board[0][i] == player && board[1][i] == player && board[2][i] == player)) {
				return true;
			}
		}

		if ((board[0][0] == player && board[1][1] == player && board[2][2] == player)
				|| (board[0][2] == player && board[1][1] == player && board[2][0] == player)) {
			return true;
		}

		return false;
	}

	private void clearBoard() {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				board[i][j] = ' ';
			}
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				buttons[i][j].setText("");
			}
		}
	}

	private void updateTurnLabel() {
		turnLabel.setText("Current Turn: " + getPlayerTurn());
	}

	private String getPlayerTurn() {
		if (currentPlayer == 'X') {
			return player1 + " (X)";
		} else {
			return player2 + " (O)";
		}
	}

	private void showWinnerAlert(String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Game Over");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
