package ru.otus.spring.security;

class SecurityConst {
    static final String SECRET = "db3cvF8mM0ROxDmbKFdPdeTfEoMyYyMfB1zhtUb0lvdBg9oNOnoi3GlrKDxDFBjW";
    static final long EXPIRATION_TIME = 900000; // 15 минут
    static final String TOKEN_PREFIX = "Bearer ";
    static final String HEADER_STRING = "Authorization";
    static final String ROLES_CLAIM = "roles";

    private SecurityConst(){
        // Private constructor
    }
}
