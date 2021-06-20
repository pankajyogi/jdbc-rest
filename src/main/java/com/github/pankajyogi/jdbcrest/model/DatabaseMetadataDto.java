package com.github.pankajyogi.jdbcrest.model;

import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Data;

@Data
public class DatabaseMetadataDto {

  private String productName;
  private String productVersion;
  private String driverName;
  private String driverVersion;
  private Set<TableMetadataDto> tables = new LinkedHashSet<>();

  public void addTableMetadata(TableMetadataDto tableMetadataDto) {
    tables.add(tableMetadataDto);
  }
}
