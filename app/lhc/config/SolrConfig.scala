package lhc.config

import play.api.Configuration
import java.io.File

case class SolrConfig(override val underlying: Configuration) extends ConfigAccessor {
  override val namespace = Some("solr")

  val enabled = getBoolean("enabled", false)
  val home: Option[File] = getString("home").map(f => new File(f))
  lazy val isEmbedded = home.isDefined
  lazy val isRemote = url.isDefined
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
