import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Location } from '@angular/common';
import { DigitalForestScene } from '../digital-forest-scene/digital-forest-scene';
import { TeamLeaderboardDisplayService } from '../services/team-leaderboard-display-service';
import { DUMMY_TEAM_ROWS, TeamDisplayRow } from '../utils/team-leaderboard-display';

@Component({
  selector: 'app-digital-forest',
  imports: [RouterLink, DigitalForestScene],
  templateUrl: './digital-forest.html',
  styleUrl: './digital-forest.css',
})
export class DigitalForest implements OnInit {
  private readonly router = inject(Router);
  private readonly location = inject(Location);
  private readonly teamLeaderboardDisplayService = inject(TeamLeaderboardDisplayService);

  displayTeams: TeamDisplayRow[] = [...DUMMY_TEAM_ROWS];

  ngOnInit(): void {
    this.teamLeaderboardDisplayService.loadDisplayTeams(true, 'full').subscribe({
      next: (teams) => {
        this.displayTeams = [...teams].sort((a, b) => b.points - a.points);
      },
    });
  }

  goBack(): void {
    if (window.history.length > 1) {
      this.location.back();
      return;
    }

    this.router.navigate(['/home']);
  }
}
