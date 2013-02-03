import lhc.config.AppConfig
import lhc.consumers.IrcConsumer

import play.api.{Application, Configuration, GlobalSettings, Mode}
import java.io.File

object LhcGlobal extends GlobalSettings {

  override def onLoadConfig(
    config: Configuration, path: File, classloader: ClassLoader, mode: Mode.Mode
  ): Configuration = {
    AppConfig.config = super.onLoadConfig(config, path, classloader, mode)
    AppConfig.config
  }

}
