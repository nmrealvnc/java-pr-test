package com.realvnc.messageservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.stream.Collectors;

@Data
@Entity
@NoArgsConstructor
public class TokenData {
    @Id
    String token;
    String userId;
    String permissions;

    public EnumSet<Permission> getPermissions() {
        return Arrays.stream(permissions.split(","))
                .map(Permission::valueOf)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Permission.class)));
    }

    public void setPermissions(EnumSet<Permission> permissions) {
        this.permissions =
                permissions.stream().map(Permission::name).collect(Collectors.joining(","));
    }

    public TokenData(String token, String userId, EnumSet<Permission> permissions) {
        this.token = token;
        this.userId = userId;
        setPermissions(permissions);
    }
}
