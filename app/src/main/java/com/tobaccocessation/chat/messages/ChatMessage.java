package com.tobaccocessation.chat.messages;

public interface ChatMessage {

  String getMessageBody();

  String getAuthor();

  String getDateCreated();
}
