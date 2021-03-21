package com.vito.spring.examples.kafka.streams.producer

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.stream.test.binder.MessageCollector
import org.springframework.messaging.support.MessageBuilder

@SpringBootTest
class ProducerTest {

    @Autowired
    lateinit var producerBinding: ProducerBinding

    @Autowired
    lateinit var messageCollector: MessageCollector

    @Test
    fun `should produce somePayload to channel`() {
        // ARRANGE
        val request = mapOf(1 to "foo", 2 to "bar", "three" to 10101)

        // ACT
        producerBinding.messageChannel().send(MessageBuilder.withPayload(request).build())
        val payload = messageCollector.forChannel(producerBinding.messageChannel())
            .poll()
            .payload

        // ASSERT
        val payloadAsMap = jacksonObjectMapper().readValue(payload.toString(), Map::class.java)
        assertTrue(request.entries.stream().allMatch { re ->
            re.value == payloadAsMap[re.key.toString()]
        })

        messageCollector.forChannel(producerBinding.messageChannel()).clear()
    }
}
