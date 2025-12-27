import { readFileSync, existsSync } from 'fs';
import z from 'zod';

const ConfigSchema = z.object({
    whitelistedParticipantMobileNumbers: z.array(z.string()),
});

type Config = z.infer<typeof ConfigSchema>;

function loadConfig(): Config {
    if (!existsSync('./config.json')) {
        throw new Error('config.json not found. Please create one based on config.example.json');
    }

    try {
        const rawConfig = JSON.parse(readFileSync('./config.json', 'utf-8'));
        return ConfigSchema.parse(rawConfig);
    } catch (error) {
        if (error instanceof z.ZodError) {
            throw new Error(`Invalid configuration in config.json: ${error.message}`);
        }
        throw new Error(`Failed to parse config.json: ${error instanceof Error ? error.message : String(error)}`);
    }
}

export const config = loadConfig();
