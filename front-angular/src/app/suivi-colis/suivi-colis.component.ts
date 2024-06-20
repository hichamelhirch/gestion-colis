import { Component, OnInit } from '@angular/core';
import { SuiviColisService } from '../services/suivi-colis.service';
import { MatTableDataSource } from '@angular/material/table';
import {SuiviColis} from "../models/colis.model";

@Component({
  selector: 'app-suivi-colis',
  templateUrl: './suivi-colis.component.html',
  styleUrls: ['./suivi-colis.component.css']
})
export class SuiviColisComponent implements OnInit {
  displayedColumns: string[] = [ 'date', 'status', 'description'];
  dataSource = new MatTableDataSource<any>();
  barcode: string = '';
  //colisEnCours: SuiviColis[] = [];
  //colisLivres: SuiviColis[] = [];

  constructor(private suiviColisService: SuiviColisService) { }

  ngOnInit(): void {
    // Initialisation si nécessaire
  }

  searchByBarcode(): void {
    if (this.barcode) {
      this.suiviColisService.getOperationsByBarcode(this.barcode).subscribe(
        (data) => {
          const formattedData = data.map((item, index) => {
            const [date, status, description] = item.split(',').map(str => str.split(': ')[1]);
            return { date, status, description };
          });
          this.dataSource.data = formattedData;
        },
        (error) => {
          console.error('Erreur lors de la récupération des opérations :', error);
        }
      );
    }
  }

  /* loadColisEnCours(): void {
    this.suiviColisService.getColisEnCours().subscribe(
      (data) => {
        this.colisEnCours = data;
        this.dataSource.data = data;
      },
      (error) => {
        console.error('Erreur lors de la récupération des colis en cours :', error);
      }
    );
  }

  loadColisLivres(): void {
    this.suiviColisService.getColisLivres().subscribe(
      (data) => {
        this.colisLivres = data;
        this.dataSource.data = data;
      },
      (error) => {
        console.error('Erreur lors de la récupération des colis livrés :', error);
      }
    );
  }

   */
}
