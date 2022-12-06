import json
import pandas as pd
from pandas import DataFrame

bundle = None
# Read FHIR bundle as json
with open('fhir_bundle.json', 'r') as f:
    bundle = json.load(f)

if bundle is None:
    print("No observations found.")

# Get all observation resources
entry = bundle['entry']
observations = [x['resource'] for x in entry]

# Convert FHIR observation to simplified vital objects
# Only valueQuantity types are selected
simplified_obs = []
for res in observations:
    if 'valueQuantity' in res:

        vital = {
            'time': res['issued'],
            'vital': res['code']['text'],
            'value': res['valueQuantity']['value'],
            'unit': res['valueQuantity']['unit']
        }
        simplified_obs.append(vital)
    elif 'component' in res:
        comps = res['component']
        for comp in comps:
            if 'valueQuantity' in comp:
                vital = {
                    'time': res['issued'],
                    'vital': comp['code']['text'],
                    'value': comp['valueQuantity']['value'],
                    'unit': comp['valueQuantity']['unit']
                }
                simplified_obs.append(vital)

df_vitals = DataFrame(simplified_obs)
df_vitals.to_csv('all_vitals.csv', sep=',', encoding='utf-8')

# Filter heart disease specific vitals from all vitals and create simplified test data
# Currently Heart Rate, Systolic BP and Diastolic BP is considered for prediction
df1 = pd.read_csv('all_vitals.csv')
df1['time'] = pd.to_datetime(df1['time'])
df1 = df1.sort_values(by='time')
df2 = pd.DataFrame([df1.vital, df1.value]).transpose()
groups = df2.groupby('vital')['value'].apply(list)
df3 = pd.DataFrame(groups.reset_index(name='values'))
df4 = pd.DataFrame(columns=['sbp', 'dbp', 'hr'])
df4['sbp'] = pd.Series(df3[df3['vital'] == 'Systolic Blood Pressure'].values[0][1])
df4['dbp'] = pd.Series(df3[df3['vital'] == 'Diastolic Blood Pressure'].values[0][1])
df4['hr'] = pd.Series(df3[df3['vital'] == 'Heart rate'].values[0][1])
df4.to_csv('heart_vitals.csv', index=False)
