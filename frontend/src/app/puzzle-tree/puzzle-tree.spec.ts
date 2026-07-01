import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { PuzzleTree } from './puzzle-tree';

describe('PuzzleTree', () => {
  let component: PuzzleTree;
  let fixture: ComponentFixture<PuzzleTree>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PuzzleTree],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(PuzzleTree);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should start with eight revealed and four locked pieces', () => {
    expect(component.unlockedCount).toBe(8);
    expect(component.pieces.filter((piece) => !piece.unlocked).length).toBe(4);
  });

  it('should unlock a shadow piece and add points on tap', () => {
    const locked = component.pieces.find((piece) => !piece.unlocked)!;
    const startingPoints = component.todayPoints;

    component.unlockPiece(locked);

    expect(locked.unlocked).toBe(true);
    expect(component.todayPoints).toBe(startingPoints + component.pointsPerPiece);
  });

  it('should ignore taps on already revealed pieces', () => {
    const revealed = component.pieces.find((piece) => piece.unlocked)!;
    const startingPoints = component.todayPoints;

    component.unlockPiece(revealed);

    expect(component.todayPoints).toBe(startingPoints);
  });
});
