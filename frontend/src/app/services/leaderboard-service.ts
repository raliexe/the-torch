import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Globals } from '../globals/globals';
import { TeamLeaderboard } from '../dto/team/team-leaderboard';
import { RiderContribution } from '../dto/rider/rider-contribution';

@Injectable({
  providedIn: 'root',
})
export class LeaderboardService {
  constructor(
    private httpClient: HttpClient,
    private globals: Globals,
  ) {}

  private get leaderboardBaseURI(): string {
    return `${this.globals.backendUri}/leaderboard`;
  }

  getGlobalLeaderboard(): Observable<TeamLeaderboard[]> {
    return this.httpClient.get<TeamLeaderboard[]>(`${this.leaderboardBaseURI}/teams`);
  }

  getTeamDetailsLeaderboard(teamId: string): Observable<RiderContribution[]> {
    return this.httpClient.get<RiderContribution[]>(`${this.leaderboardBaseURI}/teams/${teamId}/riders`);
  }

  getRiderDetailsLeaderboard(): Observable<RiderContribution[]> {
    return this.httpClient.get<RiderContribution[]>(`${this.leaderboardBaseURI}/riders`);
  }
}
