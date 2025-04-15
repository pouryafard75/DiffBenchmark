import pandas as pd
import sys
import re

def calculate_metrics(tp, fp, fn):
    """Returns raw values for LaTeX formatting."""
    precision_numerator = tp
    precision_denominator = tp + fp

    recall_numerator = tp
    recall_denominator = tp + fn

    f1_fp = fp
    f1_fn = fn

    return (precision_numerator, precision_denominator), (recall_numerator, recall_denominator), (tp, f1_fp, f1_fn)

def extract_tool_name(column_name):
    match = re.match(r'(.+)_TP_.*', column_name)
    return match.group(1) if match else column_name

def generate_latex_bars(csv_path, mode, ignore_tools=[]):
    if mode == "s":
        metric_type = "abstractMapping"
    elif mode == "p":
        metric_type = "programElement"
    else:
        raise ValueError("Invalid mode. Use 'p' for program elements or 's' for subtree (abstract mappings).")

    print(f"Metric Type: {metric_type}")  # Print metric type here
    print(f"Ignoring tools: {', '.join(ignore_tools)}")  # Print ignored tools

    df = pd.read_csv(csv_path)
    data_cols = df.columns[5:]
    tool_blocks = [data_cols[i:i+8] for i in range(0, len(data_cols), 8)]

    tool_names = []
    precision_bars = []
    recall_bars = []
    f1_bars = []

    # Iterate through the tool blocks and process them
    for block in tool_blocks:
        first_col = block[0]
        tool_name = extract_tool_name(first_col)

        # Skip the tool if it's in the ignore list
        if any(tool_name.startswith(ignore_tool) for ignore_tool in ignore_tools):
#            print(f"[Info] Ignoring tool '{tool_name}'")
            continue  # Skip this tool completely

        tool_names.append(tool_name)

        try:
            tp = df[f"{tool_name}_TP_{metric_type}"].sum()
            fp = df[f"{tool_name}_FP_{metric_type}"].sum()
            fn = df[f"{tool_name}_FN_{metric_type}"].sum()

            (prec_num, prec_den), (rec_num, rec_den), (f1_tp, f1_fp, f1_fn) = calculate_metrics(tp, fp, fn)

            precision_bars.append(f"\\pbar{{{prec_num}}}{{{prec_den}}}")
            recall_bars.append(f"\\pbar{{{rec_num}}}{{{rec_den}}}")
            f1_bars.append(f"\\fOne{{{f1_tp}}}{{{f1_fp}}}{{{f1_fn}}}")

        except KeyError as e:
            print(f"[Warning] Skipping tool '{tool_name}' due to missing column: {e}")
            continue

    # Join bars into LaTeX rows
    tool_row = ' & '.join(tool_names) + " \\\\" 
    precision_row = ' & '.join(precision_bars) + " \\\\" 
    recall_row = ' & '.join(recall_bars) + " \\\\" 
    f1_row = ' & '.join(f1_bars) + " \\\\" 

    print("==== LaTeX Tool Names Row ====")
    print(tool_row)
    print("\n==== LaTeX Precision Row ====")
    print(precision_row)
    print("\n==== LaTeX Recall Row ====")
    print(recall_row)
    print("\n==== LaTeX F1 Row ====")
    print(f1_row)

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python generate_latex.py <path_to_csv> <p|s> [ignore_tools]")
        sys.exit(1)
    else:
        ignore_tools = sys.argv[3:] if len(sys.argv) > 3 else []
#        print(f"Ignoring tools: {', '.join(ignore_tools)}")  # Print ignored tools
        generate_latex_bars(sys.argv[1], sys.argv[2], ignore_tools)
