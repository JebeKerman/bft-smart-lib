#!/usr/bin/env python3

import sys
from pathlib import Path
from typing import List, TypedDict
import re
import matplotlib.pyplot as plt
import csv
import json
from benchmark_types import ServerLog, ServerLogs
from messages_sizes import json_plot_msg_sizes
from analyze_throughput import json_analyze_throughput


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
        "id": int(p.name.split('.')[0].removeprefix('server_p')),
        "path": p,
        "content": p.read_text(),
        "data_blocks": process_server_log(p.read_text()),
    } for p in log_files if p.name.startswith("server_")]
    return sorted(logs, key=lambda x: x["id"])


def process_server_log(log_content: str):
    blocks = log_content.split('--- Measurements')[1:]
    blocks = [
        '--- Measurements' + b
        for b in log_content.split('--- Measurements')[1:]
    ]

    data_blocks = []

    for block in blocks:
        msg_sizes = re.findall(r"(\w+)\s+ByteCount\s*=\s*(\d+):(\d+)", block)
        msg_sizes = [
            {
                "name": name,
                "byte_count": int(byte_count),
                "msg_count": int(msg_count),
                "avg_size": int(byte_count) / int(msg_count),
            }
            for name, byte_count, msg_count in msg_sizes
        ]

        data = {
            'ops': int(re.search(r"Measurements after (\d+) ops", block).group(1)),
            'throughput': float(re.search(r"Throughput = ([\d.E+-]+) operations/sec", block).group(1)),
            'throughput_max': float(re.search(r"Maximum observed: ([\d.E+-]+) ops/sec", block).group(1)),
            'total_latency': float(re.search(r"Total latency = ([\d.E+-]+)", block).group(1)),
            'pro_consensus_latency': float(re.search(r"Pos-consensus latency = ([\d.E+-]+)", block).group(1)),
            'msg_sizes': msg_sizes,
        }
        data_blocks.append(data)

    return data_blocks


def plot_throughput(results: List[LogResult], plot_dir: Path):
    plot_dir.mkdir(parents=True, exist_ok=True)
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


def plot_msg_sizes(results: List[LogResult], plot_dir: Path):
    filename = f"{plot_dir}/msg_size.png"

    print(f"Generating message size plot {filename}")
    print(f"Generating message size plot {plot_dir}/message_stats.csv")

    with open(f"{plot_dir}/message_stats.csv", "w", newline="") as f:
        writer = csv.DictWriter(
            f,
            fieldnames=[
                "serializer",
                "message_type",
                "byte_count",
                # "msg_count",
                # "avg_size",
            ],
        )
        writer.writeheader()

        rows = []
        for result in results:
            print(result['server_logs'][0]['data_blocks'][-1]['msg_sizes'])
            for message in result['server_logs'][0]['data_blocks'][-1]['msg_sizes']:
                rows.append({
                    "serializer": result['serializer'],
                    "message_type": message['name'],
                })

        print(rows)

        writer.writerows(rows)

    return
    plt.title("Latency")
    plt.ylabel("total latency [us]")
    plt.xlabel("ops")
    plt.legend()
    plt.grid(True)

    plt.savefig(filename)
    plt.close()


def load_server_logs(log_dir: Path) -> list[ServerLog]:
    server_logs: list[ServerLog] = []

    for p in log_dir.iterdir():
        if p.is_file() and p.suffix == ".json":
            with p.open("r", encoding="utf-8") as f:
                server_logs.append(json.load(f))

    return server_logs


def main(log_dir: Path, log_id: str):
    log_dir = log_dir / log_id
    out_dir = Path("analysis/out/", log_id)
    print(f"Analyzing logs in {log_dir}. Writing results to {out_dir}")

    # log_files = load_log_files(log_dir / log_id)
    # plot_throughput(log_files, out_dir)
    # plot_latency(log_files, out_dir)
    # plot_msg_sizes(log_files, out_dir)

    server_logs: ServerLogs = {
        "java": load_server_logs(log_dir / "java"),
        "kryo": load_server_logs(log_dir / "kryo"),
        "proto": load_server_logs(log_dir / "proto"),
    }
    dir_msg_stats = out_dir / "message_stats"
    dir_msg_stats.mkdir(parents=True, exist_ok=True)
    json_plot_msg_sizes(server_logs, dir_msg_stats)
    json_analyze_throughput(server_logs, dir_msg_stats)


if __name__ == "__main__":
    if len(sys.argv) != 3:
        print(f"Usage: python {sys.argv[0]} <log_dir> <log_id>")
        sys.exit(1)

    main(Path(sys.argv[1]), sys.argv[2])
