package kales

import kales.activemodel.MaybeRecordId
import kales.activemodel.RecordId
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class MaybeRecordIdColumnMapper : ColumnMapper<MaybeRecordId> {
  override fun map(resultSet: ResultSet, columnNumber: Int, ctx: StatementContext): MaybeRecordId {
    return RecordId(resultSet.getInt(columnNumber))
  }
}