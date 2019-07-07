package kales

import kales.internal.MaybeRecordIdArgumentFactory
import org.jdbi.v3.core.Jdbi

object JdbiFactory {
  fun fromConnectionString(connString: String): Jdbi {
    return Jdbi.create(connString)
      .registerColumnMapper(HasManyAssociationColumnMapperFactory())
      .registerColumnMapper(BelongsToAssociationColumnMapperFactory())
      .registerColumnMapper(MaybeRecordIdColumnMapper())
      .registerArgument(MaybeRecordIdArgumentFactory())
      .installPlugins()
  }
}