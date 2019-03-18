package com.tobaccocessation.chat.listeners;

public interface TaskCompletionListener<T, U> {

  void onSuccess(T t);

  void onError(U u);
}
