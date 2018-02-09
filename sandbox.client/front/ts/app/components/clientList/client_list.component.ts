import {ClientFilter} from "../../../model/ClientFilter";
import {CharmInfo} from "../../../model/CharmInfo";
import {HttpService} from "../../HttpService";
import {ClientInfo} from "../../../model/ClientInfo";
import {Component, OnInit, ViewChild} from "@angular/core";
import {MatDialog, MatPaginator, MatSort, MatTableDataSource} from "@angular/material";
import {SelectionModel} from "@angular/cdk/collections";
import {ClientFormComponent} from "../clientForm/client_form.component";

@Component({
  template: require('./client_list.component.html'),
  selector: 'client-list-component',
  styles: [require('./client_list.component.css')],
})
// FIXME: 2/9/18 проверь почему при смене страницы таблицы (и других действиях) на сервер идет два запроса
// FIXME: 2/9/18 при нажатии на "cancel" в окне редактирования/добавления не нужно обновлять таблицу
export class ClientListComponent implements OnInit {

  displayedColumns = ['select', 'fio', 'charm', 'age', 'totalAccountBalance', 'maximumBalance', 'minimumBalance'];
  dataSource: MatTableDataSource<ClientInfo>;

  charmsMap: any[] = [];
  charmsArray: CharmInfo[];

  tableElemets: ClientInfo[];
  selection = new SelectionModel<ClientInfo>(true, []);

  filter: string = '';
  selectedOrder = 'fio';
  desc: number = 0;

  @ViewChild(MatSort) sort: MatSort;
  @ViewChild(MatPaginator) paginator: MatPaginator;

  constructor(public dialog: MatDialog, private httpService: HttpService) {
  }

  ngOnInit() {
    this.paginator.pageSize = 10;
    this.loadChamrs();
    //noinspection JSUnusedLocalSymbols
    this.paginator.page.subscribe(change => {
      this.applyFilter(false);
    });
    this.applyFilter(true);

    this.sort.sortChange.subscribe(data => {
      this.desc = data['direction'] === 'desc' ? 1 : 0;
      this.selectedOrder = data['direction'] === '' ? "" : data['active'];
      this.applyFilter(false);
    }, error => {
      console.log(error);
    })
  }

  loadChamrs() {
    this.httpService.get("/charm/list").toPromise().then(res => {
      this.charmsArray = JSON.parse(res.text()) as CharmInfo[];
      this.charmsArray.forEach(element => {
        this.charmsMap[element.id] = element;

      });
    }).catch(error => {
      console.log(error);
    })
  }

  loadTableData(filter: ClientFilter) {

    this.httpService.get("/client/list", {
      limit: filter.limit,
      page: filter.pageIndex,
      filter: filter.filter,
      orderBy: filter.orderBy,
      desc: filter.desc,

    }).toPromise().then(res => {
      this.tableElemets = JSON.parse(res.text()) as ClientInfo[];
      this.initMatTable();
    }).catch(error => {
      console.log(error);
    })
  }

  initMatTable() {
    this.selection.clear();
    this.dataSource = new MatTableDataSource<ClientInfo>(this.tableElemets);
  }

  applyFilter(getAmount: boolean) {

    let clientFilter = new ClientFilter();
    clientFilter.filter = this.filter;
    clientFilter.desc = this.desc;
    clientFilter.limit = this.paginator.pageSize;
    clientFilter.pageIndex = this.paginator.pageIndex;
    clientFilter.orderBy = this.selectedOrder.toLowerCase();

    if (getAmount) {
      this.httpService.get("/client/amount", {
        filter: clientFilter.filter,
      }).toPromise().then(res => {
        this.paginator.length = Number.parseInt(res.text());
        //
        this.loadTableData(clientFilter);
      }).catch(error => {
        console.log(error);
      })
    } else {
      this.loadTableData(clientFilter);
    }
  }

  add() {
    this.openClientModalForm(undefined);
  }

  editSelectedItem() {
    this.openClientModalForm(this.selection.selected[0]);
  }

  removeSelectedItems() {
    let answer = confirm('are you sure? ' + this.selection.selected.length + ' client will be removed');
    if (answer) {
      let ids = [];
      this.selection.selected.forEach(element => {
        ids.push(element.id)
      });
      console.log(JSON.stringify(ids));

      //noinspection JSUnusedLocalSymbols
      this.httpService.get("/client/remove", {
        ids: JSON.stringify(ids)
      }).toPromise().then(res => {
        this.applyFilter(true);
      }).catch(error => {
        console.log(error)
      })
    }

  }

  //noinspection JSUnusedGlobalSymbols
  updateMatTable() {
    this.selection.clear();
    this.initMatTable();
  }

  isAllSelected() {
    const numSelected = this.selection.selected.length;
    const numRows = this.dataSource.data.length;
    return numSelected === numRows;
  }

  masterToggle() {
    this.isAllSelected() ?
      this.selection.clear() :
      this.dataSource.data.forEach(row => this.selection.select(row));
  }

  openClientModalForm(selected): void {
    let dialogRef = this.dialog.open(ClientFormComponent, {
      // minWidth: '250px',
      maxWidth: "1000px",
      // position: {top:"100px"} ,

      data: {
        charms: this.charmsArray,
        item: selected,

      }
    });

    //noinspection JSUnusedLocalSymbols
    dialogRef.afterClosed().subscribe(result => {
      this.applyFilter(true);
    });
  }


}