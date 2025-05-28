package turtleMart.global.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import turtleMart.global.kafka.dto.InventoryDecreasePayload;
import turtleMart.global.kafka.dto.KafkaMessage;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, Object> producerFactoryForObject() {
        return new DefaultKafkaProducerFactory<>(new HashMap<>());
    }

    @Bean
    public ProducerFactory<String, String> producerFactoryForString() {
        return new DefaultKafkaProducerFactory<>(new HashMap<>());
    }

    @Bean
    public ProducerFactory<String, KafkaMessage<InventoryDecreasePayload>> kafkaMessageProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Object> objectKafkaTemplate() {
        return new KafkaTemplate<>(producerFactoryForObject());
    }

    @Bean
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        return new KafkaTemplate<>(producerFactoryForString());
    }

    @Bean
    public KafkaTemplate<String, KafkaMessage<InventoryDecreasePayload>> kafkaTemplate() {
        return new KafkaTemplate<>(kafkaMessageProducerFactory());
    }
}
