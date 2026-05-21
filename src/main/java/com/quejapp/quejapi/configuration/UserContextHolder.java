package com.quejapp.quejapi.configuration;

public class UserContextHolder {

    private static final ThreadLocal<String> currentUser =
            new ThreadLocal<>();

    public static void set(String email) {
        currentUser.set(email);
    }

    public static String get() {
        return currentUser.get();
    }

    public static void clear() {
        currentUser.remove();
    }
}