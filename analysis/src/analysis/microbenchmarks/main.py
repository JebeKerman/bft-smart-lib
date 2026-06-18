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

    meta_info = read_json_file(log_directory / "log_meta_info.json")
    for run_id, _ in meta_info.items():
        (output_dir / run_id).mkdir(parents=True, exist_ok=True)

    server_logs = read_server_logs(log_directory, meta_info)
    df_measurements = convert_measurements_to_df(server_logs)
    df_message_sizes = convert_message_sizes_to_df(server_logs)

    plot_tp(df_measurements, output_dir)
    plot_msg_sizes(df_message_sizes, output_dir)


def read_server_logs(root_dir: Path, meta_info: Dict) -> list[Dict]:
    return [
        {
            "serializer": serializer_dir.name,
            "run_id": run_dir.name,
            "num_clients": meta_info[run_dir.name]["num_clients"],
            "client_mode": meta_info[run_dir.name]["mode"],
            "content": read_json_file(log_file),
        }
        for run_dir in sorted(root_dir.iterdir())
        if run_dir.is_dir()
        for serializer_dir in sorted(run_dir.iterdir())
        if serializer_dir.is_dir()
        for log_file in sorted(serializer_dir.glob("*.json"))
    ]


def convert_measurements_to_df(server_logs: list[Dict]) -> pd.DataFrame:
    data = [
        {
            "run_id": log["run_id"],
            "num_clients": log["num_clients"],
            "client_mode": log["client_mode"],
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
    df["serializer"] = df["serializer"].replace(
        {
            "java": "Java",
            "kryo": "Kryo",
            "proto": "Proto",
        }
    )
    return df


def convert_message_sizes_to_df(server_logs: list[Dict]) -> pd.DataFrame:
    data = [
        {
            "run_id": log["run_id"],
            "num_clients": log["num_clients"],
            "client_mode": log["client_mode"],
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
    df["serializer"] = df["serializer"].replace(
        {
            "java": "Java",
            "kryo": "Kryo",
            "proto": "Proto",
        }
    )
    return df


def read_json_file(file: Path) -> Dict:
    with open(file) as f:
        return json.load(f)


if __name__ == "__main__":
    main()
