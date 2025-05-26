package com.woochang.highticket.domain.user;

public enum Role {

    USER,

    ADMIN;

    public String getAuthority() {
        return "ROLE_" + name();
    }
}
