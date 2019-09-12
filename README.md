this project is made for Distribuited systems exam at university of Cesena.

There are 2 python programs:
	- list_dir.py:
		for a given path lists the subdirectories, in particular it checks if the directory has inside some java classes, in affirmative case it puts the specified directory as a package.
		
	- dependencies.py that makes a graph with the dependencies of the classes inside and outside the package
	
	for the outer dependencies graph the color red means that the dependency is outside the package but in another package inside the project, while purple color means that the dependency is not found and it doesn't belongs to java core nor to tuprolog (this includes also imports of the entire packages that are not filtered, those are the most common nodes)