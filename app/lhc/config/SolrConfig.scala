package lhc.config

import play.api.Configuration
import java.io.File

case class SolrConfig(override val underlying: Configuration) extends ConfigAccessor {
  override val namespace = Some("solr")

  val allowCompression = getBoolean("allowCompression")
  val connectionTimeout: Option[Int] = getMillis("connectionTimeout").map(_.toInt).orElse(Option(100))
  val enabled = getBoolean("enabled", false)
  val followRedirects = getBoolean("followRedirects").orElse(Option(false))
  val home: Option[File] = getString("home").map(f => new File(f))
  lazy val isEmbedded = home.isDefined
  lazy val isRemote = url.isDefined
  val maxConnectionsPerHost: Option[Int] = getInt("maxConnectionsPerHost").orElse(Option(100))
  val maxRetries: Option[Int] = getInt("maxRetries").orElse(Option(1))
  val maxTotalConnections: Option[Int] = getInt("maxTotalConnections").orElse(Option(100))
  val readTimeout: Option[Int] = getMillis("readTimeout").map(_.toInt).orElse(Option(1000))
  val url = getUrl("url")

  override protected[config] def validate() {
    if (enabled) {
      require(isEmbedded || isRemote, "solr.home or solr.url must be defined")
      if (isEmbedded) {
        validateWritable(home.get)
        logger.info("Using embedded solr with home %s".format(home.get.getAbsolutePath))
      } else {
        // FIXME validateReachable
        logger.info("Using remote solr instance %s".format(url.get))
      }
    }
  }

  protected def validateWritable(f: File) {
    require(f.isDirectory && f.canWrite,
      "%s must be a writable and a directory".format(f.getAbsolutePath)
    )
  }
}
