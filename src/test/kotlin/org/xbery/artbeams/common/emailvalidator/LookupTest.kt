package org.xbery.artbeams.common.emailvalidator

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.xbery.artbeams.common.emailvalidator.DNSLookup.getIPAddresses

/**
 * Created by tomaspavel on 16.2.17.
 */
class LookupTest {
    private val validator = EmailValidatorBuilder().build()

    @Test
    @Disabled
    fun ipLookupTest() {
        var ips: List<String?> = getIPAddresses("seznam.cz")
        Assertions.assertFalse(ips.isEmpty())

        ips = getIPAddresses("alza.cz")
        Assertions.assertFalse(ips.isEmpty())

        ips = getIPAddresses("www.alza.cz")
        Assertions.assertFalse(ips.isEmpty())

        ips = getIPAddresses("neexistujicidomena.cz")
        Assertions.assertTrue(ips.isEmpty())
    }

    @Test
    @Disabled
    fun mxLookupTest() {
        var result = validator.validate("karel@seznam.cz")
        Assertions.assertTrue(result.email.hasMXRecord())

        result = validator.validate("karel@alza.cz")
        Assertions.assertTrue(result.email.hasMXRecord())

        result = validator.validate("karel@neexistujicidomena.cz")
        Assertions.assertFalse(result.email.hasMXRecord())

        result = validator.validate("karel@etnetera.cz")
        Assertions.assertTrue(result.email.hasMXRecord())

        result = validator.validate("karel@site.cz")
        Assertions.assertFalse(result.email.hasMXRecord())

        result = validator.validate("wjj633@126.com")
        Assertions.assertTrue(result.email.hasMXRecord())

        result = validator.validate("13472668581@163.com")
        Assertions.assertTrue(result.email.hasMXRecord())

        result = validator.validate("144969292@qq.cm")
        Assertions.assertTrue(result.email.hasMXRecord())

        result = validator.validate("xiaokang9506@sina.com")
        Assertions.assertTrue(result.email.hasMXRecord())
    }

    @Test
    fun validTest() {
        val validator = EmailValidatorBuilder().build()
        val result = validator.validate("test@gmail.com")
        Assertions.assertTrue(result.email.isDomainInValidMailServersMap)
    }
}
