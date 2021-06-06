import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import net.Message;
import nettyNetwork.NettyNetwork;

public class ControllerSignWindow {
    public TextField username;
    public PasswordField password;

    public void sendSign() {
        Message signMessage = Message.builder()
                .content("Sign " + username.getText() + " " + password.getText())
                .sentAt(System.currentTimeMillis())
                .receiver("Server")
                .build();
        NettyNetwork.getInstance().writeMassage(signMessage);
        App.setScene(Window.MAIN);
    }
}
