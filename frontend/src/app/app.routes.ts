import { Routes } from '@angular/router';
import { authGuard } from './utils/auth.guard';
import { noTeamGuard } from './utils/no-team.guard';
import { Home } from './home/home';
import { Landing } from './landing/landing';
import { Register } from './register/register';
import { Login } from './login/login';
import { Profile } from './profile/profile';
import { Leaderboard } from './leaderboard/leaderboard';
import { LeaderboardIndividuals } from './leaderboard-individuals/leaderboard-individuals';
import { Team } from './team/team';
import { PuzzleTree } from './puzzle-tree/puzzle-tree';
import { HomeGuest } from './home-guest/home-guest';
import { JoinCreateTeam } from './join-create-team/join-create-team';
import { DigitalForest } from './digital-forest/digital-forest';

export const routes: Routes = [
  { path: '', redirectTo: 'home-guest', pathMatch: 'full' },
  { path: 'home-guest', component: HomeGuest },
  { path: 'home', component: Home, canActivate: [authGuard] },
  { path: 'landing', component: Landing },
  { path: 'register', component: Register },
  { path: 'login', component: Login },
  { path: 'profile', component: Profile, canActivate: [authGuard] },
  { path: 'leaderboard', component: Leaderboard, canActivate: [authGuard] },
  { path: 'individuals', component: LeaderboardIndividuals, canActivate: [authGuard] },
  { path: 'team', component: Team, canActivate: [authGuard] },
  { path: 'join-team', component: JoinCreateTeam, canActivate: [authGuard, noTeamGuard] },
  { path: 'puzzle-tree', component: PuzzleTree, canActivate: [authGuard] },
  { path: 'digital-forest', component: DigitalForest, canActivate: [authGuard] },
];
