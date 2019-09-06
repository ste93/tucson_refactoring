import os
from graphviz import Digraph
import json


def get_name_from_path(path):
    return ".".join(path.split(".")[0].split("\\")[10:])


dot = Digraph(format="svg")


def get_list_of_files(dir_name):
    list_of_file = os.listdir(dir_name)
    to_return = False
    for entry in list_of_file:
        full_path = os.path.join(dir_name, entry)
        if os.path.isdir(full_path):
            if get_list_of_files(full_path):
                dot.node(dir_name, dir_name, color='red', style='filled', fillcolor='yellow')
                dot.node(full_path, full_path, color='red', style='filled', fillcolor='yellow')
                dot.edge(dir_name, full_path)
        else:
            if "java" in full_path:
                to_return = True

    return to_return


dot = Digraph(format="svg")
dir_name = r".\tucson\service\src\main\java"
get_list_of_files(dir_name)
dot.render('graphs/directories-service', view=True)
