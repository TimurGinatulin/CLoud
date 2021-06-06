package utils.FileSystemReader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileSystemUtils {

    public String readAsString(Path path, String filename) {
        StringBuilder newPath = new StringBuilder(path.toString());
        StringBuilder sb = new StringBuilder("cat ");
        newPath.append("/").append(filename);
        if (new File(newPath.toString()).isFile()) {
            try {
                sb.append(Files.readString(Path.of(newPath.toString())));
                return sb.toString();
            } catch (IOException e) {
                System.err.println("Read paths down");
                return null;
            }
        } else return "NoN";
    }

    public boolean createFile(Path path, String fileName) {
        String filePath = path.toString() + "/" + fileName;
        File file = new File(filePath);
        if (file.exists()) {
            return false;
        } else {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }

    public void write(Path path, String file, String data) {
        try {
            Files.write(Path.of(path.toString() + "/" + file),
                    data.getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.WRITE);
        } catch (IOException e) {
            System.err.println("Writing lost");
        }
    }

    public String getAllFilesAtDirToString(File curDir) {
        File[] filesList = curDir.listFiles();
        StringBuilder filesLists = new StringBuilder("ls .. ");
        for (File f : filesList != null ? filesList : new File[0]) {
            if (f.isDirectory())
                filesLists.append(f.getName()).append(" ");
            if (f.isFile()) {
                filesLists.append(f.getName()).append(" ");
            }
        }
        return filesLists.toString();
    }

    public Path cd(String path, String currPath) {
        if (path.startsWith("/") || path.startsWith("C:\\"))
            return Paths.get(path);
        if (path.trim().equals("~"))
            return Paths.get(System.getProperty("user.home"));
        else {
            return Paths.get(currPath + "/" + path);
        }
    }

    public boolean createDir(Path path, String dirName) {
        File file = new File(path.toString() + "/" + dirName);
        if (file.exists())
            return false;
        else
            return file.mkdir();
    }

    public boolean rm(String path, String filename) {
        File file = new File(path + "/" + filename);
        if (file.exists())
            return file.delete();
        else
            return false;
    }
}