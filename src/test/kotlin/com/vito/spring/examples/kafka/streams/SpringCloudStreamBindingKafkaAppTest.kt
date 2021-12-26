package com.vito.spring.examples.kafka.streams

import com.vito.spring.examples.kafka.streams.consumer.ConsumerBinding
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SpringCloudStreamBindingKafkaAppTest {

    @Autowired
    lateinit var consumerBinding: ConsumerBinding

    @Test
    fun contextLoads() {
        assertNotNull(consumerBinding.messageChannel())
    }
}
