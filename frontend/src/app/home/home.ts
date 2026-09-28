import { Component, HostListener, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { DigitalForestScene } from '../digital-forest-scene/digital-forest-scene';
import { TeamLeaderboardDisplayService } from '../services/team-leaderboard-display-service';
import { TeamDisplayRow } from '../utils/team-leaderboard-display';

type HomeTab = 'forest' | 'leaderboard';

@Component({
  selector: 'app-home',
  imports: [CommonModule, RouterLink, DigitalForestScene],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home implements OnInit {
  private readonly teamLeaderboardDisplayService = inject(TeamLeaderboardDisplayService);
  private readonly route = inject(ActivatedRoute);

  activeTab: HomeTab = 'forest';
  displayTeams: TeamDisplayRow[] = [];
  torchOverlayOpen = false;
  lightOverlayOpen = false;

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      this.activeTab = params.get('forest') ? 'forest' : this.activeTab;
    });

    this.teamLeaderboardDisplayService.loadDisplayTeams(true, 'full').subscribe({
      next: (teams) => {
        this.displayTeams = [...teams].sort((a, b) => b.points - a.points);
      },
    });
  }

  get leaderboardTeams(): TeamDisplayRow[] {
    return this.displayTeams.slice(0, 9);
  }

  get ctaRoute(): string {
    return this.activeTab === 'forest' ? '/digital-forest' : '/leaderboard';
  }

  setTab(tab: HomeTab): void {
    this.activeTab = tab;
  }

  isMyTeam(teamName: string): boolean {
    const myTeamName = sessionStorage.getItem('currentTeamName');
    return !!myTeamName && teamName === myTeamName;
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
