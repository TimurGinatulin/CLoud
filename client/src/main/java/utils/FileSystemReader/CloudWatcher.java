package utils.FileSystemReader;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class CloudWatcher {
    private static CloudWatcher instance;

    private CloudWatcher() throws IOException {
        Path path = Paths.get(System.getProperty("User.home"));
        WatchService watchService = FileSystems.getDefault().newWatchService();
        new Thread(() -> {
            try {
                while (true) {
                    WatchKey key = watchService.take();
                    List<WatchEvent<?>> events = key.pollEvents();
                    for (WatchEvent event : events) {
                        System.out.println(event.kind() + " " + event.context());
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        path.register(watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);
    }

    public static CloudWatcher getInstance() throws IOException {
        if (instance == null)
            instance = new CloudWatcher();
        return instance;
    }
}
