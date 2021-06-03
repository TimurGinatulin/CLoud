package utils.Decoder;

import utils.FileSystemReader.FileSystemUtils;
import net.Message;
import net.UserContainer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MessageDecoder {
    public static Message decode(Message message) {
        FileSystemUtils fileSystemUtils = new FileSystemUtils();
        String[] contentArr = message.getContent().split(" ");
        message.setReceiver(message.getAuthor());
        message.setAuthor("Server");
        switch (contentArr[0]) {
            case "help": {
                message.setContent("Welcome to home server v.Alpha\n Commands:" +
                        "\nhelp->help menu." +
                        "\n[args] ls->show files and directory." +
                        "\n[args] cd [path]-> change directory to path." +
                        "\n[args] cat [path]-> read file path." +
                        "\n[args] touch [path]-> create file path." +
                        "\n[args] edit [path]-> edit file path." +
                        "\n[args] make dir [path]-> create directory path." +
                        "\n[args] rm [path]-> remove file/directory" +
                        "\nsget [file1] [<>] [file2]");
                break;
            }
            case "ls": {
                message.setContent(fileSystemUtils.getAllFilesAtDirToString
                        (new File(String.valueOf(message.getCurrentPath()))));
                break;
            }
            case "cd": {
                if (contentArr.length > 1) {
                    Path newPath;
                    if (contentArr[1].equals("~"))
                        newPath = Path.of("server/cloudFilePool/" + message.getReceiver());
                    else
                        newPath = fileSystemUtils.cd(contentArr[1], message.getCurrentPath());
                    message.setCurrentPath(newPath.toString());
                    message.setContent(fileSystemUtils.getAllFilesAtDirToString(newPath.toFile()));
                }
                break;
            }
            case "cat": {
                if (contentArr.length > 1) {
                    String readFile = fileSystemUtils.readAsString(Path.of(message.getCurrentPath())
                            , contentArr[1]);
                    if (!readFile.equals("NoN")) {
                        message.setContent(readFile);
                    } else
                        message.setContent("File not Found");
                }
                break;
            }
            case "touch": {
                if (contentArr.length > 1) {
                    if (fileSystemUtils.createFile(Path.of(message.getCurrentPath()), contentArr[1]))
                        message.setContent("OK");
                    else
                        message.setContent("Cancel");
                }
                break;
            }
            case "edit": {
                if (contentArr.length > 2) {
                    StringBuilder userMsg = new StringBuilder();
                    for (int i = 2; i < contentArr.length; i++) {
                        userMsg.append(contentArr[i]).append(" ");
                    }
                    fileSystemUtils.write(Path.of(message.getCurrentPath())
                            , contentArr[1]
                            , userMsg.toString());
                    message.setContent("OK");
                    break;
                }
            }
            case "make": {
                if (contentArr.length > 2) {
                    if (contentArr[1].equals("dir")) {
                        if (fileSystemUtils.createDir(Path.of(message.getCurrentPath()), contentArr[2]))
                            message.setContent("OK");
                        else
                            message.setContent("Failed");
                    }
                    break;
                }
            }
            case "rm": {
                if (contentArr.length > 1)
                    if (fileSystemUtils.rm(message.getCurrentPath(), contentArr[1]))
                        message.setContent("Ok");
                    else
                        message.setContent("Cancel");
                break;
            }
            case "sget": {
                if (message.getContent().contains("<")) {
                    String[] fileArr = message.getContent().split("\"");
                    try {
                        byte[] data = Files.readAllBytes(Path.of(message.getCurrentPath() + "/" + fileArr[1]));
                        message.setData(data);
                        break;
                    } catch (IOException e) {
                        message.setContent("Error.");
                        e.printStackTrace();
                    }
                } else {
                    String[] msgArr = message.getContent().split("\"");
                    String[] fileDirIn = msgArr[msgArr.length - 1].split("/");
                    String fileIn = fileDirIn[fileDirIn.length - 1];
                    try {
                        FileOutputStream fos = new
                                FileOutputStream(message.getCurrentPath() + "/" + fileIn);
                        fos.write(message.getData());
                        message.setContent("Ok");
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                        message.setContent("Cancel");
                    }
                }
            }
            case "exit": {
                UserContainer.removeUserId(message.getIdUser());
                message.setContent("user disable");
                break;
            }
            default: {
                message.setContent("Uncorrected command. Print \"help\"");
                break;
            }

        }
        return message;
    }
}
