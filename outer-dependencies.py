import os
from graphviz import Digraph
import json


# all java classes inside the project
def get_all_java_classes():
    genericListOfFiles = get_list_of_files(r".\tucson")
    for fileLocal in genericListOfFiles:
        if fileLocal.split(".")[-1] == "java" and "test" not in fileLocal:
            name = ".".join(fileLocal.split(".")[0].split("\\")[10:])
            dict[name] = fileLocal
    return dict


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

"""
def create_schemes(dir_name):
    list_of_file = os.listdir(dir_name)
    all_files = list()
    for entry in list_of_file:
        full_path = os.path.join(dir_name, entry)
        if os.path.isdir(full_path):
            all_files = all_files + get_list_of_files(full_path)
        else:
            all_files.append(full_path)
    return all_files
"""

dot = Digraph(format="svg")
listOfFilesUpdated = []
listOfPaths = []

dict = get_all_java_classes()



# get the list of files inside the specified folder
listOfFiles = get_list_of_files(r".\tucson\inspector")
for fileLocal in listOfFiles:
    if fileLocal.split(".")[-1] == "java" and "test" not in fileLocal:
        name = ".".join(fileLocal.split(".")[0].split("\\")[10:])
        dot.node(name, name, color='red', style='filled', fillcolor='blue')
        listOfFilesUpdated.append(fileLocal)
        listOfPaths.append(name)


for fileLocal in listOfFilesUpdated:
    if fileLocal.split(".")[-1] == "java" and "test" not in fileLocal:
        fileOpened = open(fileLocal, "r")
        classThatImport = get_name_from_path(fileLocal)
        linesRead = fileOpened.readlines()
        for line in linesRead:
            # takes all the imports that are not java nor slf4j
            if line.split(" ")[0] == "import" and "java" not in line and "slf4j" not in line:  # or line.split(" ")[0] == "package":
                classImported = line.split(" ")[-1].split(";")[0]
                # takes all the classes that are not in the package
                if classImported not in listOfPaths:
                    # takes the classes that are in the project
                    if dict.get(classImported):
                        # excludes core classes
                        if "core" not in dict[classImported]:
                            dot.node(classImported, dict[classImported], color='red', style='filled', fillcolor='red')
                            dot.edge(classThatImport, classImported)
                        # core classes imported
                        # else:
                            # dot.node(classImported, dict[classImported], color='red', style='filled', fillcolor='green')
                    else:
                        # tuprolog classes imported
                        # if "tuprolog" in classImported:
                            # dot.node(classImported, classImported, color='red', style='filled', fillcolor='yellow')
                        # from the classes that aren't in the project are excluded tuprolog classes
                        if "core" not in classImported and "tuprolog" not in classImported:
                            dot.node(classImported, classImported, color='red', style='filled', fillcolor='purple')
                            dot.edge(classThatImport, classImported)
        fileOpened.close()

dot.render('graphs/outer-dependencies', view=True)
