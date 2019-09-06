import os
from graphviz import Digraph
import json


def get_name_from_path(path):
    return ".".join(path.split(".")[0].split("\\")[10:])


def get_list_of_files(dir_name):
    list_of_file = os.listdir(dir_name)
    all_files = list()
    for entry in list_of_file:
        full_path = os.path.join(dir_name, entry)
        if os.path.isdir(full_path):
            all_files = all_files + get_list_of_files(full_path)
        else:
            all_files.append(full_path)
    return all_files


dot = Digraph(format="svg")
dict = {}
listOfFilesUpdated = []
listOfPaths = []

listOfFiles = get_list_of_files(r"C:\Users\ste\Documents\sd\tucson\core")
genericListOfFiles = get_list_of_files(r"C:\Users\ste\Documents\sd\tucson")

for fileLocal in genericListOfFiles:
    if fileLocal.split(".")[-1] == "java" and "test" not in fileLocal:
        name = ".".join(fileLocal.split(".")[0].split("\\")[10:])
        dict[name] = fileLocal

# with open('dict.txt', 'w') as file:
#    file.write(json.dumps(dict))  # use `json.loads` to do the reverse

for fileLocal in listOfFiles:
    if fileLocal.split(".")[-1] == "java" and "test" not in fileLocal:
        name = ".".join(fileLocal.split(".")[0].split("\\")[10:])
        print(name)
        dot.node(name, name, color='red', style='filled', fillcolor='blue')
        listOfFilesUpdated.append(fileLocal)
        listOfPaths.append(name)

for fileLocal in listOfFilesUpdated:
    if fileLocal.split(".")[-1] == "java" and "test" not in fileLocal:
        fileOpened = open(fileLocal, "r")
        classThatImport = get_name_from_path(fileLocal)
        linesRead = fileOpened.readlines()
        for line in linesRead:
            if line.split(" ")[0] == "import" and "java" not in line and "slf4j" not in line:  # or line.split(" ")[0] == "package":
                classImported = line.split(" ")[-1].split(";")[0]
                # takes all the classes that are in the package
                if classImported in listOfPaths:
                    dot.edge(classThatImport, classImported)
        fileOpened.close()

dot.render('test-output/inner-dependencies-core', view=True)
