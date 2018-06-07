export class Charm {
    id: number;
    name: string;

    constructor(result) {
        this.id = Number(JSON.stringify(result.id));
        this.name = JSON.stringify(result.name).replace(/["]+/g, '');
    }
}