package com.github.pankajyogi.jdbcrest.model;

import lombok.Data;

@Data
public class ColumnMetadataDto {

  private String name;
  private String dataType;
  private String nullable;
  private String autoIncrement;
  private String generatedColumn;
}
