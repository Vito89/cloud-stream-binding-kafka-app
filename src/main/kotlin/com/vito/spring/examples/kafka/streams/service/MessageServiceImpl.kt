package com.vito.spring.examples.kafka.streams.service

import com.vito.spring.examples.kafka.streams.logger
import com.vito.spring.examples.kafka.streams.producer.ProducerBinding
import org.slf4j.Logger
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service
class MessageServiceImpl(val producerBinding: ProducerBinding) : MessageService {

    val log: Logger by logger()

    override fun consume(message: Map<String, Any?>) {
        val msg = MessageBuilder.withPayload(message).build()
        log.info("proxyToNode called with msg = {}", msg)
    }

    override fun produce(message: String) {
        val msg = MessageBuilder.withPayload(message).build()
        producerBinding.messageChannel().send(msg)
    }
}
