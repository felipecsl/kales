package kales

import kales.activemodel.ModelCollectionAssociation
import org.jdbi.v3.core.config.ConfigRegistry
import org.jdbi.v3.core.generic.GenericTypes
import org.jdbi.v3.core.mapper.ColumnMapper
import org.jdbi.v3.core.mapper.ColumnMapperFactory
import java.lang.reflect.Type
import java.util.*

class ModelCollectionColumnMapperFactory : ColumnMapperFactory {
  override fun build(type: Type, config: ConfigRegistry): Optional<ColumnMapper<*>> {
    return if (GenericTypes.getErasedType(type) == ModelCollectionAssociation::class.java) {
      Optional.of(ModelCollectionColumnMapper(type))
    } else {
      Optional.empty()
    }
  }
}