#!/usr/bin/env python3

import sys
import json
import pathlib
import pandas as pd
import matplotlib.pyplot as plt
from matplotlib import cm

out_dir = pathlib.Path("analysis/out")
out_dir.mkdir(parents=True, exist_ok=True)

files = sys.argv[1:]

dfs = []

def load_results(path, label):
    with open(path) as f:
        benchmarks = json.load(f)

    for bench in benchmarks:
        name: str = bench["benchmark"]
        metric = bench["primaryMetric"]
        avgTime: int = metric["score"]
        times = []
        for fork_run, raw_data in enumerate(metric["rawData"]):
            fork_times = []
            for value in raw_data:
              fork_times.append(value)
            times.append(fork_times)

        print(avgTime)
        print(times)
        return {
            "name": name,
            "mean": avgTime,
            "times": times  
        }

java_res = load_results("serialize-java/build/jmh/results.json", "java")
proto_res = load_results("serialize-proto/build/jmh/results.json", "proto")

plt.figure(figsize=(8, 5))
for run, time in enumerate(java_res['times'][0]):
    pass
plt.plot( 
    java_res['times'][0],        
    marker="o",
    linestyle="", 
    label="java - 1",
    color="tab:orange",
)
plt.plot( 
    java_res['times'][1],        
    marker="x",
    linestyle="", 
    label="java - 2",
    color="tab:orange",
)
plt.plot( 
    proto_res['times'][0],        
    marker="o",
    linestyle="-", 
    label="proto - 1",
    color="tab:blue",
)
plt.plot( 
    proto_res['times'][1],        
    marker="x",
    linestyle="-", 
    label="proto - 2",
    color="tab:blue",
)
plt.axhline(java_res['mean'], linestyle="--", alpha=0.7,
    color="tab:orange",)
plt.axhline(proto_res['mean'], linestyle="--", alpha=0.7, 
    color="tab:blue",)

plt.title("Benchmark Comparison")
plt.ylabel("Time [ns]")
plt.xlabel("Iteration")
plt.legend()
plt.grid(True)

path = out_dir / "java.png"
plt.savefig(path)
plt.close()
print(f"Wrote plot {path}")
