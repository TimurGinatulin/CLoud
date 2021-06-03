package controllers;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import decoder.ClientMessageDecoder;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import net.Message;
import nettyNetwork.NettyNetwork;
import nettyNetwork.handlers.Callback;

public class ChatController implements Initializable {
    public Text serverMsg;
    private String username;
    private int id;
    private String currentPath;
    public static String localPath;

    private NettyNetwork net;
    public TextField inputUsername;
    public PasswordField inputPassword;
    public TextField input;
    public ListView<String> listView;
    private final Callback callback = new Callback() {
        @Override
        public void processMessage(Message message) {
            try {
                currentPath = message.getCurrentPath();
                switch (message.getContent()) {
                    case "User founded status 200": {
                        inputPassword.setEditable(false);
                        inputPassword.setVisible(false);
                        inputUsername.setEditable(false);
                        inputUsername.setVisible(false);
                        serverMsg.setVisible(false);
                        input.setVisible(true);
                        listView.setVisible(true);
                        id = message.getIdUser();
                        username = message.getReceiver();

                        Platform.runLater(() -> listView
                                .getItems().add(message
                                        .getAuthor() + ": " + message.getContent()));
                        break;
                    }
                    case "Uncorrected Username or(and) password": {
                        serverMsg.setText(message.getContent());
                        break;
                    }
                    case "sget": {
                        String[] msgArr = message.getContent().split("\"");
                        String[] fileDirIn = msgArr[msgArr.length - 1].split("/");
                        String fileIn = fileDirIn[fileDirIn.length - 1];

                        FileOutputStream fos = new
                                FileOutputStream(localPath + "/" + fileIn);
                        fos.write(message.getData());
                        message.setContent("Ok");
                        break;
                    }
                    case "user disable": {
                        System.exit(0);
                    }
                    default: {
                        serverMsg.setText(message.getContent());
                        Platform.runLater(() -> listView
                                .getItems().add(message
                                        .getAuthor() + ": " + message.getContent()));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };


    public void sendMessage() {
        if (ClientMessageDecoder.isLocal(input.getText())) {
            Platform.runLater(() -> {
                listView.getItems().clear();
                listView.getItems().add(ClientMessageDecoder.localDecodeAndRun(input.getText()));
                input.clear();
            });
        } else {
            Message message = ClientMessageDecoder.remoteDecodeAndRun(input.getText());
            message.setAuthor(username);
            message.setIdUser(id);
            message.setReceiver("Server");
            message.setCurrentPath(currentPath);
            net.writeMassage(message);
            input.clear();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serverMsg.setVisible(true);
        listView.setVisible(false);
        input.setVisible(false);
        localPath = "client/myCloudFiles";
        net = NettyNetwork.getInstance();
        net.setCallback(callback);
        Thread network = new Thread(this.net);
        network.setDaemon(true);
        network.start();
    }

    public void entryPassword() {
        String messageContent = inputUsername.getText() + " " + inputPassword.getText();
        Message message = ClientMessageDecoder.remoteDecodeAndRun(messageContent);
        message.setAuthor(username);
        message.setIdUser(id);
        message.setReceiver("Server");
        message.setCurrentPath(currentPath);
        net.writeMassage(message);
    }
}
