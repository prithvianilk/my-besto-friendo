import { Kafka, Producer } from 'kafkajs';
import { Message } from './handleMessagesUpsert';

export interface MessageProducer {
    publish(message: Message): Promise<void>;
}

export class KafkaProducer implements MessageProducer {
    private producer: Producer;

    public static async initProducer(brokers: string[]): Promise<KafkaProducer> {
        const producer = await new Kafka({ clientId: 'whatsapp-service', brokers }).producer();
        producer.connect();
        return new KafkaProducer(producer);
    }

    public constructor(producer: Producer) {
        this.producer = producer;
    }

    async publish(message: Message): Promise<void> {
        await this.producer.send({
            topic: 'whatsapp-messages',
            messages: [
                {
                    key: message.participantMobileNumber,
                    value: JSON.stringify(message),
                },
            ],
        });
    }

    async disconnect(): Promise<void> {
        await this.producer.disconnect();
    }
}
