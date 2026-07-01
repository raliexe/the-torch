import {
  Component,
  ElementRef,
  HostBinding,
  Input,
  OnChanges,
  SimpleChanges,
  ViewChild,
} from '@angular/core';
import { DigitalForestTreeCard } from '../digital-forest-tree-card/digital-forest-tree-card';
import { DigitalForestTreeCardData } from '../digital-forest-tree-card/digital-forest-tree-card.model';
import { TeamDisplayRow } from '../utils/team-leaderboard-display';

type GlideState = 'idle' | 'glide-left' | 'glide-right';

@Component({
  selector: 'app-digital-forest-scene',
  imports: [DigitalForestTreeCard],
  templateUrl: './digital-forest-scene.html',
  styleUrl: './digital-forest-scene.css',
})
export class DigitalForestScene implements OnChanges {
  @ViewChild('carousel') carouselRef?: ElementRef<HTMLElement>;

  @Input({ required: true }) teams: TeamDisplayRow[] = [];
  @Input() embedded = false;

  @HostBinding('class.df-scene-host--embedded')
  get isEmbeddedHost(): boolean {
    return this.embedded;
  }

  carouselIndex = 0;
  animState: GlideState = 'idle';
  transitionEnabled = true;

  private glideBusy = false;
  private completingGlide = false;
  private touchStartX = 0;
  private touchStartY = 0;
  private trackingTouch = false;
  private readonly swipeThresholdPx = 48;

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['teams']) {
      this.syncCarouselIndex();
    }
  }

  get forestTeams(): TeamDisplayRow[] {
    return this.teams;
  }

  get carouselCardCount(): number {
    return this.forestTeams.length;
  }

  get activeTeam(): TeamDisplayRow | null {
    if (!this.forestTeams.length) {
      return null;
    }

    return this.forestTeams[this.carouselIndex] ?? this.forestTeams[0];
  }

  get activeRank(): number {
    if (!this.activeTeam) {
      return 1;
    }

    const index = this.teams.findIndex((team) => team.name === this.activeTeam!.name);
    return index >= 0 ? index + 1 : 1;
  }

  get previousTeam(): TeamDisplayRow | null {
    const count = this.forestTeams.length;
    if (!count || !this.activeTeam) {
      return null;
    }

    const index = (this.carouselIndex - 1 + count) % count;
    return this.forestTeams[index];
  }

  get nextTeamRow(): TeamDisplayRow | null {
    const count = this.forestTeams.length;
    if (!count || !this.activeTeam) {
      return null;
    }

    const index = (this.carouselIndex + 1) % count;
    return this.forestTeams[index];
  }

  get isGliding(): boolean {
    return this.glideBusy;
  }

  toCardData(team: TeamDisplayRow | null | undefined): DigitalForestTreeCardData {
    const safeTeam = team ?? { name: '—', points: 0, distanceKm: 0 };
    const maxPoints = Math.max(...this.teams.map((entry) => entry.points), 1);
    return {
      teamName: safeTeam.name,
      points: safeTeam.points,
      puzzleProgress: Math.min(100, Math.round((safeTeam.points / maxPoints) * 100)),
    };
  }

  formatRank(rank: number): string {
    const mod100 = rank % 100;
    if (mod100 >= 11 && mod100 <= 13) {
      return `${rank}th`;
    }

    switch (rank % 10) {
      case 1:
        return `${rank}st`;
      case 2:
        return `${rank}nd`;
      case 3:
        return `${rank}rd`;
      default:
        return `${rank}th`;
    }
  }

  prevTeam(): void {
    const count = this.forestTeams.length;
    if (!count || count <= 1 || this.glideBusy) {
      return;
    }

    if (this.prefersReducedMotion()) {
      this.carouselIndex = (this.carouselIndex - 1 + count) % count;
      return;
    }

    this.glideBusy = true;
    this.animState = 'glide-left';
  }

  nextTeam(): void {
    const count = this.forestTeams.length;
    if (!count || count <= 1 || this.glideBusy) {
      return;
    }

    if (this.prefersReducedMotion()) {
      this.carouselIndex = (this.carouselIndex + 1) % count;
      return;
    }

    this.glideBusy = true;
    this.animState = 'glide-right';
  }

  onGlideTransitionEnd(event: TransitionEvent): void {
    if (this.animState === 'idle' || this.completingGlide) {
      return;
    }

    const target = event.target as HTMLElement;
    if (!target.classList.contains('df-carousel-slot--pos-center') || event.propertyName !== 'left') {
      return;
    }

    this.completeGlideWithFlip();
  }

  onSceneTouchStart(event: TouchEvent): void {
    const target = event.target as HTMLElement;
    if (target.closest('button, a') || event.touches.length !== 1) {
      this.trackingTouch = false;
      return;
    }

    this.trackingTouch = true;
    this.touchStartX = event.touches[0].clientX;
    this.touchStartY = event.touches[0].clientY;
  }

  onSceneTouchEnd(event: TouchEvent): void {
    if (!this.trackingTouch || this.glideBusy || event.changedTouches.length !== 1) {
      this.trackingTouch = false;
      return;
    }

    this.trackingTouch = false;
    const touch = event.changedTouches[0];
    const deltaX = touch.clientX - this.touchStartX;
    const deltaY = touch.clientY - this.touchStartY;

    if (Math.abs(deltaX) < this.swipeThresholdPx || Math.abs(deltaX) <= Math.abs(deltaY)) {
      return;
    }

    if (deltaX < 0) {
      this.nextTeam();
    } else {
      this.prevTeam();
    }
  }

  private completeGlideWithFlip(): void {
    const carousel = this.carouselRef?.nativeElement;
    if (!carousel) {
      this.finishGlideInstantly();
      return;
    }

    const slots = Array.from(carousel.querySelectorAll<HTMLElement>('.df-carousel-slot'));
    if (slots.length === 0) {
      this.finishGlideInstantly();
      return;
    }

    this.completingGlide = true;
    const firstRects = slots.map((slot) => slot.getBoundingClientRect());
    const direction = this.animState;

    this.transitionEnabled = false;
    this.animState = 'idle';

    const count = this.forestTeams.length;
    if (direction === 'glide-left') {
      this.carouselIndex = (this.carouselIndex - 1 + count) % count;
    } else {
      this.carouselIndex = (this.carouselIndex + 1) % count;
    }

    void carousel.offsetHeight;

    const lastRects = slots.map((slot) => slot.getBoundingClientRect());

    slots.forEach((slot, index) => {
      const first = firstRects[index];
      const last = lastRects[index];
      const dx = first.left + first.width / 2 - (last.left + last.width / 2);
      const dy = first.top + first.height / 2 - (last.top + last.height / 2);
      const sx = last.width > 0 ? first.width / last.width : 1;
      const sy = last.height > 0 ? first.height / last.height : 1;

      slot.style.transformOrigin = 'center center';
      slot.style.transform = `translate(${dx}px, ${dy}px) scale(${sx}, ${sy})`;
    });

    void carousel.offsetHeight;
    this.transitionEnabled = true;

    slots.forEach((slot) => {
      slot.classList.add('df-carousel-slot--flip');
      slot.style.transform = '';
    });

    const centerSlot = slots.find((slot) => slot.classList.contains('df-carousel-slot--pos-center'));
    if (!centerSlot) {
      this.cleanupFlip(slots);
      return;
    }

    const finishFlip = () => {
      centerSlot.removeEventListener('transitionend', onFlipEnd);
      window.clearTimeout(fallbackId);
      this.cleanupFlip(slots);
    };

    const onFlipEnd = (event: TransitionEvent) => {
      if (event.target !== centerSlot || event.propertyName !== 'transform') {
        return;
      }

      finishFlip();
    };

    const fallbackId = window.setTimeout(finishFlip, 450);
    centerSlot.addEventListener('transitionend', onFlipEnd);
  }

  private cleanupFlip(slots: HTMLElement[]): void {
    slots.forEach((slot) => {
      slot.classList.remove('df-carousel-slot--flip');
      slot.style.transform = '';
      slot.style.transformOrigin = '';
    });

    this.completingGlide = false;
    this.glideBusy = false;
  }

  private finishGlideInstantly(): void {
    const count = this.forestTeams.length;
    if (this.animState === 'glide-left') {
      this.carouselIndex = (this.carouselIndex - 1 + count) % count;
    } else if (this.animState === 'glide-right') {
      this.carouselIndex = (this.carouselIndex + 1) % count;
    }

    this.animState = 'idle';
    this.glideBusy = false;
    this.completingGlide = false;
  }

  private syncCarouselIndex(): void {
    this.carouselIndex = 0;
  }

  private prefersReducedMotion(): boolean {
    return typeof window !== 'undefined' && window.matchMedia('(prefers-reduced-motion: reduce)').matches;
  }
}
