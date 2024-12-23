package org.xbery.artbeams.common.emailvalidator

import java.util.*
import javax.naming.Context
import javax.naming.NamingException
import javax.naming.directory.InitialDirContext

/**
 * http://docs.oracle.com/javase/1.5.0/docs/guide/jndi/jndi-dns.html
 * https://www.captechconsulting.com/blogs/accessing-the-dusty-corners-of-dns-with-java
 * https://cs.wikipedia.org/wiki/Domain_Name_System
 * https://en.wikipedia.org/wiki/List_of_DNS_record_types
 *
 * @author DDv
 */
object DNSLookup {
    private const val MX_ATTRIB = "MX"
    private const val ADDR_ATTRIB_IPV4 = "A"
    private const val ADDR_ATTRIB_IPV6 = "AAAA"
    private val MX_ATTRIBS = arrayOf(MX_ATTRIB)
    private val ADDR_ATTRIBS = arrayOf(ADDR_ATTRIB_IPV4, ADDR_ATTRIB_IPV6)

    //private static final Logger LOG = Logger.getRootLogger();
    private val env = Properties()

    init {
        env[Context.INITIAL_CONTEXT_FACTORY] = "com.sun.jndi.dns.DnsContextFactory"
    }

    //============== VEREJNE METODY INSTANCE ====================================
    /**
     * @return list of mx records for domain
     */
    fun getMXServers(domain: String?): List<String> {
        val servers: MutableList<String> = ArrayList()
        try {
            val idc = InitialDirContext(env)
            val attrs = idc.getAttributes(domain, MX_ATTRIBS)
            val attr = attrs[MX_ATTRIB]

            if (attr != null) {
                for (i in 0..<attr.size()) {
                    val mxAttr = attr[i] as String
                    val parts = mxAttr.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                    // Split off the priority, and take the last field
                    var part = parts[parts.size - 1]
                    part = part.replaceFirst("\\.$".toRegex(), "")
                    servers.add(part)
                }
            }
        } catch (e: NamingException) {
            //	LOG.warn("unable to get MX record for " + domain, e);
        }
        return servers
    }


    /**
     * @return list of IP adresses for domain
     */
	@JvmStatic
	fun getIPAddresses(hostname: String?): List<String> {
        val ipAddresses: MutableList<String> = ArrayList()
        try {
            val idc = InitialDirContext(env)
            val attrs = idc.getAttributes(hostname, ADDR_ATTRIBS)
            val ipv4 = attrs[ADDR_ATTRIB_IPV4]
            val ipv6 = attrs[ADDR_ATTRIB_IPV6]


            if (ipv4 != null) {
                for (i in 0..<ipv4.size()) {
                    ipAddresses.add(ipv4[i] as String)
                }
            }

            if (ipv6 != null) {
                for (i in 0..<ipv6.size()) {
                    ipAddresses.add(ipv6[i] as String)
                }
            }
        } catch (e: NamingException) {
            //	LOG.warn("unable to get IP for " + hostname, e);
        }
        return ipAddresses
    }
}
