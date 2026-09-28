import { Component, Input } from '@angular/core';
import {
  DigitalForestTreeDisplay,
  puzzleProgressTreeDisplay,
  puzzleProgressTreeImageSrc,
} from '../utils/digital-forest-tree-stage';
import { DigitalForestTreeCardVariant } from './digital-forest-tree-card.model';

@Component({
  selector: 'app-digital-forest-tree-card',
  templateUrl: './digital-forest-tree-card.html',
  styleUrl: './digital-forest-tree-card.css',
})
export class DigitalForestTreeCard {
  @Input({ required: true }) teamName!: string;
  @Input() points = 0;
  @Input() puzzleProgress = 0;
  @Input() variant: DigitalForestTreeCardVariant = 'active';

  get treeImageSrc(): string {
    return puzzleProgressTreeImageSrc(this.puzzleProgress);
  }

  get treeDisplay(): DigitalForestTreeDisplay {
    return puzzleProgressTreeDisplay(this.puzzleProgress);
  }
}
