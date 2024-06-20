import { Component, Output, EventEmitter, OnInit } from '@angular/core';

@Component({
  selector: 'app-rating',
  templateUrl: './rating.component.html',
  styleUrls: ['./rating.component.css']
})
export class RatingComponent implements OnInit {
  @Output() rated = new EventEmitter<number>();
  stars: boolean[] = [false, false, false, false, false];
  showRating: boolean = false;

  ngOnInit(): void {
    setInterval(() => {
      this.showRating = Math.random() > 0.5;
    }, 30000); // 30 seconds
  }

  rate(rating: number): void {
    this.stars = this.stars.map((_, index) => index < rating);
    this.rated.emit(rating);
    localStorage.setItem('userRating', rating.toString());
    this.showRating = false;
  }
}
