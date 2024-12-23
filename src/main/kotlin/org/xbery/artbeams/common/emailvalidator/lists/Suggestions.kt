package org.xbery.artbeams.common.emailvalidator.lists

import org.xbery.artbeams.common.emailvalidator.CharUtils.toMap

/**
 * Created by TPa on 15.02.17.
 */
object Suggestions {
    /**
     * domains with IP adress but without MX record.
     */
    val DOMAIN_TYPING_ERRORS: Map<String, String> = toMap(
        arrayOf(
            //yahoo.com
            arrayOf("yahooh.com", "yahoo.com"),  //	MX: []		IP: []		HOST: []
            //hotmail.com
            arrayOf(
                "hotmail.co",
                "hotmail.com"
            ),  //		MX: []		IP: [65.55.39.12, 64.4.6.233]		HOST: [windonws.com.br., windowsxp.com.tp.]
            arrayOf("hotmaill.com", "hotmail.com"),  //	MX: []		IP: [65.55.5.14]		HOST: [wwwhotmail.com.]
            //gmail.com
            arrayOf("gemiel.com", "gmail.com"),  //	MX: [mx.yandex.net.]		IP: [31.31.204.161]		HOST: []
            arrayOf(
                "dmail.com",
                "gmail.com"
            ),  //	MX: []		IP: [23.23.174.132]		HOST: [ec2-23-23-174-132.compute-1.amazonaws.com.]
            arrayOf("gmeil.com", "gmail.com"),  //	MX: []		IP: [158.69.143.102]		HOST: []
            arrayOf("gnail.com", "gmail.com"),  //		MX: [mail.gnail.com.]	IP: [58.215.64.224] 	HOST: []
            arrayOf("gemail.cz", "gmail.com"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("gmeil.cz", "gmail.com"),  //		MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("gaimil.com", "gmail.com"),  //	MX: []		IP: [103.224.212.222]		HOST: [lb-212-222.above.com.]
            arrayOf(
                "googl.com",
                "gmail.com"
            ),  //	MX: []		IP: [216.58.201.68, 2a00:1450:4014:800::2004]		HOST: [prg03s01-in-f4.1e100.net.]
            arrayOf("gimel.cz", "gmail.com"),  //		MX: [mxavas.forpsi.com]		IP: []		HOST: []
            arrayOf(
                "gmsil.com",
                "gmail.com"
            ),  //	MX: [gmsil.com.2.0001.arsmtp.com, gmsil.com.1.0001.arsmtp.com]		IP: [50.56.72.35]		HOST: [50-56-72-35.static.cloud-ips.com.]
            arrayOf("gmail.co", "gmail.com"),  //		MX: []		IP: []		HOST: []
            arrayOf("gmyil.com", "gmail.com"),  //	MX: []		IP: [103.224.182.207]		HOST: [lb-182-207.above.com.]
            //email.cz
            arrayOf("emai.cz", "email.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("emial.cz", "email.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("emil.cz", "email.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf(
                "cmail.cz",
                "email.cz"
            ),  //	MX: [ccx.spamfree.cz, cax.virusfree.cz, cdx.spamfree.cz, cbx.virusfree.cz]		IP: [46.255.231.48, 2a00:da80:f::48]		HOST: [centrum.cz.]
            //seznam.cz
            arrayOf("seznasm.cz", "seznam.cz"),  //	MX: []		IP: [46.149.114.131]		HOST: []
            arrayOf("seznan.cz", "seznam.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("seznbam.cz", "seznam.cz"),  //	MX: []		IP: [46.149.115.38]		HOST: []
            arrayOf("sezham.cz", "seznam.cz"),  //	MX: []		IP: [5.39.99.52]		HOST: []
            arrayOf(
                "sezmam.cz",
                "seznam.cz"
            ),  //	MX: []		IP: [217.198.113.104, 2a00:19a0:3:71:0:d9c6:7168:1]		HOST: [chimera.zarea.net.]
            arrayOf("sezna.cz", "seznam.cz"),  //		MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("zeznam.cz", "seznam.cz"),  //	MX: []		IP: [81.95.96.29]		HOST: [default.domeny.cz.]
            arrayOf("sesnam.cz", "seznam.cz"),  //	MX: []		IP: [81.95.96.29]		HOST: [default.domeny.cz.]
            arrayOf("senznam.cz", "seznam.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("sewznam.cz", "seznam.cz"),  //	MX: []		IP: []		HOST: []
            arrayOf("seznaqm.cz", "seznam.cz"),  //	MX: []		IP: []		HOST: []
            arrayOf("seznam.cs", "seznam.cz"),  //	MX: []		IP: []		HOST: []
            arrayOf("seznsam.cz", "seznam.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("seznmam.cz", "seznam.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("setnam.cz", "seznam.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("seznam.cc", "seznam.cz"),  //	MX: []		IP: [77.75.79.53]		HOST: [www.seznam.cz.]
            arrayOf("senzam.cz", "seznam.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("deznam.cz", "seznam.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("aseznam.cz", "seznam.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf(
                "ssznam.cz",
                "seznam.cz"
            ),  //	MX: []		IP: [81.31.34.55]		HOST: [81-31-34-55.static.masterinter.net.]
            arrayOf("sezan.cz", "seznam.cz"),  //		MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("seznma.cz", "seznam.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("seunam.cz", "seznam.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("sezbnam.cz", "seznam.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("sznam.cz", "seznam.cz"),  //		MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf(
                "sezn.cz",
                "seznam.cz"
            ),  //		MX: []		IP: [46.28.105.3, 2a02:2b88:1:4::17]		HOST: [miniweb.wedos.net.]
            arrayOf("sezma.cz", "seznam.cz"),  //		MX: []		IP: [185.53.177.29]		HOST: []
            arrayOf(
                "seznam.sk",
                "seznam.cz"
            ),  //	MX: []		IP: [77.75.77.120, 2a02:598:a::79:120]		HOST: [search.seznam.cz.]
            arrayOf("seynam.cy", "seznam.cz"),  //	MX: []		IP: []		HOST: []
            arrayOf(
                "seznam.ch",
                "seznam.cz"
            ),  //	MX: [no.mx.example.org.]		IP: [78.129.180.203, 84.200.228.115]		HOST: [mail.k4e1.com.]
            arrayOf(
                "sehnam.cz",
                "seznam.cz"
            ),  //	MX: []		IP: [46.28.105.3, 2a02:2b88:1:4::17]		HOST: [miniweb.wedos.net.]
            arrayOf("eznam.cz", "seznam.cz"),  //		MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf(
                "sseznam.cz",
                "seznam.cz"
            ),  //	MX: []		IP: [208.73.211.250, 208.73.210.200, 208.73.211.194, 208.73.210.209]		HOST: [null, null, null, null]
            arrayOf("sezam.cz", "seznam.cz"),  //		MX: [mail.sezam.cz.]		IP: []		HOST: []
            arrayOf("sezbam.cz", "seznam.cz"),  //	MX: [mail.h-email.net.]		IP: []		HOST: []
            arrayOf(
                "seuznam.cz",
                "seznam.cz"
            ),  //	MX: [mx.seuznam.cz]			IP: [178.217.244.35]		HOST: [frontend-1.sworp.com.]
            arrayOf("saznam.cz", "seznam.cz"),  //	MX: []		IP: [103.224.182.253]		HOST: [lb-182-253.above.com.]
            //centrum.cz
            arrayOf("centum.cz", "centrum.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("entrum.cz", "centrum.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("centtrum.cz", "centrum.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("centrun.cz", "centrum.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("centurm.cz", "centrum.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("sentrum.cz", "centrum.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("scentrum.cz", "centrum.cz"),  //	MX: []		IP: [217.198.116.188]		HOST: [czechiavps.zarea.net.]
            arrayOf("cemtrum.cz", "centrum.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("centru.cz", "centrum.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("entrum.cz", "centrum.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf(
                "cenrtum.cz",
                "centrum.cz"
            ),  //	MX: []		IP: [46.28.105.2, 2a02:2b88:1:4::16]		HOST: [default.wedos.net.]
            arrayOf("crntrum.cz", "centrum.cz"),  //	MX: []		IP: [103.224.182.241]		HOST: [lb-182-241.above.com.]
            arrayOf("centzrum.cz", "centrum.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            //annonce.cz
            arrayOf("anonce.cz", "annonce.cz"),  //	MX: []		IP: [213.29.194.4]		HOST: [lb.annonce.cz.]
            //tiscali.cz
            arrayOf("iscali.cz", "tiscali.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            //volny.cz
            arrayOf("olny.cz", "volny.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            arrayOf("vplny.cz", "volny.cz"),  //	MX: []		IP: [77.78.104.3]		HOST: [yurika.gransy.com.]
            //			{"",""},
        )
    )

    val GMAIL_SUGGESTIONS: Set<String> = HashSet(
        mutableListOf(
            "mail.com",  //	MX: [mx01.mail.com., mx00.mail.com.]			IP: []
            "qmail.com",  //	MX: [mx2.qq.com., mx3.qq.com., mx1.qq.com.]		IP: []
            "gmai.com",  //	MX: [mail.flip-mail.com.]						IP: []
            "gmajl.com",  //	MX: [mail.post-host.net.]						IP: []
            "ymail.com",  //	MX: [mta5.am0.yahoodns.net., mta7.am0.yahoodns.net., mta6.am0.yahoodns.net.]		IP: [74.6.50.150, 188.125.73.108, 77.238.184.150, 98.137.236.150]
            "email.com",  //	MX: [mx00.mail.com., mx01.mail.com.]			IP: []
            "gmaul.com",  //	MX: [mail.b-io.co.]								IP: []
            "bmail.com",  //	MX: [custmx.cscdns.net.]						IP: [165.160.13.20, 165.160.15.20]
            "gmil.com",  //	MX: [mail.yaxmail.net.]							IP: [208.64.137.62]
            "gma.com",  //	MX: [p.webcom.ctmail.com.]						IP: []
            "gmial.com",  //	MX: [MAIL.B-IO.CO.]								IP: []
            "cmail.cz",  //	MX: [cax.virusfree.cz., ccx.spamfree.cz., cbx.virusfree.cz., cdx.spamfree.cz.]		IP: []
            "qmail.cz",  //	MX: [smail2.qmail.cz., smail.qmail.cz.]			IP: []
            "gaimil.com",  //	MX: [mail.post-host.net.]						IP: []
            "gmail.con"
        )
    )

    val IGNORED: Set<String> = HashSet(
        mutableListOf(
            "op.pl",
            "mail.ru",
            "o2.pl",
            "interia.eu",
            "hotmail.com"
        )
    )
}
