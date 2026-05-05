#!/usr/bin/env python3

import sys
from pathlib import Path
from typing import List, TypedDict
import re
import matplotlib.pyplot as plt


class LogResult(TypedDict):
    serializer: str
    server_logs: List[Path]
    client_log: Path


def load_log_files(log_dir: str) -> List[LogResult]:
    print(f"Loading log files in directory {log_dir}")
    dirs = [p for p in Path(log_dir).iterdir() if p.is_dir()]
    results: List[LogResult] = []
    for dir in dirs:
        log_files = [p for p in Path(dir).iterdir() if p.is_file()]

        result: LogResult = {
            "serializer": dir.name,
            "server_logs": filter_server_logs(log_files),
            "client_log": [p for p in log_files if p.name.startswith("client")][0],
        }

        results.append(result)
    return results


def filter_server_logs(log_files: List[Path]):
    logs = [{
                "id": int(p.name.split('.')[0].removeprefix('server_200_p')),
                "path": p,
                "content": p.read_text(),
                "data_blocks": process_server_log(p.read_text()),
            } for p in log_files if p.name.startswith("server_")]
    return sorted(logs, key=lambda x: x["id"])


def process_server_log(log_content: str):
    blocks = log_content.split('--- Measurements')[1:]
    blocks = ['--- Measurements' + b.split('Batch average size')[0] + 'Batch average size' for b in blocks]

    data_blocks = []

    for block in blocks:
        data = {
            'ops': int(re.search(r"Measurements after (\d+) ops", block).group(1)),
            'throughput': float(re.search(r"Throughput = ([\d.E+-]+) operations/sec", block).group(1)),
            'throughput_max': float(re.search(r"Maximum observed: ([\d.E+-]+) ops/sec", block).group(1)),
            'total_latency': float(re.search(r"Total latency = ([\d.E+-]+)", block).group(1)),
            'pro_consensus_latency': float(re.search(r"Pos-consensus latency = ([\d.E+-]+)", block).group(1)),
        }
        data_blocks.append(data)

    return data_blocks


def plot_throughput(results: List[LogResult], plot_dir: Path):
    filename = f"{plot_dir}/throughput.png"
    print(f"Generating throughput plot {filename}")
    for result in results:
        line = plt.plot(
            [db['ops'] for db in result['server_logs'][0]['data_blocks']],
            [db['throughput'] for db in result['server_logs'][0]['data_blocks']],
            marker="",
            linestyle="-",
            label=result['serializer'],
        )[0]
        color = line.get_color()
        max_throughput = result['server_logs'][0]['data_blocks'][-1]['throughput_max']
        plt.axhline(
            max_throughput,
            linestyle="--",
            alpha=0.7,
            color=color,
            label=f"{result['serializer']} - Max",
        )

    plt.title("Throughput")
    plt.ylabel("operations/sec")
    plt.xlabel("ops")
    plt.legend()
    plt.grid(True)

    plt.savefig(filename)
    plt.close()


def plot_latency(results: List[LogResult], plot_dir: Path):
    filename = f"{plot_dir}/latency.png"
    print(f"Generating latency plot {filename}")
    for result in results:
        plt.plot(
            [db['ops'] for db in result['server_logs'][0]['data_blocks']],
            [db['total_latency'] for db in result['server_logs'][0]['data_blocks']],
            marker="",
            linestyle="-",
            label=result['serializer'],
        )

    plt.title("Latency")
    plt.ylabel("total latency [us]")
    plt.xlabel("ops")
    plt.legend()
    plt.grid(True)

    plt.savefig(filename)
    plt.close()


def main(log_dir: Path):
    log_files = load_log_files(log_dir)
    plot_throughput(log_files, "analysis/out")
    plot_latency(log_files, "analysis/out")


if __name__ == "__main__":
    if len(sys.argv) != 2:
        print(f"Usage: python {sys.argv[0]} <log_dir>")
        sys.exit(1)

    main(Path(sys.argv[1]))
