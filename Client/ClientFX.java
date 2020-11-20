package Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ClientFX extends Application {
    private TextArea textArea;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String username;
    private ReceivingThread receivingThread;
    private BufferedReader bufferedReader;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = new Scene(createLayout(), 280, 350);
        stage.setOnCloseRequest(e -> closeClient());
        stage.setTitle("Chat Application");
        stage.setScene(scene);
        stage.show();

        startConnection("localHost", 777);
    }

    private VBox createLayout() {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10, 10, 10, 10));
        textArea = new TextArea();
        textArea.setPrefHeight(330);
        textArea.setPrefWidth(280);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        TextField textField = new TextField();
        textField.setPromptText("Type your messages here...");
        textField.setOnAction(e -> {
            try {
                sendMessage(textField.getText());
                textField.clear();
            } catch (SocketException e1) {
                textArea.appendText("Connection closed. Please leave.");
            }
            catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        layout.getChildren().addAll(textArea, textField);
        return layout;
    }

    private void startConnection(String ip, int port) throws IOException, ClassNotFoundException {
        socket = new Socket(ip, port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        boolean isUsernameFree = false;
        do {
            username = LogInBox.logIn();
            if (username.length() < 3) {
                AlertBox2.display();
                continue;
            }
            out.writeObject(username);
            isUsernameFree = (boolean) in.readObject();
            if (!isUsernameFree)
                AlertBox1.display();
        }
        while (!isUsernameFree);

        receivingThread = new ReceivingThread();
        receivingThread.setDaemon(true);
        receivingThread.start();
    }

    private void sendMessage(String message) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(username).append(": ").append(message);
        out.writeObject(sb.toString());
    }

    private void receiveMessage() throws IOException, ClassNotFoundException {
        // bufferedReader is used only to make sure that "in" is not blocked when the client is being closed
        while (!bufferedReader.ready()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }

        String message1 = (String) in.readObject();
        if (message1.equals("Server closed. Please leave."))
            closeClient();
        Platform.runLater(() -> {
            textArea.appendText(message1);
            textArea.appendText("\n");
        });
    }

    private class ReceivingThread extends Thread {
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    receiveMessage();
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void closeClient() {
        if (!socket.isClosed()) {
            receivingThread.interrupt();
            try {
                out.writeObject("exit");
                in.close();
                out.close();
                socket.close();
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
