import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { FormBuilder, FormGroup } from '@angular/forms';
import { StatutColis } from '../models/colis.model';

@Component({
  selector: 'app-filter-dialog',
  templateUrl: './filter-dialog.component.html',
  styleUrls: ['./filter-dialog.component.css']
})
export class FilterDialogComponent {
  filterForm: FormGroup;

  constructor(
    public dialogRef: MatDialogRef<FilterDialogComponent>,
    private fb: FormBuilder,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.filterForm = this.fb.group({
      confirmed: false,
      draft: false,
      cancelled: false
    });
  }

  onConfirm(): void {
    const selectedStatuses: StatutColis[] = [];
    if (this.filterForm.value.confirmed) selectedStatuses.push(StatutColis.CONFIRMER);
    if (this.filterForm.value.draft) selectedStatuses.push(StatutColis.BROUILLON);
    if (this.filterForm.value.cancelled) selectedStatuses.push(StatutColis.ANNULER);

    this.dialogRef.close(selectedStatuses);
  }

  onCancel(): void {
    this.dialogRef.close();
  }
}
