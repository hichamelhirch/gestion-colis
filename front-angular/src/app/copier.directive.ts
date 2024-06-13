import { Directive, Input, HostListener } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

@Directive({
  selector: '[appCopier]'
})
export class CopierDirective {
  @Input('appCopier') textToCopy!: string;

  constructor(private snackBar: MatSnackBar) {}

  @HostListener('click') onClick() {
    navigator.clipboard.writeText(this.textToCopy).then(() => {
      this.snackBar.open('Code-barres copié!', 'Fermer', {
        duration: 2000,
      });
    }).catch(err => {
      console.error('Échec de la copie : ', err);
    });
  }
}
