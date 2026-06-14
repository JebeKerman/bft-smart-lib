from pathlib import Path
from benchmark_types import ServerLogs

import pandas as pd
import matplotlib.pyplot as plt


def convert_to_df(server_logs: ServerLogs) -> pd.DataFrame:
    messages = [
        {
            "serializer": serializer,
            "name": msg["name"],
            "byte_count": msg["byte_count"],
            "msg_count": msg["message_count"],
        }
        for (serializer, logs) in server_logs.items()
        for log in logs
        if log["id"] == 0
        for msg in log["measurements"][-1]["message_sizes"]
    ]
    df = pd.DataFrame(messages)
    df["serializer"] = pd.Categorical(
        df["serializer"],
        ordered=True
    )
    return df


def extend_df(df: pd.DataFrame) -> pd.DataFrame:
    df = df.copy()
    df["avg_size"] = df["byte_count"] / df["msg_count"]

    baseline = df[df["serializer"] == "java"].set_index("name")

    df["java_byte_count"] = df["name"].map(baseline["byte_count"])
    df["java_avg_msg_size"] = df["name"].map(baseline["avg_size"])

    df["byte_count_diff"] = df["byte_count"] - df["java_byte_count"]
    df["byte_count_diff_pct"] = (df["byte_count"] / df["java_byte_count"] - 1) * 100

    df["avg_msg_size_diff"] = df["avg_size"] - df["java_avg_msg_size"]
    df["avg_msg_size_diff_pct"] = (df["avg_size"] / df["java_avg_msg_size"] - 1) * 100
    return df


def save_avg_size_plot(df: pd.DataFrame, out_dir: Path, filename: str = "avg_message_size.pdf"):
    out_dir = out_dir / filename

    pivot = df.pivot(index="name", columns="serializer", values="avg_size")

    ax = pivot.plot(kind="bar")
    ax.set_ylabel("Average message size [bytes]")
    ax.set_xlabel("Message type")
    ax.set_title("Average message size by serializer")
    ax.tick_params(axis="x", rotation=30)

    plt.tight_layout()
    plt.savefig(out_dir)
    plt.close()

    print(f"Generated plot {out_dir}")


def save_total_bytes_plot(df: pd.DataFrame, out_dir: Path, filename: str = "total_bytes.pdf"):
    out_dir = out_dir / filename

    pivot = df.pivot(index="name", columns="serializer", values="byte_count")

    ax = pivot.plot(kind="bar")
    ax.set_ylabel("Total serialized bytes")
    ax.set_xlabel("Message type")
    ax.set_title("Total serialized bytes by serializer")
    ax.tick_params(axis="x", rotation=30)

    plt.tight_layout()
    plt.savefig(out_dir)
    plt.close()

    print(f"Generated plot {out_dir}")


def save_table_csv(df: pd.DataFrame, out_dir: Path):
    for msg_name, msg_df in df.groupby("name", observed=True):
        columns = [
            "serializer",
            "byte_count",
            "msg_count",
            "avg_size",
            "avg_msg_size_diff_pct",
        ]

        table_df = msg_df[columns].copy()

        table_df["avg_size"] = table_df["avg_size"].round(2)
        table_df["avg_msg_size_diff_pct"] = table_df["avg_msg_size_diff_pct"].round(2)
        table_df.to_csv(out_dir / f"msg_size_{msg_name}.csv", index=False)

        print(f"Generated csv file {out_dir / f"msg_size_{msg_name}.csv"}")


def json_plot_msg_sizes(server_logs: ServerLogs, out_dir: Path):
    print("------- Analyzing message sizes -------")

    df = convert_to_df(server_logs)
    df = extend_df(df)

    out_dir.mkdir(parents=True, exist_ok=True)

    save_avg_size_plot(df, out_dir)
    save_total_bytes_plot(df, out_dir)

    save_table_csv(df, out_dir)
