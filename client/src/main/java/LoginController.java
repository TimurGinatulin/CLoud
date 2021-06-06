import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import net.Message;
import nettyNetwork.NettyNetwork;
import nettyNetwork.handlers.Callback;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public Text serverMsg;
    public PasswordField inputPassword;
    public TextField inputUsername;
    private NettyNetwork net;
    private final Callback callback = new Callback() {
        @Override
        public void processMessage(Message message) {
            User.setCurrentPath(message.getCurrentPath());
            switch (message.getContent()) {
                case "User founded status 200": {
                    User.setId(message.getIdUser());
                    User.setUsername(message.getReceiver());
                    User.setCurrentPath(message.getCurrentPath());
                    Platform.runLater(() -> App.setScene(Window.MAIN));
                    break;
                }
                case "Uncorrected Username or(and) password":
                case "Please enter message in format:\n {username password}": {
                    Platform.runLater(() -> serverMsg.setText(message.getContent()));
                    break;
                }
            }

        }
    };

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User.getInstance();
        User.setLocalPath("client/myCloudFiles");

        Thread network = new Thread(this.net);
        network.setDaemon(true);
        network.start();
    }

    public void entryPassword() {
        String messageContent = inputUsername.getText() + " " + inputPassword.getText();
        Message message = ClientMessageDecoder.remoteDecodeAndRun(messageContent);
        message.setAuthor(User.getUsername());
        message.setIdUser(User.getId());
        message.setReceiver("Server");
        message.setCurrentPath(User.getCurrentPath());
        net.writeMassage(message);
    }

    public void openSignPage() {
        App.setScene(Window.SIGN);
    }
}
