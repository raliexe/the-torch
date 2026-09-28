import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Globals } from '../globals/globals';
import { TeamLeaderboard } from '../dto/team/team-leaderboard';
import { CreateTeamRequest } from '../dto/team/create-team-request';
import { CreatedTeam } from '../dto/team/created-team';

@Injectable({
  providedIn: 'root',
})
export class TeamService {
  constructor(
    private httpClient: HttpClient,
    private globals: Globals,
  ) {}

  private get teamBaseURI(): string {
    return `${this.globals.backendUri}/teams`;
  }

  getTeamForMember(riderId: string): Observable<TeamLeaderboard> {
    return this.httpClient.get<TeamLeaderboard>(`${this.teamBaseURI}/member/${riderId}`);
  }

  createTeam(request: CreateTeamRequest): Observable<CreatedTeam> {
    return this.httpClient.post<CreatedTeam>(this.teamBaseURI, request);
  }
}
