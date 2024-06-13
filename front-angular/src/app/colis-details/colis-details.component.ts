import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ColisService } from '../services/colis.service';
import { Colis } from '../models/colis.model';
import { jsPDF } from 'jspdf';
import html2canvas from 'html2canvas';

@Component({
  selector: 'app-colis-details',
  templateUrl: './colis-details.component.html',
  styleUrls: ['./colis-details.component.css']
})
export class ColisDetailsComponent implements OnInit {
  colis: Colis | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private colisService: ColisService
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.colisService.getColisById(+id).subscribe(
        (colis: Colis) => {
          this.colis = colis;
        },
        error => {
          console.error('Erreur lors de la récupération des détails du colis', error);
        }
      );
    }
  }

  downloadPDF(): void {
    const element = document.getElementById('colis-details');
    if (element) {
      // Cacher le bouton avant de capturer l'image
      const button = element.querySelector('button');
      if (button) {
        button.style.display = 'none';
      }

      html2canvas(element).then((canvas) => {
        const imgData = canvas.toDataURL('image/png');
        const pdf = new jsPDF();
        const imgProps = pdf.getImageProperties(imgData);
        const pdfWidth = pdf.internal.pageSize.getWidth();
        const pdfHeight = (imgProps.height * pdfWidth) / imgProps.width;

        // Ajouter un message professionnel en haut
        // @ts-ignore
        pdf.text('Rapport de Détails du Colis', pdfWidth / 2, 10, { align: 'center' });
        pdf.addImage(imgData, 'PNG', 0, 20, pdfWidth, pdfHeight);
        pdf.save(`Colis_${this.colis?.numeroColis}.pdf`);

        // Réafficher le bouton après la capture
        if (button) {
          button.style.display = 'block';
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/home/colislist']);
  }
}
