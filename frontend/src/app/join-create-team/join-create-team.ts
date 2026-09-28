import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Location } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { switchMap } from 'rxjs';
import { RiderService } from '../services/rider-service';
import { TeamService } from '../services/team-service';
import { persistTeamSession } from '../utils/team-session';

export type TeamFlowMode = 'join' | 'create';

@Component({
  selector: 'app-join-create-team',
  imports: [RouterLink, FormsModule],
  templateUrl: './join-create-team.html',
  styleUrl: './join-create-team.css',
})
export class JoinCreateTeam implements OnInit {
  private router = inject(Router);
  private location = inject(Location);
  private riderService = inject(RiderService);
  private teamService = inject(TeamService);

  mode: TeamFlowMode = 'create';
  teamCode = '';
  errorMessage = '';
  isSubmitting = false;

  ngOnInit(): void {
    const riderId = sessionStorage.getItem('currentRiderId');
    if (!riderId) {
      return;
    }

    this.teamService.getTeamForMember(riderId).subscribe({
      next: (team) => {
        persistTeamSession(team);
        this.router.navigate(['/home'], { replaceUrl: true });
      },
    });
  }

  selectMode(next: TeamFlowMode): void {
    this.mode = next;
    this.errorMessage = '';
  }

  submit(): void {
    const value = this.teamCode.trim();
    if (!value || this.isSubmitting) {
      return;
    }

    if (this.mode === 'create') {
      this.createTeam(value);
      return;
    }

    this.errorMessage =
      'Join team is not connected yet. Select Create Team and enter a team name.';
  }

  private createTeam(teamName: string): void {
    const riderId = sessionStorage.getItem('currentRiderId');
    const riderName = sessionStorage.getItem('currentRiderName');

    if (!riderId && !riderName) {
      this.errorMessage = 'Please log in or register before creating a team.';
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';

    const create$ = riderId
      ? this.teamService.createTeam({ name: teamName, ownerId: riderId })
      : this.riderService.getRider(riderName!).pipe(
          switchMap((rider) => {
            sessionStorage.setItem('currentRiderId', rider.id);
            return this.teamService.createTeam({
              name: teamName,
              ownerId: rider.id,
            });
          }),
        );

    create$.subscribe({
      next: (team) => {
        persistTeamSession(team);
        this.router.navigate(['/home']);
      },
      error: (err) => {
        this.isSubmitting = false;

        if (err.status === 409) {
          this.router.navigate(['/home'], { replaceUrl: true });
          return;
        }

        const apiMessage = err.error?.message;
        this.errorMessage =
          apiMessage ??
          (err.status === 404
            ? 'Account not found. Please log in again.'
            : 'Could not create the team. Please try again.');
      },
    });
  }

  goBack(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/home']);
    }
  }
}
