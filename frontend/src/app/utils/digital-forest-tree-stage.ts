/** Figma 1109-399 growth stages: image 72 → 0%, image 73 → 10%, … image 82 → 100%. */
export const DIGITAL_FOREST_TREE_STAGES = [0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100] as const;

export type DigitalForestTreeStage = (typeof DIGITAL_FOREST_TREE_STAGES)[number];

/** Natural pixel sizes from Figma 1109-399 exports (image 72–82). */
export const DIGITAL_FOREST_TREE_STAGE_WIDTHS: Record<DigitalForestTreeStage, number> = {
  0: 155,
  10: 206,
  20: 259,
  30: 263,
  40: 241,
  50: 264,
  60: 247,
  70: 272,
  80: 279,
  90: 285,
  100: 372,
};

export const DIGITAL_FOREST_TREE_STAGE_HEIGHTS: Record<DigitalForestTreeStage, number> = {
  0: 270,
  10: 321,
  20: 352,
  30: 384,
  40: 428,
  50: 428,
  60: 350,
  70: 382,
  80: 403,
  90: 403,
  100: 438,
};

export interface DigitalForestTreeDisplay {
  slotWidthPercent: number;
  aspectWidth: number;
  aspectHeight: number;
}

export function puzzleProgressToTreeStage(progress: number): DigitalForestTreeStage {
  const clamped = Math.min(100, Math.max(0, Math.round(progress)));
  const stageIndex = Math.round(clamped / 10);
  return DIGITAL_FOREST_TREE_STAGES[stageIndex];
}

export function treeStageImageSrc(stage: DigitalForestTreeStage): string {
  return `assets/images/digital-forest-tree-stage-${stage}.png`;
}

export function puzzleProgressTreeImageSrc(progress: number): string {
  return treeStageImageSrc(puzzleProgressToTreeStage(progress));
}

/** Preserve Figma-relative tree sizes; full-grown tree fills the card tree slot width. */
export function puzzleProgressTreeDisplay(progress: number): DigitalForestTreeDisplay {
  const stage = puzzleProgressToTreeStage(progress);
  const refWidth = DIGITAL_FOREST_TREE_STAGE_WIDTHS[100];
  return {
    slotWidthPercent: (DIGITAL_FOREST_TREE_STAGE_WIDTHS[stage] / refWidth) * 100,
    aspectWidth: DIGITAL_FOREST_TREE_STAGE_WIDTHS[stage],
    aspectHeight: DIGITAL_FOREST_TREE_STAGE_HEIGHTS[stage],
  };
}
