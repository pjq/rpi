package me.pjq.rpi_server

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class RpiServerApplication

fun main(args: Array<String>) {
    SpringApplication.run(RpiServerApplication::class.java, *args)
}
