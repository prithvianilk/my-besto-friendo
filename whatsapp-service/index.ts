import makeWASocket, { useMultiFileAuthState } from 'baileys';
import P from 'pino';
import { handleConnectionUpdate } from './handleConnectionUpdate.js';
import { handleMessagesUpsert } from './handleMessagesUpsert.js';
import { KafkaProducer, MessageProducer } from './producer.js';

const messageProducer: MessageProducer = await KafkaProducer.initProducer(['localhost:9094']);

async function initialiseSocket() {
    // TODO: Fix before deployment
    const { state, saveCreds } = await useMultiFileAuthState('auth');

    const sock = makeWASocket({
        auth: state,
        logger: P({ level: 'silent' }),
    });

    sock.ev.on('creds.update', saveCreds);

    sock.ev.on('connection.update', async (update) => {
        await handleConnectionUpdate(update, initialiseSocket);
    });

    sock.ev.on('messages.upsert', async (update) => {
        await handleMessagesUpsert(update, messageProducer);
    });
}

initialiseSocket();
