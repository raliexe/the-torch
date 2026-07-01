import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { Location } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { RiderService } from '../services/rider-service';
import { TeamService } from '../services/team-service';
import { navigateAfterLogin } from '../utils/team-session';

@Component({
  selector: 'app-login',
  imports: [RouterLink, FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  private router = inject(Router);
  private location = inject(Location);
  private riderService = inject(RiderService);
  private teamService = inject(TeamService);

  errorMessage = '';
  isSubmitting = false;

  onLogin(form: NgForm) {
    if (form.invalid || this.isSubmitting) {
      return;
    }

    const studentMail = (form.value.studentMail as string).trim();
    const password = form.value.password as string;

    this.isSubmitting = true;
    this.errorMessage = '';

    this.riderService.loginRider(studentMail, password).subscribe({
      next: (rider) => {
        sessionStorage.setItem('currentRiderName', rider.name);
        sessionStorage.setItem('currentRiderId', rider.id);
        navigateAfterLogin(this.router, this.teamService);
      },
      error: (err) => {
        this.isSubmitting = false;
        if (err.status === 401) {
          this.errorMessage = 'Incorrect password. Please try again.';
        } else if (err.status === 404) {
          this.errorMessage = 'No account found for this student mail.';
        } else {
          this.errorMessage = 'Something went wrong. Please try again.';
        }
      },
    });
  }

  goBack() {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/']);
    }
  }
}
