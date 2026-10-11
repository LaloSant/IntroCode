package com.introcode.entity;

public enum AppStatus {
    NONE(0),
    FILE_READ(1),
    ANLEXICO(2),
    ANSINTACTICO(3),
    ANSEMANTICO(4),
    PREPOSFIJA(5);

    private AppStatus(int value){
    }
}
