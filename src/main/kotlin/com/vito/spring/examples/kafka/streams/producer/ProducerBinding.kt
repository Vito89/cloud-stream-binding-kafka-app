package com.vito.spring.examples.kafka.streams.producer

import com.vito.spring.examples.kafka.streams.consumer.ConsumerBinding.Companion.BINDING_TARGET_NAME
import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel

interface ProducerBinding {

    @Output(BINDING_TARGET_NAME)
    fun messageChannel(): MessageChannel
}
