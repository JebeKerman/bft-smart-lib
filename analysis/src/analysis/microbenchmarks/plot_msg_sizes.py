from pathlib import Path

import matplotlib.pyplot as plt
import pandas as pd

from analysis.util import save_as_pdf_and_png


def plot_msg_sizes(df: pd.DataFrame, out_dir: Path):
    for run_id, df in df.groupby("run_id"):
        plot_msg_sizes_absolute(df, out_dir / run_id)
        plot_msg_sizes_absolute_ppt(df, out_dir / run_id)
        plot_msg_sizes_mean(df, out_dir / run_id)
        plot_msg_sizes_mean_ppt(df, out_dir / run_id)
        csv_table(df, out_dir / run_id)


def plot_msg_sizes_absolute(df: pd.DataFrame, out_dir: Path):
    plot_df = df.groupby(["serializer", "message"]).last().reset_index()
    plot_df = plot_df.pivot(
        index="message",
        columns="serializer",
        values="byte_count",
    )
    ax = plot_df.plot.bar(
        capsize=4,
        figsize=(6, 5),
    )

    ax.set_title("Absolute Message Size")
    ax.set_xlabel("Message")
    ax.set_ylabel("Size [Byte]")
    ax.legend(title="Serializer")

    plt.xticks(rotation=30, ha="right")
    plt.tight_layout()

    save_as_pdf_and_png(out_dir, "message_size_absolute")


def plot_msg_sizes_mean(df: pd.DataFrame, out_dir: Path):
    plot_df = df.groupby(["serializer", "message"]).last().reset_index()
    plot_df["byte_count_mean"] = plot_df["byte_count"] / plot_df["message_count"]
    plot_df = plot_df.pivot(
        index="message",
        columns="serializer",
        values="byte_count_mean",
    )
    ax = plot_df.plot.bar(
        capsize=4,
        figsize=(6, 5),
    )

    ax.set_title("Average Measured Message Size")
    ax.set_xlabel("Message")
    ax.set_ylabel("Average Size [Byte]")
    ax.legend(title="Serializer")

    plt.xticks(rotation=30, ha="right")
    plt.tight_layout()

    save_as_pdf_and_png(out_dir, "message_size_mean")


def plot_msg_sizes_absolute_ppt(df: pd.DataFrame, out_dir: Path):
    plot_df = df.groupby(["serializer", "message"]).last().reset_index()
    plot_df = plot_df.pivot(
        index="message",
        columns="serializer",
        values="byte_count",
    )
    plot_df = plot_df / 1_000_000
    ax = plot_df.plot.bar(
        capsize=4,
        figsize=(6, 5),
    )

    ax.set_title("")
    ax.set_xlabel("Message")
    ax.set_ylabel("Size [MB]")
    ax.get_legend().remove()

    ax.tick_params(axis="both", labelsize=11)

    plt.xticks(rotation=30, ha="right")

    ax.grid(
        axis="y",
        alpha=0.25,
    )

    plt.xticks(rotation=0, ha="center")

    plt.tight_layout()

    save_as_pdf_and_png(out_dir, "message_size_absolute_ppt")


def plot_msg_sizes_mean_ppt(df: pd.DataFrame, out_dir: Path):
    plot_df = df.groupby(["serializer", "message"]).last().reset_index()
    plot_df["byte_count_mean"] = plot_df["byte_count"] / plot_df["message_count"]
    plot_df = plot_df.pivot(
        index="message",
        columns="serializer",
        values="byte_count_mean",
    )
    ax = plot_df.plot.bar(
        capsize=4,
        figsize=(6, 5),
    )

    ax.set_title("")
    ax.set_xlabel("Message")
    ax.set_ylabel("Average Size [Byte]")
    ax.get_legend().remove()

    ax.tick_params(axis="both", labelsize=11)
    plt.xticks(rotation=30, ha="right")

    ax.grid(
        axis="y",
        alpha=0.25,
    )

    plt.xticks(rotation=0, ha="center")

    plt.tight_layout()
    save_as_pdf_and_png(out_dir, "message_size_mean_ppt")


def csv_table(df: pd.DataFrame, out_dir: Path):
    table_df = df.groupby(["serializer", "message"]).last().reset_index()
    table_df["byte_count_mean"] = table_df["byte_count"] / table_df["message_count"]
    table_df["byte_count_mean"] = table_df["byte_count_mean"].round(2)
    table_df = (
        table_df.pivot(
            index="serializer",
            columns="message",
            values="byte_count_mean",
        )
        .reset_index()
        .rename(columns={"serializer": "Serializer"})
    )
    table_df = table_df

    filename = out_dir / "msg_size_mean.csv"
    table_df.to_csv(filename, index=False)
    print(f"Generated csv {filename}")
