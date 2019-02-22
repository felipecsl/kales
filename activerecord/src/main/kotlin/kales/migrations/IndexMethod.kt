package kales.migrations

enum class IndexMethod {
  BTree,
  Hash,
  /** Only for PostgreSQL */
  Gist,
  /** Only for PostgreSQL */
  SpGist,
  /** Only for PostgreSQL */
  Gin,
  /** Only for PostgreSQL */
  BRin
}