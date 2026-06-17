import json
import sys
from pathlib import Path
from typing import Dict

import matplotlib.pyplot as plt
import pandas as pd


def main():
    if len(sys.argv) != 3:
        print(f"Usage: uv run {sys.argv[0]} <jmg-result-file> <output_directory>")
        sys.exit(1)

    input_file = Path(sys.argv[1])
    print(f"Loading results file {input_file} ...")

    output_dir = Path(sys.argv[2])
    output_dir.mkdir(parents=True, exist_ok=True)

    json_res = load_json_results(input_file)
    df = convert_to_df(json_res)
    df = extend_df(df)

    print(df)

    plot_message_sizes_absolute(df, output_dir)
    plot_message_sizes_relative(df, output_dir)


def load_json_results(input_file: Path) -> Dict:
    with open(input_file) as f:
        return json.load(f)


def convert_to_df(jmh_results: Dict) -> pd.DataFrame:
    result = [
        {
            "message": result["message"],
            "serializer": size["serializer"],
            "byte_size": size["size"],
        }
        for result in jmh_results["results"]
        for size in result["sizes"]
    ]
    df = pd.DataFrame(result)
    df["message"] = df["message"].astype("category")

    serializer_names = {
        "JavaSerializer": "Java",
        "ProtoSerializer": "Proto",
        "KryoSerializer": "Kryo",
    }
    df["serializer"] = df["serializer"].replace(serializer_names)

    df["serializer"] = pd.Categorical(
        df["serializer"],
        categories=["Java", "Proto", "Kryo"],
        ordered=True,
    )

    return df


def extend_df(df: pd.DataFrame) -> pd.DataFrame:
    df = df.copy()
    baseline = df[df["serializer"] == "Java"][["message", "byte_size"]].rename(
        columns={"byte_size": "java_byte_size"}
    )

    df = df.merge(
        baseline,
        on=["message"],
        how="left",
    )

    df["java_diff_absolute"] = df["java_byte_size"] - df["byte_size"]
    df["java_diff_relative"] = df["byte_size"] / df["java_byte_size"]
    df["compress_ratio"] = df["java_byte_size"] / df["byte_size"]

    return df


def plot_message_sizes_absolute(df: pd.DataFrame, output_dir: Path):
    pivot = df.pivot(
        index="message",
        columns="serializer",
        values="byte_size",
    )
    ax = pivot.plot.bar(
        capsize=4,
        figsize=(6, 5),
    )

    ax.set_title("performance by message type")
    ax.set_xlabel("Message")
    ax.set_ylabel("Average time [ns/op]")
    ax.legend(title="Serializer")

    plt.xticks(rotation=30, ha="right")
    plt.tight_layout()

    output_file = output_dir / "message_size_absolute.pdf"
    plt.savefig(output_file, dpi=200)
    plt.close()
    print(f"Generated plot {output_file}")


def plot_message_sizes_relative(df: pd.DataFrame, output_dir: Path):
    pivot = df.pivot(
        index="message",
        columns="serializer",
        values="java_diff_relative",
    )
    ax = pivot.plot.bar(
        capsize=4,
        figsize=(6, 5),
    )
    print(pivot)

    ax.set_title("performance by message type")
    ax.set_xlabel("Message")
    ax.set_ylabel("Average time [ns/op]")
    ax.legend(title="Serializer")

    plt.xticks(rotation=30, ha="right")
    plt.tight_layout()

    output_file = output_dir / "message_size_relative.pdf"
    plt.savefig(output_file, dpi=200)
    plt.close()
    print(f"Generated plot {output_file}")


if __name__ == "__main__":
    main()
