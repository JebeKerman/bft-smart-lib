#!/usr/bin/env python3

from collections import defaultdict
import json
import pathlib
import matplotlib.pyplot as plt
from typing import Dict, List, Tuple
import sys
from pathlib import Path
import numpy as np


GroupedResult = Dict[Tuple[str, str], List[Dict]]


def load_json_results(path: str) -> Dict:
    print(f"Loading results file {path}")
    with open(path) as f:
        benchmarks = json.load(f)
        return benchmarks


def parse_results(results_raw: Dict) -> List[Dict]:
    results = []
    for bench in results_raw:
        function_name = bench["benchmark"].split('.')[-1]
        message: str = str(bench["params"]["messageType"])
        serializerType: str = str(bench["params"]["serializerType"])
        avg_time: int = bench["primaryMetric"]["score"]
        raw_data = np.array(bench["primaryMetric"]["rawData"])
        raw_data = raw_data.flatten().tolist()

        result = {
            "function": function_name,
            "message": message,
            "serializer": serializerType,
            "avg_time": avg_time,
            "raw_data": raw_data,
        }
        results.append(result)

    return results


def group_by_function_and_message(data: List[Dict]) -> GroupedResult:
    grouped = defaultdict(list)

    for bench in data:
        key = (bench['function'], bench['message'])
        grouped[key].append(bench)

    return dict(grouped)


def plot_result(group_key: Tuple[str, str], data: List[Dict], out_dir: Path):
    function = group_key[0]
    message = group_key[1]

    file_name = f"{out_dir}/{message}_{function}.png"
    print(f"Generating plot {file_name}")
    for result in data:
        line = plt.plot(
            result['raw_data'],
            marker="o",
            linestyle="",
            label=result['serializer'],
        )[0]
        color = line.get_color()
        plt.axhline(
            result['avg_time'],
            linestyle="--",
            alpha=0.7,
            color=color,
            label=f"{result['serializer']} - Mean time",
        )

    plt.title(f"{message} - {function}")
    plt.ylabel("Time [ns]")
    plt.xlabel("Run")
    plt.legend()
    plt.grid(True)

    plt.savefig(file_name)
    plt.close()


def main():
    out_dir = pathlib.Path("out")
    out_dir.mkdir(parents=True, exist_ok=True)

    results_raw = load_json_results(sys.argv[1])
    result = parse_results(results_raw)
    grouped = group_by_function_and_message(result)

    for key, value in grouped.items():
        plot_result(key, value, out_dir)


if __name__ == "__main__":
    main()
