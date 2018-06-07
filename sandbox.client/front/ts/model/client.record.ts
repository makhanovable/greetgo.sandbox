export class ClientRecord {
    id: number;
    name: string;
    charm: string;
    age: number;
    total_account_balance: number;
    max_balance: number;
    min_balance: number;

    constructor(result) {
        this.id = Number(JSON.stringify(result.id));
        this.name = JSON.stringify(result.name).replace(/["]+/g, '');
        this.charm = JSON.stringify(result.charm).replace(/["]+/g, '');
        this.age = Number(JSON.stringify(result.age));
        this.total_account_balance = Number(JSON.stringify(result.total));
        this.max_balance = Number(JSON.stringify(result.max));
        this.min_balance = Number(JSON.stringify(result.min));
    }

}