import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ProfileService } from '../services/profile.service';

@Component({
  selector: 'app-email-composer',
  templateUrl: './email-composer.component.html',
  styleUrls: ['./email-composer.component.css']
})
export class EmailComposerComponent implements OnInit {
  emailForm: FormGroup;
  userEmail: string = '';

  constructor(
    private fb: FormBuilder,
    private profileService: ProfileService
  ) {
    this.emailForm = this.fb.group({
      subject: ['', Validators.required],
      message: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.getUserEmail();
  }

  getUserEmail(): void {
    this.profileService.getProfile().subscribe(
      profile => {
        console.log('Profile:', profile); // Debugging line
        this.userEmail = profile.email;
      },
      error => {
        console.error('Error fetching profile:', error);
      }
    );
  }

  sendEmail(): void {
    if (this.emailForm.valid) {
      const { subject, message } = this.emailForm.value;
      const mailtoUrl = `mailto:hichamelhirch2001@gmail.com?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(`From: ${this.userEmail}\n\n${message}`)}`;
      window.location.href = mailtoUrl;
    }
  }
}
