import { DisconnectReason, BaileysEventMap } from 'baileys';
import QRCode from 'qrcode';

export async function handleConnectionUpdate(
    update: BaileysEventMap['connection.update'],
    reconnectCallback: () => void
) {
    const { connection, lastDisconnect, qr } = update;

    if (
        connection === 'close' &&
        (lastDisconnect?.error as any)?.output?.statusCode === DisconnectReason.restartRequired
    ) {
        const shouldReconnect = (lastDisconnect?.error as any)?.output?.statusCode !== 401;
        if (shouldReconnect) {
            reconnectCallback();
        }
    }

    if (qr) {
        console.log(await QRCode.toString(qr, { type: 'terminal' }));
    }
}
