import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { Location } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';
import { timeout } from 'rxjs';
import { RiderService } from '../services/rider-service';

@Component({
  selector: 'app-register',
  imports: [RouterLink, FormsModule],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class Register {
  private router = inject(Router);
  private location = inject(Location);
  private riderService = inject(RiderService);

  errorMessage = '';
  isSubmitting = false;

  onRegister(form: NgForm) {
    if (form.invalid || this.isSubmitting) {
      return;
    }

    const studentMail = (form.value.studentMail as string).trim();
    const createPassword = form.value.createPassword as string;
    const repeatPassword = form.value.repeatPassword as string;

    if (createPassword !== repeatPassword) {
      this.errorMessage = 'Passwords do not match.';
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    this.riderService
      .createRider({
        name: studentMail,
        tuidToken: createPassword,
        age: 25,
        gender: 'M',
      })
      .pipe(timeout(20000))
      .subscribe({
        next: (rider) => {
          sessionStorage.setItem('currentRiderName', rider.name);
          sessionStorage.setItem('currentRiderId', rider.id);
          this.router.navigate(['/join-team']);
        },
        error: (err) => {
          this.isSubmitting = false;
          const apiMessage = err.error?.message;
          this.errorMessage =
            err.name === 'TimeoutError'
              ? 'Request timed out. Make sure the backend is running and try again.'
              : apiMessage ??
                (err.status === 400
                  ? 'Registration failed. Check your student mail and try again.'
                  : 'Something went wrong. Please try again.');
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
