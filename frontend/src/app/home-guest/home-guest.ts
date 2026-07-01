import { Component, HostListener, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { DigitalForestScene } from '../digital-forest-scene/digital-forest-scene';
import { TeamLeaderboardDisplayService } from '../services/team-leaderboard-display-service';
import { DUMMY_TEAM_ROWS, TeamDisplayRow } from '../utils/team-leaderboard-display';

type HomeTab = 'forest' | 'leaderboard';

@Component({
  selector: 'app-home-guest',
  imports: [CommonModule, RouterLink, DigitalForestScene],
  templateUrl: './home-guest.html',
  styleUrls: ['./home-guest.css', '../home/home.css'],
})
export class HomeGuest implements OnInit {
  private readonly teamLeaderboardDisplayService = inject(TeamLeaderboardDisplayService);

  activeTab: HomeTab = 'forest';
  displayTeams: TeamDisplayRow[] = [...DUMMY_TEAM_ROWS].sort((a, b) => b.points - a.points);
  torchOverlayOpen = false;
  lightOverlayOpen = false;

  ngOnInit(): void {
    this.teamLeaderboardDisplayService.loadDisplayTeams(false, 'full').subscribe({
      next: (teams) => {
        this.displayTeams = [...teams].sort((a, b) => b.points - a.points);
      },
    });
  }

  get leaderboardTeams(): TeamDisplayRow[] {
    return this.displayTeams.slice(0, 9);
  }

  get ctaRoute(): string {
    return '/landing';
  }

  setTab(tab: HomeTab): void {
    this.activeTab = tab;
  }

  openTorchOverlay(): void {
    this.lightOverlayOpen = false;
    this.torchOverlayOpen = true;
  }

  closeTorchOverlay(): void {
    this.torchOverlayOpen = false;
  }

  openLightOverlay(): void {
    this.torchOverlayOpen = false;
    this.lightOverlayOpen = true;
  }

  closeLightOverlay(): void {
    this.lightOverlayOpen = false;
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    if (this.torchOverlayOpen) {
      this.closeTorchOverlay();
    } else if (this.lightOverlayOpen) {
      this.closeLightOverlay();
    }
  }
}
