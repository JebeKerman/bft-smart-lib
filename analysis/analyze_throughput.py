from pathlib import Path
from benchmark_types import ServerLogs

import pandas as pd
import matplotlib.pyplot as plt


def convert_to_df(server_logs: ServerLogs) -> pd.DataFrame:
    messages = [
        {
            "serializer": serializer,
            "server_id": log["id"],
            "measurement": measurement["ops"],
            "throughput": measurement["throughput"]["tp"],
            "throughput_max": measurement["throughput"]["max"],
            "latency": measurement["latency"]["total"],
            "batch_size_avg": measurement["batch_requests"]["avg_size"]
        }
        for (serializer, logs) in server_logs.items()
        for log in logs
        for measurement in log["measurements"]
    ]
    df = pd.DataFrame(messages)
    df["serializer"] = pd.Categorical(
        df["serializer"],
        ordered=True
    )
    df = df.sort_values(["serializer", "server_id"])
    return df


def plot_throughput(df: pd.DataFrame, out_dir: Path, filename: str = "throughput.pdf"):
    out_dir = out_dir / filename

    plot_df = (
        df.groupby(["serializer", "measurement"], observed=True)
        .agg(throughput=("throughput", "mean"))
        .reset_index()
        .sort_values(["serializer", "measurement"])
    )

    fig, ax = plt.subplots()
    for serializer, group in plot_df.groupby("serializer", observed=True):
        ax.plot(
            group["measurement"],
            group["throughput"],
            marker="o",
            label=serializer,
        )

    ax.set_xlabel("Measurement")
    ax.set_ylabel("Throughput [ops/s]")
    ax.set_title("Throughput over measurements")
    ax.legend()

    plt.tight_layout()
    plt.savefig(out_dir)
    plt.close()

    print(f"Generated plot {out_dir}")


def plot_throughput_latency(
    df: pd.DataFrame,
    out_dir: Path,
    filename: str = "throughput_latency.pdf",
):
    out_path = out_dir / filename

    plot_df = (
        df.groupby(["serializer", "measurement"], observed=True)
        .agg(
            throughput=("throughput", "mean"),
            latency=("latency", "mean"),
        )
        .reset_index()
        .sort_values(["serializer", "measurement"])
    )

    fig, ax = plt.subplots()

    for serializer, group in plot_df.groupby("serializer", observed=True):
        ax.plot(
            group["throughput"],
            group["latency"],
            marker="o",
            label=serializer,
        )

    ax.set_xlabel("Throughput [ops/s]")
    ax.set_ylabel("Latency")
    ax.set_title("Throughput vs latency")
    ax.legend()

    plt.tight_layout()
    plt.savefig(out_path)
    plt.close()

    print(f"Generated plot {out_path}")


def json_analyze_throughput(server_logs: ServerLogs, out_dir: Path):
    print("------- Analyzing throughput -------")

    df = convert_to_df(server_logs)
    print(df)

    out_dir.mkdir(parents=True, exist_ok=True)

    plot_throughput(df, out_dir)
    plot_throughput_latency(df, out_dir)
