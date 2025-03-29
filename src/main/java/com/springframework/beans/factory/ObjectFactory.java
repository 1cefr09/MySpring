package com.springframework.beans.factory;

@FunctionalInterface
public interface ObjectFactory<T> {
    T getObject() throws RuntimeException;
}