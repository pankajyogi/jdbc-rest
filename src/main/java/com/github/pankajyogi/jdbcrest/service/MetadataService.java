package com.github.pankajyogi.jdbcrest.service;

import static com.github.pankajyogi.jdbcrest.service.MetadataConst.*;

import com.github.pankajyogi.jdbcrest.model.ColumnMetadataDto;
import com.github.pankajyogi.jdbcrest.model.DatabaseMetadataDto;
import com.github.pankajyogi.jdbcrest.model.TableMetadataDto;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MetadataService {

  private DataSource dataSource;
  private String[] tableTypes;

  public MetadataService(DataSource dataSource, String[] tableTypes) {
    this.dataSource = dataSource;
    this.tableTypes = tableTypes;
  }

  public DatabaseMetadataDto getDatabaseMetadata(String catalog, String schemaPattern)
      throws SQLException {
    DatabaseMetadataDto databaseMetadataDto = null;

    try (Connection connection = dataSource.getConnection()) {
      var metadata = connection.getMetaData();
      databaseMetadataDto = createDatabaseMetadata(metadata);
      var tableResultSet = metadata.getTables(catalog, schemaPattern, null, tableTypes);
      var tableMetadataDtos = createTableSchemas(tableResultSet);
      tableMetadataDtos.forEach(databaseMetadataDto::addTableMetadata);

      enrichColumnMetadata(databaseMetadataDto.getTables(), metadata);
    }

    return databaseMetadataDto;
  }

  private void enrichColumnMetadata(Set<TableMetadataDto> tableMetadataDtos,
      DatabaseMetaData metadata)
      throws SQLException {
    for (var tableMetadataDto : tableMetadataDtos) {
      var tcatalog = tableMetadataDto.getCatalog();
      var tschema = tableMetadataDto.getSchema();
      var tname = tableMetadataDto.getName();
      var columnResultSet = metadata.getColumns(tcatalog, tschema, tname, null);
      var columnMetadataDtos = createColumnSchemas(columnResultSet);
      columnMetadataDtos.forEach(tableMetadataDto::addColumnMetadata);
    }
  }

  public TableMetadataDto getTableMetadata(String catalog, String schemaName, String tableName)
      throws SQLException {
    TableMetadataDto tableMetadataDto = null;

    try (Connection connection = dataSource.getConnection()) {
      var metadata = connection.getMetaData();
      var tableResultSet = metadata.getTables(catalog, schemaName, null, tableTypes);
      var tableMetadataDtos = createTableSchemas(tableResultSet);

      if (tableMetadataDtos.size() != 1) {
        throw new SQLException("Not correct");
      }

      tableMetadataDto = tableMetadataDtos.get(0);
      enrichColumnMetadata(Set.of(tableMetadataDto), metadata);
    }

    return tableMetadataDto;
  }

  private DatabaseMetadataDto createDatabaseMetadata(DatabaseMetaData metadata)
      throws SQLException {
    assert metadata != null;
    var dbMetadata = new DatabaseMetadataDto();
    dbMetadata.setProductName(metadata.getDatabaseProductName());
    dbMetadata.setProductVersion(metadata.getDatabaseProductVersion());
    dbMetadata.setDriverName(metadata.getDriverName());
    dbMetadata.setDriverVersion(metadata.getDriverVersion());
    return dbMetadata;
  }

  private List<TableMetadataDto> createTableSchemas(ResultSet tableResultSet)
      throws SQLException {
    assert tableResultSet != null;
    var tableSchemas = new ArrayList<TableMetadataDto>();

    while (tableResultSet.next()) {
      var tableMetadata = new TableMetadataDto();
      tableMetadata.setName(tableResultSet.getString(TABLE_NAME));
      tableMetadata.setType(tableResultSet.getString(TABLE_TYPE));
      tableMetadata.setCatalog(tableResultSet.getString(TABLE_CATALOG));
      tableMetadata.setSchema(tableResultSet.getString(TABLE_SCHEMA));
    }

    return tableSchemas;
  }

  private List<ColumnMetadataDto> createColumnSchemas(ResultSet columnResultSet)
      throws SQLException {
    assert columnResultSet != null;
    var columnSchemas = new ArrayList<ColumnMetadataDto>();

    while (columnResultSet.next()) {
      var columnMetadata = new ColumnMetadataDto();
      columnMetadata.setName(columnResultSet.getString(COLUMN_NAME));
      columnMetadata.setDataType(columnResultSet.getString(DATA_TYPE));
      columnMetadata.setNullable(columnResultSet.getString(IS_NULLABLE));
      columnMetadata.setAutoIncrement(columnResultSet.getString(IS_AUTO_INCREMENT));
      columnMetadata.setGeneratedColumn(columnResultSet.getString(IS_GENERATED_COLUMN));
    }

    return columnSchemas;
  }
}
