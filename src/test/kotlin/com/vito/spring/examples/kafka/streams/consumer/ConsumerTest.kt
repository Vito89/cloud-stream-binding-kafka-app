package com.vito.spring.examples.kafka.streams.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.vito.spring.examples.kafka.streams.producer.ProducerBinding
import com.vito.spring.examples.kafka.streams.service.MessageService
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.ClassRule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration
import org.springframework.kafka.test.rule.EmbeddedKafkaRule
import org.springframework.messaging.support.MessageBuilder
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = [TestSupportBinderAutoConfiguration::class])
@EnableBinding(ProducerBinding::class)
class ConsumerTest {

    @Autowired
    lateinit var producer: ProducerBinding

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var messageService: MessageService

    companion object {
        @ClassRule @JvmField
        var embeddedKafka = EmbeddedKafkaRule(1, true, "any-name-of-topic")

        @BeforeEach
        fun beforeEach() {
            System.setProperty("spring.cloud.stream.kafka.binder.brokers", embeddedKafka.embeddedKafka.brokersAsString)
        }
    }

    @Test
    fun `should consume via txConsumer process`() {
        // ACT
        val request = mapOf(1 to "foo", 2 to "bar")
        producer.messageChannel().send(MessageBuilder.withPayload(request)
            .setHeader("someHeaderName", "someHeaderValue")
            .build())

        // ASSERT
        val requestAsMap = objectMapper.readValue<Map<String, Any?>>(objectMapper.writeValueAsString(request))
        runBlocking {
            delay(20)
            verify(messageService).consume(requestAsMap)
        }
    }
}
