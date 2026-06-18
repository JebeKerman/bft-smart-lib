from pathlib import Path

import matplotlib.pyplot as plt
import pandas as pd


def plot_msg_sizes(df: pd.DataFrame, out_dir: Path):
    for run_id, df in df.groupby("run_id"):
        plot_msg_sizes_absolute(df, out_dir / run_id)
        plot_msg_sizes_mean(df, out_dir / run_id)
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

    ax.set_title("performance by message type")
    ax.set_xlabel("Message")
    ax.set_ylabel("Average time [ns/op]")
    ax.legend(title="Serializer")

    plt.xticks(rotation=30, ha="right")
    plt.tight_layout()

    output_file = out_dir / "message_size_absolute.pdf"
    plt.savefig(output_file, dpi=200)
    plt.close()
    print(f"Generated plot {output_file}")


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

    ax.set_title("performance by message type")
    ax.set_xlabel("Message")
    ax.set_ylabel("Average time [ns/op]")
    ax.legend(title="Serializer")

    plt.xticks(rotation=30, ha="right")
    plt.tight_layout()

    output_file = out_dir / "message_size_mean.pdf"
    plt.savefig(output_file, dpi=200)
    plt.close()
    print(f"Generated plot {output_file}")


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
