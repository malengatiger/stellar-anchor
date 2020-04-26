package com.anchor.api.data.transfer.sep10;

public class JWTToken {
    String token;

    public JWTToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
