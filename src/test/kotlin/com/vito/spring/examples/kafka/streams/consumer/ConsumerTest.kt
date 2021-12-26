package com.vito.spring.examples.kafka.streams.consumer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.vito.spring.examples.kafka.streams.logger
import com.vito.spring.examples.kafka.streams.producer.ProducerBinding
import com.vito.spring.examples.kafka.streams.service.MessageService
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.junit.ClassRule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.cloud.stream.annotation.EnableBinding
import org.springframework.cloud.stream.test.binder.TestSupportBinderAutoConfiguration
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.KafkaMessageListenerContainer
import org.springframework.kafka.listener.MessageListener
import org.springframework.kafka.test.rule.EmbeddedKafkaRule
import org.springframework.kafka.test.utils.ContainerTestUtils
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.messaging.support.MessageBuilder
import org.springframework.test.annotation.DirtiesContext
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

@SpringBootTest
@DirtiesContext
@EnableAutoConfiguration(exclude = [TestSupportBinderAutoConfiguration::class])
@EnableBinding(ProducerBinding::class)
class ConsumerTest {

    @Autowired
    lateinit var producerBinding: ProducerBinding

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var messageService: MessageService

    lateinit var container: KafkaMessageListenerContainer<String, String>

    lateinit var records: BlockingQueue<ConsumerRecord<String, String>>

    val log: Logger by logger()

    companion object {
        @ClassRule @JvmField
        var embeddedKafka = EmbeddedKafkaRule(1, true, "name-of-topic")
    }

    @BeforeEach
    fun setUp() {
        // set up the Kafka consumer properties
        val consumerProperties = KafkaTestUtils.consumerProps("sender", "false",
            embeddedKafka.embeddedKafka)

        // create a Kafka consumer factory
        val consumerFactory = DefaultKafkaConsumerFactory<String, String>(
            consumerProperties)

        // set the topic that needs to be consumed
        val containerProperties = ContainerProperties("name-of-topic")

        // create a Kafka MessageListenerContainer
        container = KafkaMessageListenerContainer(consumerFactory,
            containerProperties)

        // create a thread safe queue to store the received message
        records = LinkedBlockingQueue<ConsumerRecord<String, String>>()

        // setup a Kafka message listener
        container
            .setupMessageListener(MessageListener<String, String> { record ->
                log.debug("test-listener received message='{}'",
                    record.toString())
                records.add(record)
            })

        // start the container and underlying message listener
        container.start()

        // wait until the container has the required number of assigned partitions
        ContainerTestUtils.waitForAssignment(container,
            embeddedKafka.embeddedKafka.partitionsPerTopic)
    }

    @Test
    fun `should consume via txConsumer process`() {
        // ACT
        val request = mapOf(1 to "foo", 2 to "bar")
        producerBinding.messageChannel().send(MessageBuilder.withPayload(request)
            .setHeader("someHeaderName", "someHeaderValue")
            .build())

        // ASSERT
        val requestAsMap = objectMapper.readValue<Map<String, Any?>>(objectMapper.writeValueAsString(request))
        runBlocking {
            delay(20)
            verify(messageService).consume(requestAsMap)
        }
    }

    @AfterEach
    fun `tear down`() {
        container.stop()
    }
}
