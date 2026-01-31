#!/usr/bin/env python3

import json
import pathlib
import matplotlib.pyplot as plt
from typing import TypedDict, Literal, Dict, List, Tuple


out_dir = pathlib.Path("analysis/out")
out_dir.mkdir(parents=True, exist_ok=True)


class BenchmarkResult(TypedDict):
    avg_time: int
    raw_data: List[int]


class MessageResult(TypedDict):
    serialize: BenchmarkResult
    pass


class Result(TypedDict):
    label: Literal['Java', 'Proto']
    results: Dict[Literal['TOMMessage', 'VMMessage'], MessageResult]


def load_results(path: str, label: Literal['Java', 'Proto']) -> Result:
    print(f"Loading {label} results of file {path}")
    with open(path) as f:
        benchmarks = json.load(f)

    result: Result = {
        "label": label,
        "results": {
            'TOMMessage': {},
            'VMMessage': {},
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

        if function_name == 'serialize':
            result['results'][message]['serialize'] = function_result
    return result


def getBenchmarkName(name: str) -> Tuple[str, str]:
    names = name.split('.')
    messageName = names[-2].split('_')[1]

    return (messageName, names[-1])


def plot_message(
        java_res: Result,
        proto_res: Result,
        message_name: Literal['TOMMessage', 'VMMessage'],
        operation: str
        ):
    msg_result_java = java_res["results"][message_name]
    msg_result_proto = proto_res["results"][message_name]

    plt.plot(
        msg_result_java[operation]['raw_data'],
        marker="o",
        linestyle="",
        label="java - 1",
        color="tab:orange",
    )
    plt.axhline(
        msg_result_java[operation]["avg_time"],
        linestyle="--",
        alpha=0.7,
        color="tab:orange",
        label="Java - Mean time"
    )

    plt.plot(
        msg_result_proto[operation]['raw_data'],
        marker="o",
        linestyle="-",
        label="proto - 1",
        color="tab:blue",
    )
    plt.axhline(
        msg_result_proto[operation]["avg_time"],
        linestyle="--",
        alpha=0.7,
        color="tab:blue",
        label="Proto - Mean time"
    )

    plt.title(f"Benchmark Comparison - {message_name} Serialization")
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
        "serialize-java/build/jmh/results.json", "java")
    proto_res: Result = load_results(
        "serialize-proto/build/jmh/results.json", "proto")

    plot_message(java_res, proto_res, "TOMMessage", "serialize")
    plot_message(java_res, proto_res, "VMMessage", "serialize")


main()
