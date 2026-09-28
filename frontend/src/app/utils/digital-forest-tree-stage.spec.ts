import {
  puzzleProgressToTreeStage,
  puzzleProgressTreeDisplay,
  puzzleProgressTreeImageSrc,
  treeStageImageSrc,
} from './digital-forest-tree-stage';

describe('digital-forest-tree-stage', () => {
  it('maps progress to the nearest 10% stage', () => {
    expect(puzzleProgressToTreeStage(0)).toBe(0);
    expect(puzzleProgressToTreeStage(4)).toBe(0);
    expect(puzzleProgressToTreeStage(5)).toBe(10);
    expect(puzzleProgressToTreeStage(50)).toBe(50);
    expect(puzzleProgressToTreeStage(96)).toBe(100);
    expect(puzzleProgressToTreeStage(150)).toBe(100);
  });

  it('returns stage image paths', () => {
    expect(treeStageImageSrc(70)).toBe('assets/images/digital-forest-tree-stage-70.png');
    expect(puzzleProgressTreeImageSrc(73)).toBe('assets/images/digital-forest-tree-stage-70.png');
  });

  it('scales tree display relative to the full-grown stage', () => {
    expect(puzzleProgressTreeDisplay(100)).toEqual({
      slotWidthPercent: 100,
      aspectWidth: 372,
      aspectHeight: 438,
    });
    expect(puzzleProgressTreeDisplay(0).slotWidthPercent).toBeCloseTo(41.67, 1);
    expect(puzzleProgressTreeDisplay(50).slotWidthPercent).toBeCloseTo(70.97, 1);
  });
});
