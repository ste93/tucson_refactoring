import os
from graphviz import Digraph
import json

#path origin of the project
initialPath = r".\sd-project-bernagozzi-1617"

# package name or path
packageName = "service"

# words to be excluded in the searching of imports
wordsToExcludeInImport = ["java", "slf4j"]
wordsToExcludeInInnerGraph = [""]

# the verbose mode check for all the classes also with their names inside the code
verboseMode = False

# additional structures of the project
importNameToPath = {}
pathToImportName = {}
nameToFullName = {}
classesThatImportsTheClass = {}

javaClassList = []
listOfFilesUpdated = []
listOfPaths = []

innerGraph = Digraph(format="svg", strict=True)
outerGraph = Digraph(format="svg", strict=True)


# list all the files in the directory
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


# lists all java classes inside the project and put the name and the path in 2 different dictionaries
def get_all_java_classes(listOfFiles):
    for fileLocal in listOfFiles:
        if fileLocal.split(".")[-1] == "java" and "test" not in fileLocal:
            fileOpened = open(fileLocal, "r")
            linesRead = fileOpened.readlines()
            for line in linesRead:
                # takes all the imports that are not java nor slf4j
                if line.split(" ")[0] == "package":  # or line.split(" ")[0] == "package":
                    package = line.split(" ")[-1].split(";")[0]
                    className = fileLocal.split(".")[1].split("\\")[-1]
                    importString = package + "." + className
                    javaClassList.append(className)
                    nameToFullName[className] = importString
                    importNameToPath[importString] = fileLocal
                    pathToImportName[fileLocal] = importString
            fileOpened.close()


def fill_dictionaries(file_local):
    if file_local.split(".")[-1] == "java" and "test" not in file_local:
        name = pathToImportName[fileLocal]
        print(name)
        outerGraph.node(name, name, color='red', style='filled', fillcolor='blue')
        innerGraph.node(name, name, color='red', style='filled', fillcolor='blue')
        listOfFilesUpdated.append(file_local)
        listOfPaths.append(name)


def path_contains_list_of_words(path, word_list):
    to_return = False
    for word in word_list:
        if word in path:
            to_return = True
    return to_return

def add_to_imports(class_imported, class_that_import):
    if not classesThatImportsTheClass.get(class_imported):
        classesThatImportsTheClass[class_imported] = set()
    classesThatImportsTheClass[class_imported].add(class_that_import)


def get_dependencies_per_file(file_local):
    inner_dependencies_for_class = []
    if file_local.split(".")[-1] == "java" and "test" not in file_local:
        file_opened = open(file_local, "r")
        class_that_import = pathToImportName[file_local]
        lines_read = file_opened.readlines()
        for line in lines_read:
            if line.split(" ")[0] == "import" and not path_contains_list_of_words(line, wordsToExcludeInImport):
                class_imported = line.split(" ")[-1].split(";")[0]
                if class_imported not in listOfPaths:
                    # takes the classes that are in the project
                    if importNameToPath.get(class_imported):
                        # excludes core classes
                        if "core" not in importNameToPath[class_imported]:
                            outerGraph.node(class_imported, importNameToPath[class_imported], color='red', style='filled', fillcolor='red')
                            outerGraph.edge(class_that_import, class_imported)
                    else:
                        if "core" not in class_imported and "tuprolog" not in class_imported:
                            outerGraph.node(class_imported, class_imported, color='red', style='filled', fillcolor='purple')
                            outerGraph.edge(class_that_import, class_imported)
                else:
                    add_to_imports(class_imported, class_that_import)
                    innerGraph.edge(class_that_import, class_imported)
                    # inner_dependencies_for_class.append(importNameToPath.get(class_imported))
            else:
                if not line.split(" ")[0] == "package":
                    for name in javaClassList:
                        # TODO: filter missing
                        if name in line:
                            class_imported = nameToFullName.get(name)
                            if class_imported != class_that_import:
                                if class_imported in listOfPaths:
                                    if packageName in importNameToPath.get(class_imported):
                                    # innerGraph.node(nameToFullName.get(name), nameToFullName.get(name), color='red', style='filled', fillcolor='blue')
                                        add_to_imports(class_imported, class_that_import)
                                        innerGraph.edge(class_that_import, class_imported)
                                else:
                                    if verboseMode:
                                        if packageName not in importNameToPath.get(class_imported) and not path_contains_list_of_words(line, wordsToExcludeInImport) \
                                                and 'core' not in importNameToPath.get(class_imported):
                                            outerGraph.node(class_imported, importNameToPath.get(class_imported), color='red', style='filled',
                                                            fillcolor='red')
                                            outerGraph.edge(class_that_import, class_imported)

        file_opened.close()


# get the list of files inside the specified folders
get_all_java_classes(get_list_of_files(initialPath))
listOfFiles = get_list_of_files(initialPath + "\\" + packageName )

# create a node for each class in the package
for fileLocal in listOfFiles:
    fill_dictionaries(fileLocal)


# for each file checks if the imports are inside or outside the package, in affirmative case it
for fileLocal in listOfFilesUpdated:
    get_dependencies_per_file(fileLocal)


print(classesThatImportsTheClass.get(nameToFullName.get("CompletedOpsQueue")))

outerGraph.render('graphs/outer-dependencies-' + packageName, view=True)
# innerGraph.render('graphs/inner-dependencies-' + packageName, view=True)

