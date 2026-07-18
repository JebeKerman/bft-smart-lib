import json
import sys
from pathlib import Path
from typing import Dict

import matplotlib.pyplot as plt
import pandas as pd

from analysis.util import save_as_pdf_and_png


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

    plot_bar_charts(df, output_dir)
    plot_relative_speedup(df, output_dir)
    table_relative_speedup(df, output_dir)


def load_json_results(input_file: Path) -> Dict:
    with open(input_file) as f:
        return json.load(f)


def convert_to_df(jmh_results: Dict) -> pd.DataFrame:
    result = [
        {
            "serializer": result["params"]["serializerType"],
            "message": result["params"]["messageType"],
            "method": str(result["benchmark"]).split(".")[-1],
            "warmupTime": result["warmupTime"],
            "warmupIterations": result["warmupIterations"],
            "measurementTime": result["measurementTime"],
            "measurementIterations": result["measurementIterations"],
            "avg_time": result["primaryMetric"]["score"],
            "avg_time_err": result["primaryMetric"]["scoreError"],
        }
        for result in jmh_results
    ]
    df = pd.DataFrame(result)
    df["serializer"] = pd.Categorical(
        df["serializer"],
        categories=[
            "Java",
            "Kryo",
            "Proto",
        ],
        ordered=True,
    )
    df["method"] = pd.Categorical(
        df["method"],
        categories=["serialize", "deserialize"],
        ordered=True,
    )
    df["message"] = (
        df["message"]
        .replace(
            {
                "CSTSMMessageMinimal": "CSTSM",
                "ConsensusMessage": "Consensus",
                "LCMessage": "LC",
                "StandardSMMessage": "StandardSM",
                "TOMMessage": "TOM",
                "VMMessage": "VM",
            }
        )
        .astype("category")
    )

    df = df.sort_values(["method", "message", "serializer"])

    return df


def extend_df(df: pd.DataFrame) -> pd.DataFrame:
    df = df.copy()
    baseline = df[df["serializer"] == "Java"][["message", "method", "avg_time"]].rename(
        columns={"avg_time": "java_avg_time"}
    )

    df = df.merge(
        baseline,
        on=["message", "method"],
        how="left",
    )

    df["speedup_vs_java"] = df["java_avg_time"] / df["avg_time"]

    return df


def plot_bar_charts(df: pd.DataFrame, output_dir: Path):
    for method, group in df.groupby("method", observed=True):
        pivot = group.pivot(
            index="message",
            columns="serializer",
            values="avg_time",
        )
        pivot = pivot / 1000

        ax = pivot.plot.bar(
            capsize=4,
            figsize=(8, 4),
        )

        if method == "serialize":
            methodStr = "Serialize"
        else:
            methodStr = "Deserialize"

        ax.set_title(f"Average {methodStr} Time by Message")
        ax.set_xlabel("Message")
        ax.set_ylabel("Average time [μs/op]")
        ax.grid(axis="y", alpha=0.3)
        ax.legend(title="Serializer")

        plt.xticks(rotation=45, ha="right")
        plt.tight_layout()

        save_as_pdf_and_png(output_dir, f"avg_time_by_message_{method}")


def plot_relative_speedup(df: pd.DataFrame, output_dir: Path):
    for method, method_df in df.groupby("method", observed=True):
        speedup_table = method_df.pivot(
            index="message",
            columns="serializer",
            values="speedup_vs_java",
        )
        speedup_table = speedup_table[["Java", "Proto", "Kryo"]].round(2)

        # Save to csv
        output_file = output_dir / f"relative_speedup_{method}.csv"
        speedup_table.to_csv(output_file, index=False)
        print(f"Generated csv {output_file}")

        # Save plot to pdf
        ax = speedup_table.plot.bar(
            capsize=4,
            figsize=(10, 5),
        )

        ax.set_title(f"Relative speedup {method} by message type")
        ax.set_xlabel("Message")
        ax.set_ylabel("Speedup compared to java serialization")
        ax.legend(title="Serializer")

        plt.xticks(rotation=45, ha="right")
        plt.tight_layout()

        save_as_pdf_and_png(output_dir, f"relative_speedup_{method}")


def table_relative_speedup(df: pd.DataFrame, output_dir: Path):
    pivot = df[df["serializer"] != "Java"]
    pivot = (
        pivot.pivot_table(
            index="message",
            columns=["method", "serializer"],
            values="speedup_vs_java",
        )
        .reset_index()
        .round(2)
        .rename(
            columns={
                "message": r"\textbf{Message}",
                "serialize": r"\textbf{Serialize}",
                "deserialize": r"\textbf{Deserialize}",
            }
        )
    )

    output_file = output_dir / "relative_speedup.tex"
    with open(output_file, "w") as f:
        f.write(
            pivot.to_latex(
                index=False,
                escape=False,
                multicolumn=True,
                multicolumn_format="c",
                column_format="@{}lcc@{\hspace{1.4em}}cc@{}",
                float_format=lambda x: f"{x:.2f}$\\times$",
            )
        )

    print(f"Generated tex {output_file}")


if __name__ == "__main__":
    main()
