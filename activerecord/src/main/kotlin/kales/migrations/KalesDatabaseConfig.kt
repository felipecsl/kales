package kales.migrations

import org.yaml.snakeyaml.Yaml
import java.io.InputStream

/** Represents a Kales database config as defined in a database.yml file */
data class KalesDatabaseConfig(
  val environment: String,
  val adapter: String,
  val host: String,
  val database: String,
  val username: String,
  val password: String
) {
  override fun toString(): String {
    return if (adapter == "h2") {
      "jdbc:$adapter:$host:$database"
    } else {
      "jdbc:$adapter://$host/$database?user=$username&password=$password"
    }
  }

  fun toConnectionString() = toString()

  companion object {
    @Suppress("UNCHECKED_CAST")
    fun fromDatabaseYml(fileStream: InputStream): KalesDatabaseConfig {
      val yaml = Yaml()
      val data = yaml.load<Map<String, Any>>(fileStream)
      // TODO handle muliple environments
      val devData = data["development"] as? Map<String, String> ?: throwMissingField("development")
      val adapter = devData["adapter"] ?: throwMissingField("adapter")
      val host = devData["host"] ?: throwMissingField("host")
      val database = devData["database"] ?: throwMissingField("database")
      val username = devData["username"] ?: ""
      val password = devData["password"] ?: ""
      return KalesDatabaseConfig("development", adapter, host, database, username, password)
    }

    private fun throwMissingField(name: String): Nothing =
      throw IllegalArgumentException(
        "Please set a value for the field '$name' in the file database.yml")
  }
}