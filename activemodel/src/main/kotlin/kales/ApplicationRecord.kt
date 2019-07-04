package kales

import kales.activemodel.use
import kales.internal.KApplicationRecordClass
import kales.internal.RecordQueryBuilder
import kales.migrations.KalesDatabaseConfig
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.result.ResultProducers.returningGeneratedKeys
import java.sql.SQLException

/**
 * Maps model classes to database records. Kales follows some conventions when dealing with models:
 * - All models are expected to have an `id` autoincrement primary key column
 * - Foreign key columns are mapped using `<model_name>_id` column naming format
 * - The table name is a pluralized lowercase model name, eg: `User` -> table is `users`. We don't
 *   do any fancy pluralization for now, just naively append `s` at the end, which means the table
 *   for `Hero` would be `heros`, awkwardly.
 */
interface ApplicationRecord {
  val id: Int

  companion object {
    val JDBI: Jdbi = JdbiFactory.fromConnectionString(dbConnectionString())

    private fun dbConnectionString(): String {
      val stream = ApplicationRecord::class.java.classLoader.getResourceAsStream("database.yml")
          ?: throw RuntimeException("Failed to load 'database.yml' file. " +
              "Please make sure it's in your 'resources' directory")
      return KalesDatabaseConfig.fromDatabaseYml(stream).toConnectionString()
    }

    /** Returns a list with all records in the table (potentially dangerous for big tables!) */
    inline fun <reified T : ApplicationRecord> allRecords(): List<T> {
      useJdbi {
        val queryBuilder = RecordQueryBuilder(it, KApplicationRecordClass(T::class))
        return queryBuilder.allRecords()
            .mapTo<T>()
            .list()
      }
    }

    /** Returns only the records matching the provided selection criteria */
    inline fun <reified T : ApplicationRecord> whereRecords(clause: Map<String, Any?>): List<T> {
      useJdbi {
        val queryBuilder = RecordQueryBuilder(it, KApplicationRecordClass(T::class))
        queryBuilder.where(clause).let { query ->
          return query.mapTo<T>().list()
        }
      }
    }

    inline fun <reified T : ApplicationRecord> createRecord(values: Map<String, Any?>): T {
      useJdbi {
        val queryBuilder = RecordQueryBuilder(it, KApplicationRecordClass(T::class))
        queryBuilder.create(values).let { create ->
          return create.execute(returningGeneratedKeys())
              .mapTo<Int>()
              .findFirst()
              .map { id -> findRecord<T>(id) }
              .orElseThrow { RuntimeException("Failed to create record.") }!!
        }
      }
    }

    inline fun <reified T : ApplicationRecord> T.saveRecord(): T {
      useJdbi {
        val queryBuilder = RecordQueryBuilder(it, KApplicationRecordClass(T::class))
        queryBuilder.update(this)
        return this
      }
    }

    inline fun <reified T : ApplicationRecord> T.destroyRecord(): T {
      useJdbi {
        val queryBuilder = RecordQueryBuilder(it, KApplicationRecordClass(javaClass.kotlin))
        queryBuilder.destroy(this)
        return this
      }
    }

    inline fun <reified T : ApplicationRecord> findRecord(id: Int): T? {
      useJdbi {
        val queryBuilder = RecordQueryBuilder(it, KApplicationRecordClass(T::class))
        return queryBuilder.findRecord(id)
            .mapTo<T>()
            .findFirst()
            .orElse(null)
      }
    }

    inline fun <T> useJdbi(block: (Handle) -> T) = JDBI.use(block)
  }
}