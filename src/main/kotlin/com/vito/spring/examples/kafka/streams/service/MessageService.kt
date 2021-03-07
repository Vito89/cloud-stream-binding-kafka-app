package com.vito.spring.examples.kafka.streams.service

interface MessageService {

    fun consume(message: Map<String, Any?>)

    fun produce(message: String)
}
