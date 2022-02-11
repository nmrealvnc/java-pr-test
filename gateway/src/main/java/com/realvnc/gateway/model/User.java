package com.realvnc.gateway.model;

import lombok.Value;

import java.util.EnumSet;

@Value
public class User {

    String id;
    String email;
    String firstName;
    String lastName;
    EnumSet<Permission> permissions;

}
