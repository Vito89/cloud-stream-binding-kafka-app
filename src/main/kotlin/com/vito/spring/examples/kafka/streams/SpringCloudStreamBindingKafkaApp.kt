package com.vito.spring.examples.kafka.streams

import com.vito.spring.examples.kafka.streams.producer.ProducerBinding
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.stream.annotation.EnableBinding

@EnableBinding(ProducerBinding::class)
@SpringBootApplication
class SpringCloudStreamBindingKafkaApp

fun main(args: Array<String>) {
    SpringApplication.run(SpringCloudStreamBindingKafkaApp::class.java, *args)
}
