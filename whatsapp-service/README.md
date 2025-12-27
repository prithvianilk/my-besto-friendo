# whatsapp-service

- A baileys websocket server that will be the interface to whatsapp

## Functionality

- Handle auth to connect to whatsapp as a linked device
- Listen to whatsapp messages

## Setup

A `config.json` file is required for the service to run. Create it by copying `config.example.json`:

```bash
cp config.example.json config.json
```

Then edit `config.json` to include the numbers you want to whitelist:

```json
{
    "whitelistedParticipantMobileNumbers": [
        "+919876543210"
    ]
}
```
