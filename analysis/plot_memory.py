#!/usr/bin/env python3

import json
import sys
from pathlib import Path

import matplotlib.pyplot as plt
import numpy as np


def main(json_path: Path):
    with json_path.open() as f:
        data = json.load(f)

    results = data["results"]

    messages = []
    sizes = {}

    for result in results:
        messages.append(result["message"])

        for s in result["sizes"]:
            serializer = s["serializer"]
            size = s["size"]

            sizes.setdefault(serializer, []).append(size)

    x = np.arange(len(messages))
    width = 0.25
    multiplier = 0

    plt.figure(figsize=(10, 5))

    for serializer in sizes.keys():
        offset = width * multiplier
        rects = plt.bar(x + offset, sizes[serializer], width, label=serializer)
        plt.bar_label(rects)
        multiplier += 1

    plt.ylabel("Size (bytes)")
    plt.xlabel("Message")
    plt.title("Serialized Message Size Comparison")
    plt.xticks(x, messages, rotation=20)
    plt.legend()
    
    plt.tight_layout()
    path = f"out/message_size_comparison.png"
    plt.savefig(path)
    plt.close()
    print(f"Generated plot at {path}")


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Usage: python plot_sizes.py <input.json>")
        sys.exit(1)

    main(Path(sys.argv[1]))
