package com.vito.spring.examples.kafka.streams.consumer

import org.springframework.cloud.stream.annotation.Input
import org.springframework.messaging.MessageChannel

interface ConsumerBinding {

    companion object {
        const val BINDING_TARGET_NAME = "customChannel"
    }

    @Input(BINDING_TARGET_NAME)
    fun messageChannel(): MessageChannel
}
