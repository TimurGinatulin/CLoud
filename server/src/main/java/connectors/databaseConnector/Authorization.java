package connectors.databaseConnector;

import net.Message;

public class Authorization {

    public static Message authUser(String username, String password) {
        return DBConnector.getUserByUsernameAndPassword(username, password);
    }
    public static boolean signUser(String username, String password){
        return DBConnector.createUser(username,password);
    }
}
