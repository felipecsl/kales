package kales

import kales.activemodel.BelongsToAssociation
import org.jdbi.v3.core.config.ConfigRegistry
import org.jdbi.v3.core.generic.GenericTypes
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.mapper.ColumnMapperFactory
import java.lang.reflect.Type
import java.util.*

internal class BelongsToAssociationColumnMapperFactory : ColumnMapperFactory {
  override fun build(type: Type, config: ConfigRegistry): Optional<ColumnMapper<*>> {
    return if (GenericTypes.getErasedType(type) == BelongsToAssociation::class.java) {
      Optional.of(BelongsToAssociationColumnMapper(type))
    } else {
      Optional.empty()
    }
  }
}
