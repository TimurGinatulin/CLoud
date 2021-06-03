package net;

import java.util.LinkedList;
import java.util.List;

public class UserContainer {
    private static final List<Integer> userIdList = new LinkedList<>();

    public static void addUserId(int id) {
        userIdList.add(id);
    }

    public static void removeUserId(int id) {
        userIdList.remove((Integer) id);
    }

    public static boolean containsId(int id) {
        return userIdList.contains(id);
    }
}
