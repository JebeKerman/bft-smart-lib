from typing import TypedDict


class Throughput(TypedDict):
    unit: str
    tp: float
    max: float


class Latency(TypedDict):
    unit: str
    total: float
    total_dp: int
    consensus: float
    consensus_dp: int
    pre_consensus: float
    pre_consensus_dp: int
    pos_consensus: float
    pos_consensus_dp: int
    propose: float
    propose_dp: int
    write: float
    write_dp: int
    accept: float
    accept_dp: int


class BatchRequests(TypedDict):
    avg_size: float
    dp: int


class MessageSize(TypedDict):
    name: str
    byte_count: int
    message_count: int


class Measurement(TypedDict):
    ops: int
    throughput: Throughput
    latency: Latency
    batch_requests: BatchRequests
    message_sizes: list[MessageSize]


class ServerLog(TypedDict):
    id: int
    interval: int
    measurements: list[Measurement]


class ServerLogs(TypedDict):
    java: list[ServerLog]
    kryo: list[ServerLog]
    proto: list[ServerLog]
