public class User {
    private static User instance;
    private static int id;
    private static String currentPath;
    private static String localPath;
    private static String username;

    public User() {
    }

    public static User getInstance() {
        if (instance == null)
            instance = new User();
        return instance;
    }

    public static int getId() {
        return id;
    }

    public static String getCurrentPath() {
        return currentPath;
    }

    public static String getLocalPath() {
        return localPath;
    }

    public static void setId(int id) {
        User.id = id;
    }

    public static void setCurrentPath(String currentPath) {
        User.currentPath = currentPath;
    }

    public static void setLocalPath(String localPath) {
        User.localPath = localPath;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        User.username = username;
    }
}
