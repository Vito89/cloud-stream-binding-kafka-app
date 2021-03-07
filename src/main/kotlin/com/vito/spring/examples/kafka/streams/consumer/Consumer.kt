package com.vito.spring.examples.kafka.streams.consumer

import com.vito.spring.examples.kafka.streams.service.MessageService
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload

@EnableBinding(ConsumerBinding::class)
class Consumer(val messageService: MessageService) {

    @StreamListener(target = ConsumerBinding.BINDING_TARGET_NAME)
    fun process(
        @Payload message: Map<String, Any?>,
        @Header(value = KafkaHeaders.OFFSET, required = false) offset: Int?
    ) {
        messageService.consume(message)
    }
}
