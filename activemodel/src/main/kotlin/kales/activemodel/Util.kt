package kales.activemodel

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi

inline fun <T> Jdbi.use(block: (Handle) -> T) = open().use { block(it) }