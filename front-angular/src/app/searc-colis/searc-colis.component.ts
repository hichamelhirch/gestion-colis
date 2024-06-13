import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Colis } from '../models/colis.model';

@Component({
  selector: 'app-searc-colis',
  templateUrl: './searc-colis.component.html',
  styleUrls: ['./searc-colis.component.css']
})
export class SearcColisComponent {

  constructor(
    public dialogRef: MatDialogRef<SearcColisComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { colis: Colis, errorMessage: string },
    private router: Router
  ) {}

  onClose(): void {
    this.dialogRef.close();
  }

  voirDetails(): void {
    this.dialogRef.close();
    this.router.navigate(['/home/colis-details', this.data.colis.id]);
  }
}
