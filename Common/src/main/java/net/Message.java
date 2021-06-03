package net;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class Message implements Serializable {
    private int idUser;
    private String author;
    private String receiver;
    private String content;
    private String currentPath;
    private byte[] data;
    private long sentAt;

}
