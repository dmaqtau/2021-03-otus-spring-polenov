package ru.otus.spring.security;

import java.util.stream.Stream;

import lombok.Getter;

@Getter
public enum UserRole {
    ADMIN("ADMIN"),
    USER("USER"),
    MANAGER("MANAGER");

    String name;

    UserRole(String name){
        this.name = name;
    }

    public static UserRole getByName(String name){
        return Stream.of(UserRole.values()).filter(r -> r.getName().equals(name)).findAny().orElse(null);
    }
}
