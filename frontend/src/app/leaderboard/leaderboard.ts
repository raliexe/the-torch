import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Location } from '@angular/common';
import { TeamLeaderboardDisplayService } from '../services/team-leaderboard-display-service';
import { DUMMY_TEAM_ROWS, TeamDisplayRow } from '../utils/team-leaderboard-display';

@Component({
  selector: 'app-leaderboard',
  imports: [RouterLink],
  templateUrl: './leaderboard.html',
  styleUrl: './leaderboard.css',
})
export class Leaderboard implements OnInit {
  private router = inject(Router);
  private location = inject(Location);
  private teamLeaderboardDisplayService = inject(TeamLeaderboardDisplayService);

  myTeamName = this.teamLeaderboardDisplayService.getDefaultMyTeamName();
  readonly dummyTeams = DUMMY_TEAM_ROWS;
  displayTeams: TeamDisplayRow[] = [...DUMMY_TEAM_ROWS];

  ngOnInit(): void {
    this.teamLeaderboardDisplayService.loadDisplayTeams(true, 'full').subscribe({
      next: (teams) => {
        this.displayTeams = teams;
        this.myTeamName = this.teamLeaderboardDisplayService.getDefaultMyTeamName();
      },
    });
  }

  isMyTeam(teamName: string): boolean {
    const myTeamName = sessionStorage.getItem('currentTeamName');
    return !!myTeamName && teamName === myTeamName;
  }

  goBack() {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/']);
    }
  }
}
