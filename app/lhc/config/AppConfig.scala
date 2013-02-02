package lhc.config

import play.api.{Configuration, PlayException}
import java.util.concurrent.atomic.AtomicReference
import scala.util.control.NonFatal

object AppConfig {
  private var globalConfigRef: AtomicReference[Configuration] =
    new AtomicReference(Configuration.empty)
  private var ircConfigRef: AtomicReference[IrcConfig] =
    new AtomicReference(IrcConfig(config))
  private var solrConfigRef: AtomicReference[SolrConfig] =
    new AtomicReference(SolrConfig(config))

  def config = globalConfigRef.get
  def config_=(cfg: Configuration) = {
    globalConfigRef.set(cfg)
    initialize()
  }
  def irc = ircConfigRef.get
  def solr = solrConfigRef.get

  private def initialize() {
    val ircConfig = IrcConfig(config)
    val solrConfig = SolrConfig(config)
    try {
      ircConfig.validate
      ircConfigRef.set(ircConfig)
      solrConfig.validate
      solrConfigRef.set(solrConfig)
    } catch {
      case e:PlayException =>
        throw e
      case NonFatal(e) =>
        throw new PlayException("Error during configuration", e.getMessage, e)
    }
  }

}
