import json
import sys
from pathlib import Path
from typing import Dict

import matplotlib.pyplot as plt
import pandas as pd

from analysis.util import PLOT_COLORS, save_as_pdf_and_png


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

    plot_message_sizes_absolute(df, output_dir)
    plot_message_sizes_relative(df, output_dir)
    table_message_size(df, output_dir)


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
    df["message"] = (
        df["message"]
        .replace(
            {
                "CSTSMMessageWire": "CSTSM",
                "ConsensusMessage": "Consensus",
                "LCMessageWire": "LC",
                "StandardSMMessageWire": "StandardSM",
                "TOMMessageWire": "TOM",
                "VMMessage": "VM",
            }
        )
        .astype("category")
    )

    serializer_names = {
        "JavaSerializer": "Java",
        "ProtoSerializer": "Proto",
        "KryoSerializer": "Kryo",
    }
    df["serializer"] = df["serializer"].replace(serializer_names)

    df["serializer"] = pd.Categorical(
        df["serializer"],
        categories=["Java", "Kryo", "Proto"],
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
        figsize=(8, 4),
        color=PLOT_COLORS,
    )

    ax.set_title("Total Serialized Message Size by Message Type")
    ax.set_xlabel("Message")
    ax.set_ylabel("Message Size [Byte]")
    ax.grid(axis="y", alpha=0.3)
    ax.legend(title="Serializer")

    plt.xticks(rotation=30, ha="right")
    plt.tight_layout()

    save_as_pdf_and_png(output_dir, "message_size_absolute")


def plot_message_sizes_relative(df: pd.DataFrame, output_dir: Path):
    pivot = df.pivot(
        index="message",
        columns="serializer",
        values="java_diff_relative",
    )
    ax = pivot.plot.bar(
        capsize=4,
        figsize=(8, 4),
        color=PLOT_COLORS,
    )

    ax.set_title("Relative Serialized Message Size by Message Type")
    ax.set_xlabel("Message")
    ax.set_ylabel("Message Size [Byte]")
    ax.grid(axis="y", alpha=0.3)
    ax.legend(title="Serializer")

    plt.xticks(rotation=30, ha="right")
    plt.tight_layout()

    save_as_pdf_and_png(output_dir, "message_size_relative")


def table_message_size(df: pd.DataFrame, output_dir: Path):
    pivot = (
        df.pivot(
            index="message",
            columns="serializer",
            values="java_diff_relative",
        )
        .reset_index()
        .drop("Java", axis=1)
    )

    pivot = pivot.rename(columns={"message": "Message"})

    pivot.columns.name = None
    pivot.columns = [rf"\textbf{{{c}}}" for c in pivot.columns]
    print(pivot)

    output_file = output_dir / "message_size.tex"
    with open(output_file, "w") as f:
        f.write(
            pivot.to_latex(
                index=False,
                escape=False,
                column_format="@{}lrrr@{}",
                float_format=lambda x: f"{100 * x:.1f} \\%",
            )
        )

    print(f"Generated tex {output_file}")


if __name__ == "__main__":
    main()
