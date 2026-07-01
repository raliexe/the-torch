import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Location } from '@angular/common';
import { TeamLeaderboard } from '../dto/team/team-leaderboard';
import { Rider } from '../dto/rider/rider';
import { RiderService } from '../services/rider-service';
import { TeamService } from '../services/team-service';
import { clearAuthSession } from '../utils/auth-session';

@Component({
  selector: 'app-profile',
  imports: [RouterLink],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile {
  private router = inject(Router);
  private location = inject(Location);
  currRider!: Rider;
  myTeam!: TeamLeaderboard;
  teamProgressPercent: number = 21;

  constructor(private riderService: RiderService,
              private teamService: TeamService) {}

  ngOnInit(): void {
    this.loadRider();
  }

  loadRider(): void {
    this.riderService.getRider("Steve").subscribe({
      next: (data: Rider) => {
        this.currRider = data;
        this.loadMyTeam();
      },
      error: (err) => {
        console.error('Failed to load current rider data:', err);
      },
    });
  }

  loadMyTeam(): void {
    this.teamService.getTeamForMember(this.currRider.id).subscribe({
      next: (data: TeamLeaderboard) => {
        this.myTeam = data;
      },
      error: (err) => {
        console.error('Failed to load team for member data:', err);
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

  logOut(): void {
    clearAuthSession();
    this.router.navigateByUrl('/home-guest', { replaceUrl: true });
  }
}
