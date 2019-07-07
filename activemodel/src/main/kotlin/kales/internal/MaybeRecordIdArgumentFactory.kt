package kales.internal

import kales.activemodel.MaybeRecordId
import kales.activemodel.RecordId
import org.jdbi.v3.core.argument.AbstractArgumentFactory
import org.jdbi.v3.core.argument.Argument
import org.jdbi.v3.core.config.ConfigRegistry
import java.sql.Types

class MaybeRecordIdArgumentFactory : AbstractArgumentFactory<MaybeRecordId>(Types.INTEGER) {
  override fun build(maybeRecordId: MaybeRecordId, config: ConfigRegistry): Argument {
    return Argument { position, statement, _ ->
      val value = when (maybeRecordId) {
        is RecordId -> maybeRecordId.value
        else -> -1
      }
      statement.setInt(position, value)
    }
  }
}
