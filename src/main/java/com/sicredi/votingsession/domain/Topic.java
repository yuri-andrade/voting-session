package com.sicredi.votingsession.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Topic {
  
  @Id
  private String id;
  private final String name;
}
