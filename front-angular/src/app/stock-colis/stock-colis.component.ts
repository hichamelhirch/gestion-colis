import { Component, OnInit, ViewChild } from '@angular/core';
import { ColisService } from '../services/colis.service';
import { Colis } from '../models/colis.model';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { SelectionModel } from '@angular/cdk/collections'; // Importer SelectionModel
// @ts-ignore
import { saveAs } from 'file-saver';
import { Router } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';

@Component({
  selector: 'app-stock-colis',
  templateUrl: './stock-colis.component.html',
  styleUrls: ['./stock-colis.component.css']
})
export class StockColisComponent implements OnInit {
  displayedColumns: string[] = ['select', 'Numero', 'barcode', 'creationDate', 'chargeur', 'livreA', 'de', 'a', 'type', 'frais', 'crbt', 'status', 'actions'];
  dataSource: MatTableDataSource<Colis> = new MatTableDataSource<Colis>([]);
  totalFrais: number = 0;
  totalCashDelivery: number = 0;
  selection = new SelectionModel<Colis>(true, []); // Initialiser le modèle de sélection

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private colisService: ColisService,
    private snackBar: MatSnackBar,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.getColisList();
  }

  getColisList(): void {
    this.colisService.getColis().subscribe(colis => {
      this.dataSource.data = colis;
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.sort.sort({ id: 'creationDate', start: 'desc', disableClear: false });
      this.calculateTotals();
    });
  }

  calculateTotals(): void {
    const pageIndex = this.paginator.pageIndex;
    const pageSize = this.paginator.pageSize;
    const startIndex = pageIndex * pageSize;
    const endIndex = startIndex + pageSize;
    const pageData = this.dataSource.data.slice(startIndex, endIndex);

    this.totalFrais = pageData.reduce((acc, val) => acc + val.frais, 0);
    this.totalCashDelivery = pageData.reduce((acc, val) => acc + val.cashDelivery, 0);
  }

  onPageChange(): void {
    this.calculateTotals();
  }

  copyToClipboard(text: string): void {
    navigator.clipboard.writeText(text).then(() => {
      this.snackBar.open('Code-barres copié!', 'Fermer', {
        duration: 2000,
      });
    }).catch(err => {
      console.error('Échec de la copie : ', err);
    });
  }

  updateColisStatusInDataSource(id: number, status: string): void {
    const colisIndex = this.dataSource.data.findIndex(colis => colis.id === id);
    if (colisIndex !== -1) {
      // @ts-ignore
      this.dataSource.data[colisIndex].statusColis = status;
      this.dataSource.data = [...this.dataSource.data]; // Crée une nouvelle référence
      this.dataSource._updateChangeSubscription(); // Notifie Angular de la modification
      this.cdr.detectChanges(); // Force la détection des changements
    }
  }

  downloadLabelAndConfirmStatus(colis: Colis): void {
    // @ts-ignore
    if (colis.statusColis !== 'CONFIRMER') {
      this.colisService.updateColisStatusToConfirmed(colis.id!).subscribe(() => {
        this.updateColisStatusInDataSource(colis.id!, 'CONFIRMER');
        this.downloadLabel(colis.id!);
      }, error => {
        console.error('Erreur de mise à jour:', error);
      });
    } else {
      this.downloadLabel(colis.id!);
    }
  }

  downloadLabel(id: number): void {
    this.colisService.generateLabel(id).subscribe(() => {
      this.colisService.downloadLabel(id).subscribe(response => {
        if (response.body) {
          const blob = new Blob([response.body], { type: response.headers.get('Content-Type') || 'application/pdf' });
          const contentDisposition = response.headers.get('Content-Disposition');
          const matches = /filename="([^;]+)"/.exec(contentDisposition || '');
          const filename = (matches != null && matches[1]) ? matches[1] : 'label.pdf';
          saveAs(blob, filename);
        } else {
          console.error('Réponse vide lors du téléchargement du label');
        }
      }, error => {
        console.error('Erreur de téléchargement:', error);
      });
    }, error => {
      console.error('Erreur de génération du label:', error);
    });
  }

  formatBarcodeText(text: string): string {
    let formattedText = '';
    const length = Math.min(text.length, 12);
    for (let i = 0; i < length; i += 4) {
      if (i > 0) {
        formattedText += ' ';
      }
      formattedText += text.substring(i, i + 4);
    }
    return formattedText;
  }

  editColis(id: number): void {
    this.router.navigate(['home/edit-colis', id]);
  }

  updateStatusToConfirmed(id: number): void {
    this.colisService.updateColisStatusToConfirmed(id).subscribe(() => {
      this.updateColisStatusInDataSource(id, 'CONFIRMER');
    }, error => {
      console.error('Erreur de mise à jour:', error);
    });
  }

  updateStatusToCancel(id: number): void {
    this.colisService.updateColisStatusToCancel(id).subscribe(() => {
      this.updateColisStatusInDataSource(id, 'ANNULER');
      this.snackBar.open('Colis annulé avec succès!', 'Fermer', {
        duration: 2000,
      });
    }, error => {
      console.error('Erreur de mise à jour:', error);
      this.snackBar.open('Erreur lors de l\'annulation du colis.', 'Fermer', {
        duration: 2000,
      });
    });
  }

  getStatusLabel(status: string): string {
    switch(status) {
      case 'CONFIRMER':
        return 'CONFIRMÉ';
      case 'BROUILLON':
        return 'BROUILLON';
      case 'ANNULER':
        return 'ANNULÉ';
      default:
        return status;
    }
  }

  // Sélection et actions groupées
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

  toggleSelection(row: Colis) {
    this.selection.toggle(row);
  }

  batchConfirm() {
    const selectedColis = this.selection.selected;
    // @ts-ignore
    const validColis = selectedColis.filter(colis => colis.statusColis !== 'ANNULER');

    const selectedIds = validColis.map(colis => colis.id!);
    selectedIds.forEach(id => this.updateStatusToConfirmed(id));

    // Réinitialiser la sélection après confirmation
    this.selection.clear();

    // Rafraîchir la liste des colis
    this.getColisList();
  }

  batchDownloadLabel() {
    const selectedColis = this.selection.selected;
    // @ts-ignore
    const validColis = selectedColis.filter(colis => colis.statusColis !== 'ANNULER');

    validColis.forEach(colis => {
      this.downloadLabel(colis.id!);
    });

    // Réinitialiser la sélection après le téléchargement
    this.selection.clear();
  }

}
