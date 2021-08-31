package ru.otus.spring.util;

import javax.persistence.AttributeConverter;

import ru.otus.spring.security.UserRole;

public class UserRoleConverter implements AttributeConverter<UserRole, String> {
    @Override
    public String convertToDatabaseColumn(UserRole attribute) {
        return attribute.getName();
    }

    @Override
    public UserRole convertToEntityAttribute(String dbData) {
        return UserRole.getByName(dbData);
    }
}
