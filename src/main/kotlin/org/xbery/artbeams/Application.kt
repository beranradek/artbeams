package org.xbery.artbeams

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class Application

/**
 * Application entry point.
 * @param args command line arguments
 */
fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
