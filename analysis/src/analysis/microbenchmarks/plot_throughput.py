from pathlib import Path

import matplotlib.pyplot as plt
import pandas as pd

from analysis.util import PLOT_COLORS, save_as_pdf_and_png


def plot_tp(df: pd.DataFrame, out_dir: Path):
    for run_id, group_df in df.groupby("run_id"):
        plot_tp_over_time(group_df, out_dir / run_id)
        plot_tp_mean(group_df, out_dir / run_id)
        plot_latency_mean(group_df, out_dir / run_id)

    plot_runs(df, out_dir)
    plot_runs_tp(df, out_dir)
    plot_runs_latency(df, out_dir)


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
            color=PLOT_COLORS[serializer],
        )
    ax.set_xlabel("Measurement")
    ax.set_ylabel("Throughput [ops/s]")
    ax.set_title("Throughput over measurements")
    ax.legend()
    ax.grid(axis="y", alpha=0.3)

    plt.tight_layout()

    save_as_pdf_and_png(out_dir, "throughput")


def plot_tp_mean(df: pd.DataFrame, out_dir: Path):
    summary = df.groupby("serializer", observed=True)["throughput"].agg(["mean", "std"])

    ax = summary["mean"].plot.bar(
        yerr=summary["std"],
        capsize=4,
        figsize=(4, 4),
        color=[PLOT_COLORS[s] for s in summary.index],
    )

    ax.set_xlabel("Serializer")
    ax.set_ylabel("Mean throughput [ops/s]")
    ax.set_title("Mean throughput by serializer")
    ax.grid(axis="y", alpha=0.3)

    plt.xticks(rotation=0)
    plt.tight_layout()

    save_as_pdf_and_png(out_dir, "throughput_mean")


def plot_latency_mean(df: pd.DataFrame, out_dir: Path):
    summary = df.groupby("serializer", observed=True)["latency"].agg(["mean", "std"])
    ax = summary["mean"].plot.bar(
        yerr=summary["std"],
        capsize=3,
        figsize=(4, 4),
        color=[PLOT_COLORS[s] for s in summary.index],
    )

    ax.set_xlabel("Serializer")
    ax.set_ylabel("Mean latency [ms]")
    ax.set_title("Latency by serializer")
    ax.grid(axis="y", alpha=0.3)

    plt.xticks(rotation=0)
    plt.tight_layout()

    save_as_pdf_and_png(out_dir, "latency_mean")


def plot_runs(df: pd.DataFrame, out_dir: Path):
    plot_df = df.groupby(["serializer", "run_id", "num_clients"], as_index=False).agg(
        mean_throughput=("throughput", "mean"), mean_latency=("latency", "mean")
    )
    _, ax = plt.subplots(figsize=(8, 6))

    markers = {
        "01": "o",
        "02": "s",
        "03": "^",
        "04": "D",
    }

    for _, row in plot_df.iterrows():
        ax.scatter(
            row["mean_throughput"],
            row["mean_latency"],
            color=PLOT_COLORS.get(row["serializer"], "gray"),
            marker=markers.get(row["run_id"], "o"),
            s=80,
            alpha=0.8,
            label=f"{row['serializer']} - {row['num_clients']}",
        )

    ax.set_xlabel("Mean Throughput")
    ax.set_ylabel("Mean Latency")
    ax.set_title("Latency vs Throughput")
    ax.grid(alpha=0.3)
    ax.legend()

    plt.tight_layout()

    save_as_pdf_and_png(out_dir, "latency_vs_throughput")


def plot_runs_tp(df: pd.DataFrame, out_dir: Path):
    plot_df = (
        df.groupby(["serializer", "run_id", "num_clients"], as_index=False)
        .agg(mean_throughput=("throughput", "mean"))
        .pivot(
            index="num_clients",
            columns="serializer",
            values="mean_throughput",
        )
    )

    ax = plot_df.plot.bar(
        capsize=4,
        figsize=(6, 4),
    )

    ax.set_xlabel("Number of Clients")
    ax.set_ylabel("Mean Throughput [ops/s]")
    ax.set_title("Mean Throughput")
    ax.grid(axis="y", alpha=0.3)
    ax.legend()

    plt.xticks(rotation=0, ha="center")
    plt.tight_layout()

    save_as_pdf_and_png(out_dir, "throughput")


def plot_runs_latency(df: pd.DataFrame, out_dir: Path):
    plot_df = (
        df.groupby(["serializer", "run_id", "num_clients"], as_index=False)
        .agg(mean_latency=("latency", "mean"))
        .pivot(
            index="num_clients",
            columns="serializer",
            values="mean_latency",
        )
    )

    ax = plot_df.plot.bar(
        capsize=4,
        figsize=(6, 4),
    )

    ax.set_xlabel("Number of Clients")
    ax.set_ylabel("Mean Latency [ms]")
    ax.set_title("Mean Latency")
    ax.grid(axis="y", alpha=0.3)
    ax.legend()

    plt.xticks(rotation=0, ha="center")
    plt.tight_layout()

    save_as_pdf_and_png(out_dir, "latency")
