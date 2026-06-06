import csv
import os
import requests
import json
import sys
import time

INPUT_FILE = 'evaluation_dataset_35.csv'
OUTPUT_FILE = 'evaluation_dataset_35_answer.csv'
API_URL = 'http://localhost:8080/api/v1/chat'

def load_data():
    """Load data from input and merge with existing output if available."""
    input_data = []
    if not os.path.exists(INPUT_FILE):
        print(f"Error: Input file {INPUT_FILE} not found.")
        return []
        
    with open(INPUT_FILE, mode='r', encoding='utf-8-sig') as f:
        reader = csv.DictReader(f)
        for row in reader:
            input_data.append(row)
    
    if not os.path.exists(OUTPUT_FILE):
        for row in input_data:
            row['actual_answer'] = ''
            row['is_executed'] = 'False'
        return input_data

    output_data = []
    with open(OUTPUT_FILE, mode='r', encoding='utf-8') as f:
        reader = csv.DictReader(f)
        for row in reader:
            output_data.append(row)
    
    # Merge: keep existing output results, add new rows from input if any
    output_ids = {row['id'] for row in output_data}
    for row in input_data:
        if row['id'] not in output_ids:
            row['actual_answer'] = ''
            row['is_executed'] = 'False'
            output_data.append(row)
            
    return output_data

def save_data(data):
    """Save the current state to the output CSV file."""
    if not data:
        return
    fieldnames = list(data[0].keys())
    with open(OUTPUT_FILE, mode='w', encoding='utf-8', newline='') as f:
        writer = csv.DictWriter(f, fieldnames=fieldnames)
        writer.writeheader()
        writer.writerows(data)

def query_api(question):
    """Call the backend chat API."""
    try:
        # Backend expects @RequestBody String message
        payload = {
            "message": question
        }
        response = requests.post(API_URL, json=payload,timeout=30)
        if response.status_code == 200:
            return response.text
        else:
            return f"Error: Status {response.status_code} - {response.text}"
    except Exception as e:
        return f"Error: {str(e)}"

def main():
    print(f"Starting/Resuming evaluation...")
    data = load_data()
    if not data:
        print("No data to process.")
        return
        
    total = len(data)
    executed_count = sum(1 for row in data if row.get('is_executed') == 'True')
    
    print(f"Total items: {total}, Already executed: {executed_count}")

    try:
        for i, row in enumerate(data):
            if row.get('is_executed') == 'True':
                continue
            
            id_val = row['id']
            question = row['question']
            print(f"[{i+1}/{total}] Processing ID {id_val}: {question[:50]}...")
            
            answer = query_api(question)
            row['actual_answer'] = answer
            row['is_executed'] = 'True'
            
            # sleep 1 minute to avoid hitting rate limits or overloading the backend
            time.sleep(60)  # Uncomment if needed
            
            # Save after every successful call for durability
            save_data(data)
            
    except KeyboardInterrupt:
        print("\nInterrupted by user. Progress saved.")
        sys.exit(0)
    except Exception as e:
        print(f"\nUnexpected error: {e}")
        save_data(data)
        sys.exit(1)

    print(f"Evaluation complete. Results saved to {OUTPUT_FILE}")

if __name__ == "__main__":
    main()
