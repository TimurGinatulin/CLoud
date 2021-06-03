package connectors.databaseConnector;

import net.Message;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import java.util.List;

public class DBConnector {
    private static JdbcTemplate template;

    private DBConnector() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriverClass(com.mysql.jdbc.Driver.class);
        dataSource.setUsername("guest");
        dataSource.setUrl("jdbc:mysql://localhost:3306/learning");
        dataSource.setPassword("Guest@001");
        template = new JdbcTemplate(dataSource);
        System.out.println("Connect");
    }

    public static void startDbConnection() {
        new DBConnector();
    }

    public static Message getUserById(int id) {
        List<Message> list = template.query(
                "select * from user where id = ?"
                , new Object[]{id}
                , (resultSet, i) -> Message.builder()
                        .idUser(resultSet.getInt("id"))
                        .author("Server")
                        .receiver(resultSet.getString("name"))
                        .content("Password changed. Status 200")
                        .build());
        return list.get(0);
    }

    public static Message getUserByUsernameAndPassword(String username, String password) {
        try {
            List<Message> list = template.query(
                    "select * from user where name = ? && password = ?"
                    , new Object[]{username, password}
                    , (resultSet, i) -> Message.builder()
                            .idUser(resultSet.getInt("id"))
                            .author("Server")
                            .receiver(resultSet.getString("name"))
                            .content("User founded status 200")
                            .currentPath("server/cloudFilePool/" + resultSet.getString("name"))
                            .build());
            return list.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean createUser(String username, String password) {
        try {
            template.update("insert into user (user.name,user.password)values(?,?)"
                    , username, password);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updatePassword(int id, String newPassword) {
        try {
            template.update("update user set password = ? where id = ?"
                    , newPassword, id);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
