import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { Location } from '@angular/common';

interface PuzzlePiece {
  id: number;
  unlocked: boolean;
}

@Component({
  selector: 'app-puzzle-tree',
  imports: [RouterLink],
  templateUrl: './puzzle-tree.html',
  styleUrl: './puzzle-tree.css',
})
export class PuzzleTree {
  private router = inject(Router);
  private location = inject(Location);

  /** 3×4 puzzle grid – pieces 1–8 start revealed, 9–12 start as shadow milestones. */
  pieces: PuzzlePiece[] = [
    { id: 1, unlocked: true },
    { id: 2, unlocked: true },
    { id: 3, unlocked: true },
    { id: 4, unlocked: true },
    { id: 5, unlocked: true },
    { id: 6, unlocked: true },
    { id: 7, unlocked: true },
    { id: 8, unlocked: true },
    { id: 9, unlocked: false },
    { id: 10, unlocked: false },
    { id: 11, unlocked: false },
    { id: 12, unlocked: false },
  ];

  todayPoints = 90;
  readonly rewardPoints = 150;
  readonly pointsPerPiece = Math.round(this.rewardPoints / 4);

  get unlockedCount(): number {
    return this.pieces.filter((piece) => piece.unlocked).length;
  }

  get progressPercent(): number {
    return Math.round((this.unlockedCount / this.pieces.length) * 100);
  }

  get progressRingStyle(): string {
    const angle = (this.progressPercent / 100) * 360;
    return `conic-gradient(#297B00 ${angle}deg, transparent ${angle}deg)`;
  }

  pieceGridArea(id: number): string {
    const col = ((id - 1) % 3) + 1;
    const row = Math.floor((id - 1) / 3) + 1;
    return `${row} / ${col} / ${row + 1} / ${col + 1}`;
  }

  unlockPiece(piece: PuzzlePiece): void {
    if (piece.unlocked) {
      return;
    }

    piece.unlocked = true;
    this.todayPoints += this.pointsPerPiece;
  }

  goBack(): void {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/team']);
    }
  }
}
