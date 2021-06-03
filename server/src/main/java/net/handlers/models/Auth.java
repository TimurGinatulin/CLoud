package net.handlers.models;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Auth {
    private final String userName;
    private final String password;
}