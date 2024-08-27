package com.mckernant1.lol.esports.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class Runner

fun main(args: Array<String>) {
	runApplication<Runner>(*args)
}
