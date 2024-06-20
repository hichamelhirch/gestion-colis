import {Component} from "@angular/core";
import {ColisService} from "../services/colis.service";
import {MatDialog} from "@angular/material/dialog";
import {Colis, StatutColis} from "../models/colis.model";
import {SearcColisComponent} from "../searc-colis/searc-colis.component";
import {Router} from "@angular/router";
import {MatTableDataSource} from "@angular/material/table";
import {FilterDialogComponent} from "../filter-dialog/filter-dialog.component";

declare var Tawk_API: any;
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  searchQuery: string = '';
  dataSource: MatTableDataSource<Colis> = new MatTableDataSource<Colis>([]);
  filteredDataSource: MatTableDataSource<Colis> = new MatTableDataSource<Colis>([]);
  filterApplied: boolean = false;

  constructor(private colisService: ColisService, private router: Router, private dialog: MatDialog) {
    this.dataSource = new MatTableDataSource<Colis>();
  }


  searchColis(): void {
    if (this.searchQuery.trim().length === 0) {
      console.error('Search query is empty');
      return;
    }

    this.colisService.getColisByCodeBarre(this.searchQuery).subscribe(
      response => {
        this.openDialog(response);
      },
      error => {
        this.colisService.getColisByNumeroColis(this.searchQuery).subscribe(
          response => {
            this.openDialog(response);
          },
          error => {
            this.openDialog(null, 'Colis non trouvé');
          }
        );
      }
    );
  }

  openDialog(colis: Colis | null, errorMessage: string = ''): void {
    this.dialog.open(SearcColisComponent, {
      data: {colis, errorMessage}
    });
  }

  handleLogout(): void {
    localStorage.clear();
    this.router.navigate(['/login'])
  }

  openChatbot(): void {
    // Ouvrir le widget Tawk.to
    if (Tawk_API) {
      Tawk_API.maximize();
    } else {
      console.error('Le widget de chat n\'est pas encore chargé.');
    }
  }

  ngOnInit(): void {
    this.getColisList();
  }

  getColisList(): void {
    this.colisService.getColis().subscribe(colis => {
      this.dataSource.data = colis;
    });
  }

  openFilterDialog(): void {
    const dialogRef = this.dialog.open(FilterDialogComponent, {
      width: '300px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.applyFilter(result);
      }
    });
  }

  applyFilter(filterValues: any): void {
    this.colisService.filterColis(filterValues).subscribe(filteredColis => {
      this.filteredDataSource.data = filteredColis;
      this.filterApplied = true;
    });
  }


}
