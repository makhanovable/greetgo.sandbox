import {Client} from "./client.record";

export interface ClientWrapper {
    items: Client[];
    total_count: number;
}