import os
import csv
from collections import defaultdict

# Define the base directory
base_dir = '.'

# Define the output directory and file name
output_dir = './output'  # Change this to your desired output directory
output_file = 'group_line_counts.csv'

# Ensure the output directory exists
os.makedirs(output_dir, exist_ok=True)

# Initialize a dictionary to store the number of lines for each group in each directory
line_counts = defaultdict(lambda: defaultdict(int))

# List of directories to process
directories = ["BLOCK", "CONSTRUCTOR", "MODIFIER", "SIMPLE_NAME", "SINGLE_VARIABLE_DECL", "TYPE"]

# Function to count lines in a CSV file excluding the header
def count_lines_in_csv(file_path):
    with open(file_path, 'r') as file:
        reader = csv.reader(file)
        return sum(1 for row in reader) - 1  # Subtract 1 for the header row

# Iterate through each directory
for directory in directories:
    dir_path = os.path.join(base_dir, directory)
    if os.path.isdir(dir_path):
        for file_name in os.listdir(dir_path):
            if file_name.endswith('.csv'):
                group_name = file_name.split('--')[1]  # Extract group name
                file_path = os.path.join(dir_path, file_name)
                line_count = count_lines_in_csv(file_path)
                line_counts[group_name][directory] += line_count

# Get a sorted list of all group names
all_groups = sorted(line_counts.keys())

# Write the results to a CSV file in the output directory
output_path = os.path.join(output_dir, output_file)
with open(output_path, 'w', newline='') as csvfile:
    writer = csv.writer(csvfile)
    
    # Write the header row
    header = ['Group Name'] + directories
    writer.writerow(header)
    
    # Write the data rows
    for group in all_groups:
        row = [group] + [line_counts[group][directory] for directory in directories]
        writer.writerow(row)

print(f"Line counts have been written to {output_path}")
