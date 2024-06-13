import {Component} from "@angular/core";
import {ColisService} from "../services/colis.service";
import {MatDialog} from "@angular/material/dialog";
import {Colis} from "../models/colis.model";
import {SearcColisComponent} from "../searc-colis/searc-colis.component";
import {Router} from "@angular/router";

declare var Tawk_API: any;
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  searchQuery: string = '';

  constructor(private colisService: ColisService,private router:Router,private dialog: MatDialog) {}

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
      data: { colis, errorMessage }
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
}
