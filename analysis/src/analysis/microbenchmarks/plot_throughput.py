from pathlib import Path

import matplotlib.pyplot as plt
import pandas as pd


def plot_tp(df: pd.DataFrame, out_dir: Path):
    plot_tp_over_time(df, out_dir)
    plot_tp_mean(df, out_dir)
    plot_latency_mean(df, out_dir)


def plot_tp_over_time(df: pd.DataFrame, out_dir: Path):
    plot_df = (
        df.groupby(["serializer", "ops"], observed=True)
        .agg(throughput=("throughput", "mean"))
        .reset_index()
        .sort_values(["serializer", "ops"])
    )

    _, ax = plt.subplots()
    for serializer, group in plot_df.groupby("serializer", observed=True):
        group = group.iloc[3:-3]
        ax.plot(
            group["ops"],
            group["throughput"],
            marker="o",
            label=serializer,
        )
    ax.set_xlabel("Measurement")
    ax.set_ylabel("Throughput [ops/s]")
    ax.set_title("Throughput over measurements")
    ax.legend()
    ax.grid(axis="y", alpha=0.3)

    out_file = out_dir / "throughput.pdf"
    plt.tight_layout()
    plt.savefig(out_file)
    plt.close()

    print(f"Generated plot {out_file}")


def plot_tp_mean(df: pd.DataFrame, out_dir: Path):
    summary = (
        df.groupby("serializer", observed=True)["throughput"]
        .agg(["mean", "std"])
        .reindex(["java", "proto", "kryo"])
    )

    ax = summary["mean"].plot.bar(
        yerr=summary["std"],
        capsize=3,
        figsize=(4, 4),
    )

    ax.set_xlabel("Serializer")
    ax.set_ylabel("Throughput [ops/s]")
    ax.set_title("Mean throughput by serializer")
    ax.grid(axis="y", alpha=0.3)

    out_file = out_dir / "throughput_mean.pdf"
    plt.xticks(rotation=0)
    plt.tight_layout()
    plt.savefig(out_file)
    plt.close()

    print(f"Generated plot {out_file}")


def plot_latency_mean(df: pd.DataFrame, out_dir: Path):
    summary = (
        df.groupby("serializer", observed=True)["latency"]
        .agg(["mean", "std"])
        .reindex(["java", "proto", "kryo"])
    )
    ax = summary["mean"].plot.bar(
        yerr=summary["std"],
        capsize=3,
        figsize=(4, 4),
    )

    ax.set_xlabel("Serializer")
    ax.set_ylabel("Latency [..]")
    ax.set_title("Mean latency by serializer")
    ax.grid(axis="y", alpha=0.3)

    out_file = out_dir / "latency_mean.pdf"
    plt.xticks(rotation=0)
    plt.tight_layout()
    plt.savefig(out_file)
    plt.close()

    print(f"Generated plot {out_file}")
