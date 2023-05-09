import ast, csv, json

csvFilePath = '/Users/Shinra/st_thomas/SEIS 630-01/Final Project/resources/movie_credits.csv'
jsonFilePath = '/Users/Shinra/st_thomas/SEIS 630-01/Final Project/resources/movie_credits_doc.json'

data = {}
with open(csvFilePath) as csvFile:
    csvReader = csv.DictReader(csvFile)
    for rows in csvReader:
        id = rows['id']
        data[id] = rows

with open(jsonFilePath, 'w') as jsonFile:
    jsonStr = str(data)
    formatted = ast.literal_eval(jsonStr)
    jsonFile.write(json.dumps(formatted))