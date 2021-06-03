package decoder;

import utils.FileSystemReader.FileSystemUtils;
import controllers.ChatController;
import net.Message;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class ClientMessageDecoder {
    public static boolean isLocal(String message) {
        return message.trim().split(" ")[0].toLowerCase(Locale.ROOT).equals("l");
    }

    public static String localDecodeAndRun(String message) {
        FileSystemUtils fileSystemUtils = new FileSystemUtils();
        String[] contentArr = message.split(" ");
        String answer = null;
        switch (contentArr[1]) {
            case "ls": {
                answer = fileSystemUtils.getAllFilesAtDirToString
                        (new File(String.valueOf(ChatController.localPath)));
                break;
            }
            case "cd": {
                if (contentArr.length > 2) {
                    Path newPath;
                    if (contentArr[2].equals("~"))
                        newPath = Path.of("client/myCloudFiles");
                    else
                        newPath = Path.of((ChatController.localPath + "/" + contentArr[2]));
                    ChatController.localPath = (newPath.toString());
                    answer = fileSystemUtils.getAllFilesAtDirToString(newPath.toFile());
                } else answer = "Cancel";
                break;
            }
            case "cat": {
                if (contentArr.length > 2) {
                    String readFile = fileSystemUtils.readAsString(Path.of(ChatController.localPath)
                            , contentArr[2]);
                    if (!readFile.equals("NoN")) {
                        answer = readFile;
                    } else
                        answer = "File not Found";
                }
                break;
            }
            case "touch": {
                if (contentArr.length > 2) {
                    if (fileSystemUtils.createFile(Path.of(ChatController.localPath), contentArr[2]))
                        answer = "OK";
                    else
                        answer = "Cancel";
                }
                break;
            }
            case "edit": {
                if (contentArr.length > 3) {
                    StringBuilder userMsg = new StringBuilder();
                    for (int i = 2; i < contentArr.length; i++) {
                        userMsg.append(contentArr[i]).append(" ");
                    }
                    fileSystemUtils.write(Path.of(ChatController.localPath)
                            , contentArr[2]
                            , userMsg.toString());
                    answer = "OK";
                    break;
                } else answer = "Cancel";
                break;
            }
            case "make": {
                if (contentArr.length > 3) {
                    if (contentArr[2].equals("dir")) {
                        if (fileSystemUtils.createDir(Path.of(ChatController.localPath), contentArr[3]))
                            return "OK";
                        else
                            return "Failed";
                    }
                    break;
                }
            }
            case "rm": {
                if (contentArr.length > 2)
                    if (fileSystemUtils.rm(ChatController.localPath, contentArr[2]))
                        answer = "Ok";
                    else
                        answer = "Cancel";
                break;
            }
            default: {
                answer = "Uncorrected command. Print \"help\"";
                break;
            }
        }
        return answer;
    }

    public static Message remoteDecodeAndRun(String message) {
        Message answer = null;
        if (message.contains("sget") && message.contains(">")) {
            try {
                if (message.contains(">")) {
                    String[] fileNameDir = message.split("\"");
                    String fileName = fileNameDir[1];
                    answer = Message.builder()
                            .content(message)
                            .data(Files.readAllBytes(Path.of((
                                    ChatController.localPath + "/" + fileName))))
                            .sentAt(System.currentTimeMillis())
                            .build();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            answer = Message.builder()
                    .content(message)
                    .sentAt(System.currentTimeMillis())
                    .build();
        }
        return answer;
    }
}
