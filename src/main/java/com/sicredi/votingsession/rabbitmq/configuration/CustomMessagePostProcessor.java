package com.sicredi.votingsession.rabbitmq.configuration;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;

public class CustomMessagePostProcessor implements MessagePostProcessor {
  
  private final Long ttl;
  
  public CustomMessagePostProcessor(final Long ttl) {
    this.ttl = ttl;
  }
  
  @Override
  public Message postProcessMessage(final Message message) throws AmqpException {
    message.getMessageProperties().setExpiration(ttl.toString());
    return message;
  }
}