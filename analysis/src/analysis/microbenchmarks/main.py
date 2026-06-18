import json
import sys
from pathlib import Path
from typing import Dict

import pandas as pd

from analysis.microbenchmarks.plot_msg_sizes import plot_msg_sizes
from analysis.microbenchmarks.plot_throughput import plot_tp


def main():
    if len(sys.argv) != 3:
        print(f"Usage: uv run {sys.argv[0]} <log_dir> <output_directory>")
        sys.exit(1)

    log_directory = Path(sys.argv[1])
    print(f"Loading logs from directory {log_directory} ...")

    output_dir = Path(sys.argv[2])
    output_dir.mkdir(parents=True, exist_ok=True)

    server_logs = read_server_logs(log_directory)
    df_measurements = convert_measurements_to_df(server_logs)
    df_message_sizes = convert_message_sizes_to_df(server_logs)

    plot_tp(df_measurements, output_dir)
    plot_msg_sizes(df_message_sizes, output_dir)


def read_server_logs(root_dir: Path) -> list[Dict]:
    return [
        {
            "serializer": subdir.name,
            "content": read_json_file(log_file),
        }
        for subdir in sorted(root_dir.iterdir())
        if subdir.is_dir()
        for log_file in sorted(subdir.glob("*.json"))
    ]


def convert_measurements_to_df(server_logs: list[Dict]) -> pd.DataFrame:
    data = [
        {
            "server_id": log["content"]["id"],
            "serializer": log["serializer"],
            "interval": log["content"]["interval"],
            "ops": measurements["ops"],
            "throughput": measurements["throughput"]["tp"],
            "max": measurements["throughput"]["max"],
            "latency": measurements["latency"]["total"],
            "batch_avg_size": measurements["batch_requests"]["avg_size"],
        }
        for log in server_logs
        for measurements in log["content"]["measurements"]
    ]
    df = pd.DataFrame(data)
    return df


def convert_message_sizes_to_df(server_logs: list[Dict]) -> pd.DataFrame:
    data = [
        {
            "server_id": log["content"]["id"],
            "serializer": log["serializer"],
            "interval": log["content"]["interval"],
            "ops": measurements["ops"],
            "message": message_size["name"],
            "byte_count": message_size["byte_count"],
            "message_count": message_size["message_count"],
        }
        for log in server_logs
        for measurements in log["content"]["measurements"]
        for message_size in measurements["message_sizes"]
    ]
    df = pd.DataFrame(data)
    return df


def read_json_file(file: Path) -> Dict:
    with open(file) as f:
        return json.load(f)


if __name__ == "__main__":
    main()
