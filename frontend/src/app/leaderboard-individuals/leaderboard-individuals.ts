import { Component, inject, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Location } from '@angular/common';
import { IndividualLeaderboardDisplayService } from '../services/individual-leaderboard-display-service';
import { TeamLeaderboardDisplayService } from '../services/team-leaderboard-display-service';
import {
  DUMMY_INDIVIDUAL_ROWS,
  IndividualDisplayRow,
} from '../utils/individual-leaderboard-display';

@Component({
  selector: 'app-leaderboard-individuals',
  imports: [RouterLink],
  templateUrl: './leaderboard-individuals.html',
  styleUrl: './leaderboard-individuals.css',
})
export class LeaderboardIndividuals implements OnInit {
  private router = inject(Router);
  private location = inject(Location);
  private individualLeaderboardDisplayService = inject(IndividualLeaderboardDisplayService);
  private teamLeaderboardDisplayService = inject(TeamLeaderboardDisplayService);

  myTeamName = this.teamLeaderboardDisplayService.getDefaultMyTeamName();
  displayIndividuals: IndividualDisplayRow[] = [...DUMMY_INDIVIDUAL_ROWS];

  ngOnInit(): void {
    this.individualLeaderboardDisplayService.loadDisplayIndividuals(true).subscribe({
      next: (individuals) => {
        this.displayIndividuals = individuals;
        this.myTeamName = this.teamLeaderboardDisplayService.getDefaultMyTeamName();
      },
    });
  }

  isMe(individual: IndividualDisplayRow): boolean {
    const riderId = sessionStorage.getItem('currentRiderId');
    const riderName = sessionStorage.getItem('currentRiderName');

    if (riderId && individual.id) {
      return individual.id === riderId;
    }

    return !!riderName && individual.name === riderName;
  }

  goBack() {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/']);
    }
  }
}
