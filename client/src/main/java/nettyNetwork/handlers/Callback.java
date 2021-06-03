package nettyNetwork.handlers;

import net.Message;

public interface Callback {
    void processMessage(Message message);
}
