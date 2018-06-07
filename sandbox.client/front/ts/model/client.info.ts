import {ClientRecord} from "./client.record";

export class ClientInfo {
    items: ClientRecord[];
    total_count: number;

    constructor(result) {
        this.total_count = Number(JSON.stringify(result.total_count));
        this.items = [];
        for (let i = 0; i < Number(JSON.stringify(result.items.length)); i++) {
            let clientRecord = new ClientRecord(result.items[i]);
            this.items.push(clientRecord);
        }
    }
}