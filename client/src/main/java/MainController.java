
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import net.Message;
import nettyNetwork.NettyNetwork;
import nettyNetwork.handlers.Callback;
import utils.FileSystemReader.FileSystemUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainController implements Initializable {
    public Button createFile;
    public TextField createFileField;
    private String editFile;
    public Button receiveTo;
    public Button remove;
    public Button exit;
    public Button mkDir;
    public TextField newDirName;
    public Button closeInfo;
    public Button editInfoButton;
    public TextArea info;
    private final Image updateImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("img/r.png")));
    private final Image sendImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("img/s.png")));
    private final Image receiveImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("img/rec.png")));
    private final Image removeImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("img/rm.png")));
    private final Image exitImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("img/ex.png")));
    private final Image mkDirImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("img/mkdir.png")));
    private final Image createImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("img/create.png")));

    FileSystemUtils fileSystemUtils = new FileSystemUtils();
    public ListView<String> myFiles;
    public ListView<String> cloudFiles;
    public Button sendTo;
    public TextField usernameInput;
    public PasswordField passwordInput;
    public Button sign;
    public Button login;
    public Button update;
    private NettyNetwork net;
    private final Callback callback = new Callback() {
        @Override
        public void processMessage(Message message) {
            User.setCurrentPath(message.getCurrentPath());
            if (message.getContent().contains("User founded status 200")) {
                User.setId(message.getIdUser());
                User.setUsername(message.getReceiver());
                User.setCurrentPath(message.getCurrentPath());
                gotToMain();
                updateLocalFileList();
                sendCommand("ls");
            }

            if (message.getContent().contains("ls")) {
                User.setCurrentPath(message.getCurrentPath());
                updateCloudFiles(message);
            }
            if (message.getContent().contains("OK")) {
                sendCommand("ls");
            }
            if (message.getContent().contains("sget")) {
                String[] msgArr = message.getContent().split("\"");
                String[] fileDirIn = msgArr[msgArr.length - 1].split("/");
                String fileIn = fileDirIn[fileDirIn.length - 1];
                try {
                    FileOutputStream fos = new
                            FileOutputStream(User.getLocalPath() + "/" + fileIn);
                    fos.write(message.getData());
                    updateLocalFileList();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (message.getContent().contains("cat")) {
                editFile = cloudFiles.getSelectionModel().getSelectedItem();
                info.setVisible(true);
                closeInfo.setVisible(true);
                editInfoButton.setVisible(true);
                info.setEditable(true);
                createFile.setVisible(false);
                info.setText(message.getContent().split(" ")[1]);
            }
        }
    };

    private void updateCloudFiles(Message message) {
        String[] fileListArr = message.getContent().split(" ");
        List<String> fileList = new LinkedList<>(Arrays.asList(fileListArr)
                .subList(1, fileListArr.length));
        Platform.runLater(() -> {
            cloudFiles.getItems().clear();
            cloudFiles.getItems().addAll(fileList);
        });
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        net = NettyNetwork.getInstance();
        net.setCallback(callback);

        update.setGraphic(new ImageView(updateImg));
        sendTo.setGraphic(new ImageView(sendImg));
        receiveTo.setGraphic(new ImageView(receiveImg));
        remove.setGraphic(new ImageView(removeImg));
        exit.setGraphic(new ImageView(exitImg));
        mkDir.setGraphic(new ImageView(mkDirImg));
        createFile.setGraphic(new ImageView(createImg));

        User.getInstance();
        User.setLocalPath("client/myCloudFiles");
        Thread network = new Thread(this.net);
        network.start();
        goToLogin();
    }

    private void sendCommand(String command) {
        Message message = Message.builder()
                .idUser(User.getId())
                .author(User.getUsername())
                .currentPath(User.getCurrentPath())
                .receiver("Server")
                .sentAt(System.currentTimeMillis())
                .content(command)
                .build();
        net.writeMassage(message);
    }

    public void sendTo() {
        String fileName = myFiles.getSelectionModel().getSelectedItem();
        Message message = ClientMessageDecoder.remoteDecodeAndRun("sget \"" + fileName + "\" > \"" + fileName + "\"");
        net.writeMassage(message);
    }

    public void update() {
        sendCommand("ls");
    }

    public void login() {
        String messageContent = usernameInput.getText() + " " + passwordInput.getText();
        Message message = ClientMessageDecoder.remoteDecodeAndRun(messageContent);
        message.setAuthor(User.getUsername());
        message.setIdUser(User.getId());
        message.setReceiver("Server");
        message.setCurrentPath(User.getCurrentPath());
        net.writeMassage(message);
    }

    public void sign() {
        App.setScene(Window.SIGN);
    }

    private void gotToMain() {
        myFiles.setVisible(true);
        cloudFiles.setVisible(true);
        sendTo.setVisible(true);
        update.setVisible(true);
        usernameInput.setVisible(false);
        passwordInput.setVisible(false);
        sign.setVisible(false);
        login.setVisible(false);
        receiveTo.setVisible(true);
        remove.setVisible(true);
        exit.setVisible(true);
        newDirName.setVisible(false);
        mkDir.setVisible(true);
        closeInfo.setVisible(false);
        editInfoButton.setVisible(false);
        info.setVisible(false);
        createFile.setVisible(true);
        createFileField.setVisible(false);
    }

    private void goToLogin() {
        mkDir.setVisible(false);
        myFiles.setVisible(false);
        cloudFiles.setVisible(false);
        sendTo.setVisible(false);
        update.setVisible(false);
        receiveTo.setVisible(false);
        remove.setVisible(false);
        exit.setVisible(false);
        usernameInput.setVisible(true);
        passwordInput.setVisible(true);
        newDirName.setVisible(false);
        sign.setVisible(true);
        login.setVisible(true);
        closeInfo.setVisible(false);
        editInfoButton.setVisible(false);
        info.setVisible(false);
        createFile.setVisible(false);
        createFileField.setVisible(false);
    }

    public void receiveTo() {
        sendCommand("sget \"" + cloudFiles.getSelectionModel().getSelectedItem() +
                "\" < \"" + cloudFiles.getSelectionModel().getSelectedItem() + "\"");
    }

    public void remove() {
        String fileName = cloudFiles.getSelectionModel().getSelectedItem();
        fileSystemUtils.rm(User.getCurrentPath(), fileName);
    }

    private void updateLocalFileList() {
        Platform.runLater(() -> {
            myFiles.getItems().clear();
            myFiles.getItems().addAll(fileSystemUtils.getAllFilesAtDirToList(
                    new File(User.getLocalPath())));
        });

    }

    public void exit() {
        System.exit(0);
    }

    public void mkDir() {
        newDirName.setVisible(true);
    }

    public void createDir() {
        sendCommand("make dir " + newDirName.getText());
        newDirName.setVisible(false);
    }

    public void cloudMouseAction(MouseEvent mouseEvent) {
        String file = cloudFiles.getSelectionModel().getSelectedItem();
        if (mouseEvent.getClickCount() == 2) {
            sendCommand("cd " + file);
        }
        if (file.contains(".") && mouseEvent.getClickCount() == 2) {
            sendCommand("cat " + file);
        }
    }

    public void editInfo() {
        sendCommand("edit " + editFile + " " + info.getText());
        closeInfo.setVisible(false);
        editInfoButton.setVisible(false);
        info.setVisible(false);
    }

    public void closeInfo() {
        closeInfo.setVisible(false);
        editInfoButton.setVisible(false);
        info.setVisible(false);
    }

    public void createFile() {
        createFileField.setVisible(true);
    }

    public void createFileKey() {
        sendCommand("touch " + createFileField.getText());
        createFileField.setVisible(false);
    }
}
