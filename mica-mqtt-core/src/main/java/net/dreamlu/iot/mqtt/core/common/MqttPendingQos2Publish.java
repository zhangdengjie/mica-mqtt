package net.dreamlu.iot.mqtt.core.common;

import net.dreamlu.iot.mqtt.codec.MqttMessage;
import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;

import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * MqttPendingPublish，参考于 netty-mqtt-client
 */
public final class MqttPendingQos2Publish {
	private final MqttPublishMessage incomingPublish;
	private final RetryProcessor<MqttMessage> retryProcessor = new RetryProcessor<>();

	public MqttPendingQos2Publish(MqttPublishMessage incomingPublish, MqttMessage originalMessage) {
		this.incomingPublish = incomingPublish;
		this.retryProcessor.setOriginalMessage(originalMessage);
	}

	public MqttPublishMessage getIncomingPublish() {
		return incomingPublish;
	}

	public void startPubRecRetransmitTimer(ScheduledThreadPoolExecutor executor, Consumer<MqttMessage> sendPacket) {
		this.retryProcessor.setHandle((fixedHeader, originalMessage) ->
			sendPacket.accept(new MqttMessage(fixedHeader, originalMessage.variableHeader())));
		this.retryProcessor.start(executor);
	}

	public void onPubRelReceived() {
		this.retryProcessor.stop();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MqttPendingQos2Publish that = (MqttPendingQos2Publish) o;
		return Objects.equals(incomingPublish, that.incomingPublish);
	}

	@Override
	public int hashCode() {
		return Objects.hash(incomingPublish);
	}

}
