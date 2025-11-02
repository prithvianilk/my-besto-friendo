import type { BaileysEventMap } from 'baileys'
import { MessageProducer } from './producer.js'
import z from 'zod';

export const Message = z.object({
    participantMobileNumber: z.string(),
    senderName: z.string(),
    fromMe: z.boolean(),
    content: z.string(),
    sentAt: z.date(),
})

export type Message = z.infer<typeof Message>

export async function handleMessagesUpsert(
    update: BaileysEventMap['messages.upsert'],
    messageProducer: MessageProducer) {
    if (update.type !== 'notify') {
        return
    }

    await Promise.all(
        update.messages.map(async (rawMessage) => {
            const sentAt = new Date((rawMessage.messageTimestamp as number) * 1000);
            const participantMobileNumber = rawMessage.key.remoteJid?.slice(0, 12).slice(-10)!
            const senderName = rawMessage.pushName!;
            const fromMe = rawMessage.key.fromMe!;

            const replyMessageContent = rawMessage.message?.extendedTextMessage?.text;
            const regularMessageContent = rawMessage.message?.conversation;
            const content = (replyMessageContent || regularMessageContent)!;

            try {
                const message = Message.parse({
                    participantMobileNumber,
                    senderName,
                    fromMe,
                    content,
                    sentAt
                })

                console.log("Parsed message: " + message);
                await messageProducer.publish(message);
            } catch (error) {
                // TODO: Handle this?
                console.error('Failed to publish message to Kafka:', error)
            }
        })
    )
}

