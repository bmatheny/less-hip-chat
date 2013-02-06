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
  
  override def beforeStart(app: Application) {
    System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
    System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http", "WARN");
    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.http.wire", "WARN");
    System.setProperty("org.apache.commons.logging.simplelog.http.wire", "WARN");
    System.setProperty("org.apache.commons.logging.simplelog.http.wire.content", "WARN");
    System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "WARN");
  }

}
