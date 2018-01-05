package myown
package telegram

import java.io.File

import models._
import org.fusesource.scalate.{TemplateEngine, TemplateSource}

object views {
  private val te = new TemplateEngine(sourceDirectories = Seq(classpathFile("views")))
  private val startTemplate = TemplateSource.fromFile(classpathFile("views/start.ssp"))

  private def classpathFile(f: String) = new File(getClass.getClassLoader.getResource(f).getFile)

  def start(name: String, accounts: Seq[AccountView]): String = {
    te.layout(startTemplate, Map("name" -> name, "accounts" -> accounts)).trim
  }
}
