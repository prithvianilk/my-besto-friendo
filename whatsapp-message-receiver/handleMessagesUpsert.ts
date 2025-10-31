import type { BaileysEventMap } from 'baileys'

interface Message {
    participantMobileNumber: string;
    senderName: string;
    fromMe: boolean;
    content: string
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

        messagesStore.push({
            participantMobileNumber,
            senderName,
            fromMe,
            content
        })

        console.log(messagesStore)
    })
}

