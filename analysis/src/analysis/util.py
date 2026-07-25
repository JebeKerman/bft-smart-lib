from pathlib import Path
import matplotlib.pyplot as plt

PLOT_COLORS = {
    "Java": "tab:blue",
    "Kryo": "tab:orange",
    "Proto": "tab:green",
}


def save_as_pdf_and_png(output_dir: Path, file_name: str):
    output_file = output_dir / f"{file_name}.png"
    plt.savefig(output_file, dpi=200)
    print(f"Generated plot {output_file}")

    output_file = output_dir / f"{file_name}.pdf"
    plt.savefig(output_file, dpi=200)
    print(f"Generated plot {output_file}")
    plt.close()
