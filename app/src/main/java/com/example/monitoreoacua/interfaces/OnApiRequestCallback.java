package com.example.monitoreoacua.interfaces;

public interface OnApiRequestCallback<T, E> {
    void onSuccess(T data);
    void onFail(E error);
}