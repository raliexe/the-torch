"""Split puzzle-tree-82 into per-cell shadow overlays for the puzzle grid."""

from __future__ import annotations

from pathlib import Path

import numpy as np
from PIL import Image

SRC = (
    Path(__file__).resolve().parents[1]
    / "public/assets/images/puzzle-tree-82-2c17f1.png"
)
OUT_DIRS = [
    Path(__file__).resolve().parents[1] / "public/assets/images/puzzle-pieces",
    Path(__file__).resolve().parents[2].parent
    / "TheTorchCursor/assets/images/puzzle-pieces",
]

COLS = 3
ROWS = 4
BLACK_THRESHOLD = 35
SHADOW_ALPHA = int(255 * 0.79)


def remove_black_background(image: Image.Image) -> Image.Image:
    rgba = np.array(image.convert("RGBA"), dtype=np.uint8)
    dark = np.max(rgba[..., :3], axis=-1) <= BLACK_THRESHOLD
    rgba[dark, 3] = 0
    return Image.fromarray(rgba, mode="RGBA")


def crop_to_content(image: Image.Image) -> Image.Image:
    rgba = np.array(image.convert("RGBA"), dtype=np.uint8)
    alpha = rgba[..., 3] > 0
    if not alpha.any():
        return image

    rows = np.any(alpha, axis=1)
    cols = np.any(alpha, axis=0)
    top, bottom = np.where(rows)[0][[0, -1]]
    left, right = np.where(cols)[0][[0, -1]]
    return image.crop((left, top, right + 1, bottom + 1))


def make_shadow(crop: Image.Image) -> Image.Image:
    rgba = np.array(remove_black_background(crop).convert("RGBA"), dtype=np.uint8)
    content = rgba[..., 3] > 0
    shadow = np.zeros_like(rgba)
    shadow[..., 3] = np.where(content, SHADOW_ALPHA, 0).astype(np.uint8)
    return Image.fromarray(shadow, mode="RGBA")


def main() -> None:
    board = crop_to_content(remove_black_background(Image.open(SRC)))
    w, h = board.size
    cell_w = w // COLS
    cell_h = h // ROWS

    for out_dir in OUT_DIRS:
        out_dir.mkdir(parents=True, exist_ok=True)

    for row in range(ROWS):
        for col in range(COLS):
            idx = row * COLS + col + 1
            left = col * cell_w
            top = row * cell_h
            right = left + cell_w if col < COLS - 1 else w
            bottom = top + cell_h if row < ROWS - 1 else h
            crop = board.crop((left, top, right, bottom))
            shadow = make_shadow(crop)

            for out_dir in OUT_DIRS:
                shadow.save(out_dir / f"piece-{idx:02d}-shadow.png")

    print(f"Generated {COLS * ROWS} shadow overlays from {w}x{h} tree")


if __name__ == "__main__":
    main()
