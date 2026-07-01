import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { TeamLeaderboard } from '../dto/team/team-leaderboard';
import { TeamService } from '../services/team-service';
import { normalizeTeamLeaderboard } from './team-leaderboard-display';

export function persistTeamSession(
  team: Partial<TeamLeaderboard> & { id?: string; name?: string },
): void {
  const normalized = normalizeTeamLeaderboard(team);
  if (!normalized) {
    return;
  }

  sessionStorage.setItem('currentTeamName', normalized.teamName);

  if (normalized.teamId) {
    sessionStorage.setItem('currentTeamId', normalized.teamId);
  }
}

export function navigateAfterLogin(router: Router, teamService: TeamService): void {
  const riderId = sessionStorage.getItem('currentRiderId');

  if (!riderId) {
    router.navigate(['/join-team']);
    return;
  }

  teamService.getTeamForMember(riderId).subscribe({
    next: (team) => {
      persistTeamSession(team);
      router.navigate(['/home']);
    },
    error: (err: HttpErrorResponse) => {
      if (err.status === 404) {
        router.navigate(['/join-team']);
        return;
      }

      router.navigate(['/home']);
    },
  });
}
