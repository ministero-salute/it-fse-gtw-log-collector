package it.finanze.sanita.fse2.ms.gtw.logcollector.config.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 *	Kafka topic configuration.
 */
@Data
@Component
public class KafkaTopicCfg {

	/**
	 * Notifier topic.
	 */
	@Value("${kafka.notifier.topic}")
	private String notifierTopic;
	
	/**
	 * Notifier topic dlt.
	 */
	@Value("${kafka.notifier.deadletter.topic}")
	private String notifierDeadLetterTopic;

	/**
	 * Topic Producer to sender.
	 */
	@Value("${Kafka.notifier.sender.topic}")
	private String senderTopic;
	
}
