#!/usr/bin/env python3

import json
import pathlib
import matplotlib.pyplot as plt
from typing import TypedDict, Literal, Dict, List, Tuple
import sys
from pathlib import Path


out_dir = pathlib.Path("out")
out_dir.mkdir(parents=True, exist_ok=True)


class BenchmarkResult(TypedDict):
    avg_time: int
    raw_data: List[int]


class MessageResult(TypedDict):
    serialize: BenchmarkResult
    deserialize: BenchmarkResult
    pass


class Result(TypedDict):
    label: Literal['Java', 'Proto']
    results: Dict[Literal['TOMMessage', 'VMMessage', 'LCMessage'], MessageResult]


def load_results(path: str, label: Literal['Java', 'Proto']) -> Result:
    print(f"Loading {label} results of file {path}")
    with open(path) as f:
        benchmarks = json.load(f)

    result: Result = {
        "label": label,
        "results": {
            'TOMMessage': {},
            'VMMessage': {},
            'LCMessage': {},
        }
    }
    for bench in benchmarks:
        benchmark_fqn: str = str(bench["benchmark"])
        [message, function_name] = getBenchmarkName(benchmark_fqn)

        metrics = bench["primaryMetric"]
        function_result: Result = {
            "avg_time": metrics["score"],
            "raw_data": []
        }
        for raw_data in metrics["rawData"]:
            for value in raw_data:
                function_result['raw_data'].append(value)

        result['results'][message][function_name] = function_result
    return result


def getBenchmarkName(name: str) -> Tuple[str, str]:
    names = name.split('.')
    messageName = names[-2].split('_')[1]

    return (messageName, names[-1])


def plot_message(
        java_res: Result,
        proto_res: Result,
        message_name: Literal['TOMMessage', 'VMMessage', 'LCMessage'],
        operation: str
        ):
    msg_result_java = java_res["results"][message_name]
    msg_result_proto = proto_res["results"][message_name]

    plt.plot(
        msg_result_java[operation]['raw_data'],
        marker="o",
        linestyle="",
        label="Java",
        color="tab:orange",
    )
    plt.axhline(
        msg_result_java[operation]["avg_time"],
        linestyle="--",
        alpha=0.7,
        label="Java - Mean time",
        color="tab:orange",
    )

    plt.plot(
        msg_result_proto[operation]['raw_data'],
        marker="o",
        linestyle="-",
        label="Proto",
        color="tab:blue",
    )
    plt.axhline(
        msg_result_proto[operation]["avg_time"],
        linestyle="--",
        alpha=0.7,
        label="Proto - Mean time",
        color="tab:blue",
    )

    plt.title(f"Benchmark Comparison - {message_name} {operation}")
    plt.ylabel("Time [ns]")
    plt.xlabel("Run")
    plt.legend()
    plt.grid(True)

    path = f"{out_dir}/{message_name}_{operation}.png"
    plt.savefig(path)
    plt.close()
    print(f"Generated plot at {path}")


def main():
    java_res: Result = load_results(
        Path(sys.argv[1]), "java")
    proto_res: Result = load_results(
        Path(sys.argv[2]), "proto")

    plot_message(java_res, proto_res, "TOMMessage", "serialize")
    plot_message(java_res, proto_res, "TOMMessage", "deserialize")
    plot_message(java_res, proto_res, "VMMessage", "serialize")
    plot_message(java_res, proto_res, "VMMessage", "deserialize")
    plot_message(java_res, proto_res, "LCMessage", "serialize")
    plot_message(java_res, proto_res, "LCMessage", "deserialize")


main()
