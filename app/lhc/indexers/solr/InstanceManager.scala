package lhc.indexers.solr

import lhc.config.SolrConfig
import lhc.util.DefaultLhcLogger

import org.apache.solr.client.solrj.SolrServer
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer
import org.apache.solr.client.solrj.impl.{HttpSolrServer, XMLResponseParser}
import org.apache.solr.core.{CoreContainer, CoreDescriptor, SolrCore}
import org.apache.solr.core.{SolrConfig => SConfig}

import java.io.File

object InstanceManager extends DefaultLhcLogger {

  def get(config: SolrConfig): SolrServer = {
    logger.debug("Creating solr server instance")
    if (config.isEmbedded) {
      createLocalInstance(config)
    } else {
      createRemoteInstance(config)
    }
  }

  def shutdown(instance: SolrServer) {
    logger.info("Shutting down solr server connection")
    // FIXME handle shutdown
    // instance.shutdown
  }

  protected[indexers] def createLocalInstance(config: SolrConfig): SolrServer = {
    val home = config.home.map(_.getAbsolutePath).get
    System.setProperty("solr.solr.home", home)
    val initializer = new CoreContainer.Initializer()
    val coreContainer = initializer.initialize()
    logger.info("Creating local solr instance using %s".format(home))
    new EmbeddedSolrServer(coreContainer, "")
  }
  protected[indexers] def createRemoteInstance(config: SolrConfig): SolrServer = {
    val url = config.url.map(_.toString).get
    val server = new HttpSolrServer(url)
    logger.info("Connecting to remote solr instance using %s".format(url))
    config.allowCompression.foreach(b => server.setAllowCompression(b))
    config.readTimeout.foreach(i => server.setSoTimeout(i))
    config.connectionTimeout.foreach(i => server.setConnectionTimeout(i))
    config.maxConnectionsPerHost.foreach(i => server.setDefaultMaxConnectionsPerHost(i))
    config.maxTotalConnections.foreach(i => server.setMaxTotalConnections(i))
    config.followRedirects.foreach(b => server.setFollowRedirects(b))
    config.allowCompression.foreach(b => server.setAllowCompression(b))
    config.maxRetries.foreach(i => server.setMaxRetries(i))
    server.setParser(new XMLResponseParser())
    server
  }
}
