package com.sicredi.votingsession.rabbitmq.configuration;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueueConfiguration {
  
  @Value("${vote-session-exchange}")
  private String voteSessionExchange;
  @Value("${vote-session-dlq-exchange}")
  private String voteSessionDlqExchange;
  
  @Bean
  public Queue voteSessionQueue() {
    return QueueBuilder.durable("vote-session-queue")
        .deadLetterRoutingKey("session-expired")
        .deadLetterExchange(voteSessionDlqExchange)
        .build();
  }
  
  @Bean
  public Queue voteSessionDlq() {
    return QueueBuilder.durable("vote-session-dlq")
        .build();
  }
  
  @Bean
  Queue sessionResultQueue() {
    return QueueBuilder.durable("session-result-queue").build();
  }
  
  @Bean
  public Exchange voteSessionExchange() {
    return ExchangeBuilder.directExchange(voteSessionExchange).durable(true).build();
  }
  
  @Bean
  public Exchange voteSessionDeadLetterExchange() {
    return ExchangeBuilder.directExchange(voteSessionDlqExchange).durable(true).build();
  }
  
  @Bean
  public Binding bindingDlq() {
    return BindingBuilder.bind(voteSessionDlq()).to(voteSessionDeadLetterExchange())
        .with("session-expired")
        .noargs();
  }
  
  @Bean
  Binding bindingResultSession() {
    return BindingBuilder.bind(sessionResultQueue()).to(voteSessionExchange())
        .with("session-result").noargs();
  }
  
  @Bean
  public Binding binding() {
    return BindingBuilder.bind(voteSessionQueue()).to(voteSessionExchange())
        .with("session-started")
        .noargs();
  }
  
  @Bean
  public MessageConverter jsonMessageConverter() {
    return new Jackson2JsonMessageConverter();
  }
}
