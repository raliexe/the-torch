import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Location } from '@angular/common';
import { RiderContribution } from '../dto/rider/rider-contribution';
import { TeamLeaderboard } from '../dto/team/team-leaderboard';
import { LeaderboardService } from '../services/leaderboard-service';
import { RiderService } from '../services/rider-service';
import { TeamService } from '../services/team-service';

@Component({
  selector: 'app-team',
  imports: [RouterLink],
  templateUrl: './team.html',
  styleUrl: './team.css',
})
export class Team implements OnInit {
  /** Hardcoded demo rider – team is resolved from this rider, never from the URL. */
  private readonly DEMO_RIDER_NAME = 'Steve';

  private teamId = '';

  private router = inject(Router);
  private location = inject(Location);

  teamMembers: RiderContribution[] = [];

  teamName = 'Team Rockets';
  teamRankLabel = 'Rank #2 (This Week)';
  teamBadgeId = 'TR-2026';
  teamPointsDisplay = '1,250';
  progressPercent = 68.5;

  readonly fallbackMembers = [
    { name: 'Victor', points: 320 },
    { name: 'Ehsan', points: 280 },
    { name: 'Filip', points: 250 },
    { name: 'Ralitsa', points: 230 },
    { name: 'Simin', points: 200 },
  ];

  constructor(
    private leaderboardService: LeaderboardService,
    private riderService: RiderService,
    private teamService: TeamService,
  ) {}

  ngOnInit(): void {
    this.riderService.getRider(this.DEMO_RIDER_NAME).subscribe({
      next: (rider) => {
        this.teamService.getTeamForMember(rider.id).subscribe({
          next: (team: TeamLeaderboard) => {
            this.teamId = team.teamId;
            this.teamName = team.teamName || this.teamName;
            this.teamPointsDisplay = Math.round(team.totalPoints).toLocaleString();
            this.loadTeamMembers();
          },
          error: (err) => console.error('Failed to load team for member:', err),
        });
      },
      error: (err) => console.error('Failed to load demo rider:', err),
    });
  }

  loadTeamMembers(): void {
    if (!this.teamId) {
      return;
    }

    this.leaderboardService.getTeamDetailsLeaderboard(this.teamId).subscribe({
      next: (data: RiderContribution[]) => {
        this.teamMembers = data;
      },
      error: (err) => {
        console.error('Failed to load team details:', err);
      },
    });
  }

  goBack() {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/leaderboard']);
    }
  }
}
