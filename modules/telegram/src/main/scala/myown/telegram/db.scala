package myown.telegram

import com.zaxxer.hikari.HikariDataSource
import doobie._, doobie.hikari._
import cats.effect.IO

object db {
  val ds: HikariDataSource = {
    val ds = new HikariDataSource
    ds.setDriverClassName("org.postgresql.Driver")
    ds.setJdbcUrl(config.db.url)
    ds.setUsername(config.db.username)
    ds.setPassword(config.db.password)
    ds.setMinimumIdle(config.db.pool.min)
    ds.setMaximumPoolSize(config.db.pool.max)
    ds
  }

  val xa: Transactor[IO] = HikariTransactor.apply[IO](ds)
}
