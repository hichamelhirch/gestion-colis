import { Component, OnInit, ViewChild ,Input} from '@angular/core';
import { ColisService } from '../services/colis.service';
import { Colis, StatutColis } from '../models/colis.model';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { SelectionModel } from '@angular/cdk/collections';
import { forkJoin } from 'rxjs';

// @ts-ignore
import { saveAs } from 'file-saver';
import { Router } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';
import {CancelDialogComponent} from "../cancel-dialog/cancel-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {FilterDialogComponent} from "../filter-dialog/filter-dialog.component";

@Component({
  selector: 'app-stock-colis',
  templateUrl: './stock-colis.component.html',
  styleUrls: ['./stock-colis.component.css']
})
export class StockColisComponent implements OnInit {
  displayedColumns: string[] = ['select', 'Numero', 'barcode', 'creationDate', 'chargeur', 'livreA', 'de', 'a', 'type', 'frais', 'crbt', 'status','etatSuiviColis','actions'];
 dataSource: MatTableDataSource<Colis> = new MatTableDataSource<Colis>([]);
  filteredDataSource: MatTableDataSource<Colis> = new MatTableDataSource<Colis>([]);
  filterApplied: boolean = false;
  totalFrais: number = 0;
  totalCashDelivery: number = 0;
  selection = new SelectionModel<Colis>(true, []);
  selectedMap: Map<number, boolean> = new Map(); // New map to track selections

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private colisService: ColisService,
    private snackBar: MatSnackBar,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private dialog: MatDialog
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
      this.updateSelections(); // Update selections when data changes
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
    this.updateSelections();
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

  updateColisStatusInDataSource(id: number, status: StatutColis): void {
    const colisIndex = this.dataSource.data.findIndex(colis => colis.id === id);
    if (colisIndex !== -1) {
      this.dataSource.data[colisIndex].statusColis = status;
      this.dataSource.data = [...this.dataSource.data];
      this.dataSource._updateChangeSubscription();
      this.cdr.detectChanges();
    }
  }

  downloadLabelAndConfirmStatus(colis: Colis): void {
    if (colis.statusColis !== StatutColis.CONFIRMER) {
      this.colisService.updateColisStatusToConfirmed(colis.id!).subscribe(() => {
        this.updateColisStatusInDataSource(colis.id!, StatutColis.CONFIRMER);
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
      this.updateColisStatusInDataSource(id, StatutColis.CONFIRMER);
      this.snackBar.open('Colis confirmé avec succès!', 'Fermer', {
        duration: 2000,
      });
    }, error => {
      console.error('Erreur de mise à jour:', error);
      this.snackBar.open('Erreur lors de la confirmation du colis.', 'Fermer', {
        duration: 2000,
      });
    });
  }

  updateStatusToCancel(id: number): void {
    const dialogRef = this.dialog.open(CancelDialogComponent, {
      width: '300px'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result === true) {
        this.colisService.updateColisStatusToCancel(id).subscribe(() => {
          this.updateColisStatusInDataSource(id, StatutColis.ANNULER);
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
    });
  }



  getStatusLabel(status: StatutColis): string {
    switch (status) {
      case StatutColis.CONFIRMER:
        return 'CONFIRMÉ';
      case StatutColis.BROUILLON:
        return 'BROUILLON';
      case StatutColis.ANNULER:
        return 'ANNULÉ';
      default:
        return status;
    }
  }

  isAllSelected() {
    const pageData = this.dataSource.data.slice(
      this.paginator.pageIndex * this.paginator.pageSize,
      (this.paginator.pageIndex + 1) * this.paginator.pageSize
    );
    const numRows = pageData.filter(colis => colis.statusColis !== StatutColis.ANNULER).length;
    const selectedRows = pageData.filter(colis => this.selection.isSelected(colis) && colis.statusColis !== StatutColis.ANNULER);
    return selectedRows.length === numRows;
  }

  masterToggle() {
    const pageData = this.dataSource.data.slice(
      this.paginator.pageIndex * this.paginator.pageSize,
      (this.paginator.pageIndex + 1) * this.paginator.pageSize
    );
    const isAllSelected = this.isAllSelected();
    if (isAllSelected) {
      pageData.forEach(row => {
        if (row.statusColis !== StatutColis.ANNULER) {
          this.selection.deselect(row);
          this.selectedMap.delete(row.id!);
        }
      });
    } else {
      pageData.forEach(row => {
        if (row.statusColis !== StatutColis.ANNULER) {
          this.selection.select(row);
          this.selectedMap.set(row.id!, true);
        }
      });
    }
  }

  toggleSelection(row: Colis) {
    if (row.statusColis !== StatutColis.ANNULER) {
      this.selection.toggle(row);
      if (this.selection.isSelected(row)) {
        this.selectedMap.set(row.id!, true);
      } else {
        this.selectedMap.delete(row.id!);
      }
    }
  }

  updateSelections() {
    this.selection.clear();
    const pageData = this.dataSource.data.slice(
      this.paginator.pageIndex * this.paginator.pageSize,
      (this.paginator.pageIndex + 1) * this.paginator.pageSize
    );
    pageData.forEach(row => {
      if (this.selectedMap.get(row.id!) && row.statusColis !== StatutColis.ANNULER) {
        this.selection.select(row);
      }
    });
  }

  batchConfirm() {
    const selectedColis = Array.from(this.selectedMap.keys())
      .map(id => this.dataSource.data.find(colis => colis.id === id))
      .filter(colis => colis && colis.statusColis !== StatutColis.ANNULER);

    const validColis = selectedColis.filter(colis => colis!.statusColis !== StatutColis.ANNULER);
    const selectedIds = validColis.map(colis => colis!.id!);

    if (selectedIds.length === 0) {
      this.snackBar.open('Aucun colis sélectionné pour confirmation', 'Fermer', {
        duration: 2000,
      });
      return;
    }

    const confirmRequests = selectedIds.map(id => this.colisService.updateColisStatusToConfirmed(id));

    forkJoin(confirmRequests).subscribe(() => {
      this.selection.clear();
      this.selectedMap.clear();
      this.snackBar.open('Tous les colis sélectionnés ont été confirmés avec succès!', 'Fermer', {
        duration: 2000,
      });
      window.location.reload();
    }, error => {
      console.error('Erreur lors de la confirmation des colis:', error);
      this.snackBar.open('Erreur lors de la confirmation des colis.', 'Fermer', {
        duration: 2000,
      });
    });
  }


  batchDownloadLabel() {
    const selectedColis = Array.from(this.selectedMap.keys())
      .map(id => this.dataSource.data.find(colis => colis.id === id))
      .filter(colis => colis && colis.statusColis !== StatutColis.ANNULER);
    const selectedIds = selectedColis.map(colis => colis!.id!);

    if (selectedIds.length === 0) {
      this.snackBar.open('Aucun colis sélectionné pour le téléchargement', 'Fermer', {
        duration: 2000,
      });
      return;
    }

    this.colisService.downloadMultipleLabels(selectedIds).subscribe(response => {
      const blob = new Blob([response], { type: 'application/pdf' });
      saveAs(blob, 'etiquettes-combinees.pdf');

      // Mettre à jour le statut de chaque colis à "CONFIRMER"
      const confirmRequests = selectedIds.map(id => this.colisService.updateColisStatusToConfirmed(id));
      forkJoin(confirmRequests).subscribe(() => {
        this.selection.clear();
        this.selectedMap.clear();
        this.getColisList(); // Rafraîchir la liste des colis après confirmation
        this.snackBar.open('Tous les colis sélectionnés ont été confirmés et les étiquettes téléchargées avec succès!', 'Fermer', {
          duration: 2000,
        });
        window.location.reload();
      }, error => {
        console.error('Erreur lors de la confirmation des colis:', error);
        this.snackBar.open('Erreur lors de la confirmation des colis.', 'Fermer', {
          duration: 2000,
        });
      });

    }, error => {
      console.error('Erreur lors du téléchargement en masse:', error);
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

  protected readonly StatutColis = StatutColis;
}
