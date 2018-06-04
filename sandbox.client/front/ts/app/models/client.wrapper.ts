import {Client} from "./client";

export interface ClientWrapper {
    items: Client[];
    total_count: number;
}