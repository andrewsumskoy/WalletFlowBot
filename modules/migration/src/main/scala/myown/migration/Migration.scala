package myown.migration

import org.flywaydb.core.Flyway

object Migration {
  def migrate(url: String, username: String, password: String): Unit = {
    val flyway = new Flyway()
    flyway.setDataSource(url, username, password)
    flyway.migrate()
  }
}
