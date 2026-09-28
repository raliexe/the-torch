import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map, startWith } from 'rxjs/operators';
import { TeamLeaderboard } from '../dto/team/team-leaderboard';
import { LeaderboardService } from './leaderboard-service';
import { TeamService } from './team-service';
import {
  buildHomePreviewRows,
  buildLoggedInTeamRows,
  DUMMY_TEAM_ROWS,
  readStoredMyTeam,
  TeamDisplayRow,
} from '../utils/team-leaderboard-display';

export type TeamLeaderboardDisplayMode = 'preview' | 'full';

@Injectable({
  providedIn: 'root',
})
export class TeamLeaderboardDisplayService {
  constructor(
    private teamService: TeamService,
    private leaderboardService: LeaderboardService,
  ) {}

  loadDisplayTeams(
    includeMyTeam: boolean,
    mode: TeamLeaderboardDisplayMode = 'full',
  ): Observable<TeamDisplayRow[]> {
    if (!includeMyTeam) {
      return of([...DUMMY_TEAM_ROWS]);
    }

    const storedTeam = readStoredMyTeam();
    const buildRows =
      mode === 'preview'
        ? (team: TeamLeaderboard | null) => buildHomePreviewRows(team)
        : (team: TeamLeaderboard | null) => buildLoggedInTeamRows(team);
    const initialTeams = buildRows(storedTeam);

    return this.fetchMyTeam().pipe(
      map((team) => buildRows(team)),
      startWith(initialTeams),
      catchError(() => of(initialTeams)),
    );
  }

  getDefaultMyTeamName(): string {
    return sessionStorage.getItem('currentTeamName') ?? 'Team Rockets';
  }

  private fetchMyTeam(): Observable<TeamLeaderboard | null> {
    const riderId = sessionStorage.getItem('currentRiderId');
    const teamId = sessionStorage.getItem('currentTeamId');

    if (riderId) {
      return this.teamService.getTeamForMember(riderId).pipe(
        map((team) => {
          this.persistTeam(team);
          return team;
        }),
        catchError(() => this.fetchFromGlobalLeaderboard(teamId)),
      );
    }

    return this.fetchFromGlobalLeaderboard(teamId);
  }

  private fetchFromGlobalLeaderboard(teamId: string | null): Observable<TeamLeaderboard | null> {
    if (!teamId && !sessionStorage.getItem('currentTeamName')) {
      return of(null);
    }

    return this.leaderboardService.getGlobalLeaderboard().pipe(
      map((teams) => {
        const storedName = sessionStorage.getItem('currentTeamName');
        const team =
          (teamId ? teams.find((entry) => entry.teamId === teamId) : undefined) ??
          (storedName ? teams.find((entry) => entry.teamName === storedName) : undefined) ??
          null;

        if (team) {
          this.persistTeam(team);
        }

        return team;
      }),
      catchError(() => of(readStoredMyTeam())),
    );
  }

  private persistTeam(
    team: Pick<TeamLeaderboard, 'teamId' | 'teamName' | 'totalPoints' | 'totalDistance'>,
  ): void {
    sessionStorage.setItem('currentTeamName', team.teamName);

    if (team.teamId) {
      sessionStorage.setItem('currentTeamId', team.teamId);
    }
  }
}
