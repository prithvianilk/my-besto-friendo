import type { BaileysEventMap, WAMessage } from 'baileys'

interface Message {
    participantMobileNumber: string;
    senderName: string;
    fromMe: boolean;
    content: string;
    sentAt: Date
}

const messagesStore: Message[] = []

export function handleMessagesUpsert(update: BaileysEventMap['messages.upsert']) {
    if (update.type !== 'notify') {
        return
    }

    update.messages.forEach((message) => {
        const participantMobileNumber = message.key.remoteJid?.slice(0, 12).slice(-10)!
        const senderName = message.pushName!;
        const fromMe = message.key.fromMe!;

        const replyMessageContent = message.message?.extendedTextMessage?.text;
        const regularMessageContent = message.message?.conversation;

        const content = (replyMessageContent || regularMessageContent)!;

        const sentAt = getSentAt(message);

        messagesStore.push({
            participantMobileNumber,
            senderName,
            fromMe,
            content,
            sentAt
        })

        console.log(messagesStore)
    })
}

function getSentAt(message: WAMessage) {
    const sentAt = new Date();
    sentAt.setUTCSeconds(message.messageTimestamp as number);
    return sentAt;
}

