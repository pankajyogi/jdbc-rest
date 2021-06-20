package com.github.pankajyogi.jdbcrest.model;

import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Data;

@Data
public class TableMetadataDto {

  private String catalog;
  private String schema;
  private String name;
  private String type;
  private Set<ColumnMetadataDto> columns = new LinkedHashSet<>();

  public void addColumnMetadata(ColumnMetadataDto columnMetadataDto) {
    columns.add(columnMetadataDto);
  }
}
