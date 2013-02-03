package lhc.util

import play.api.Logger
import org.slf4j.{Logger => Slf4jLogger}

trait LhcLogger {
  val loggerName: String
  lazy val logger: Logger = Logger(loggerName)
}

trait DefaultLhcLogger extends LhcLogger {
  override val loggerName: String = "lhc"
}
object DefaultLhcLogger extends DefaultLhcLogger
