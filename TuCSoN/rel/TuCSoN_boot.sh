#! /bin/sh
if [ $# -lt 1 ]; then
## no args, syntax help
	echo "<==========> \n usage: bash TuCSoN_boot.sh \n\t { Node | CLI | Inspector } {args} to launch respectively: \n\t\t a TuCSoN Node \n\t\t the TuCSoN Command Line Interpreter (for human users) \n\t\t the TuCSoN Graphical Inspector (for human users) \n\t { Hello | PrologHello | JavaHello | Spawn | PrologSpawn | DiningPhilos | DDiningPhilos | TDiningPhilos | Thermostat | Persistency } {args} to launch respectively: \n\t\t the used & abused 'Hello World' example \n\t\t the same using a tuProlog (http://tuprolog.unibo.it) agent \n\t\t the same using a Java Tuples (not tuProlog-based as usual) \n\t\t an example involving basic ReSpecT programming and the <spawn> TuCSoN primitive \n\t\t the same using a tuProlog (http://tuprolog.unibo.it) agent \n\t\t the used & abused Dining Philosophers example \n\t\t the same in a 'distributed' setting (needs a TuCSoN node on port 20504 and one on port 20505) \n\t\t again the philosophers but timed (non-distributed) version \n\t\t an example showcasing ReSpecT situatedness \n\t\t an example showcasing persistency of tuple centres content (automatically launches a TuCSoN node on default port. After termination, launch a TuCSoN node on default port then inspect it) \n <==========>"
elif [[ "$1" == -? || "$1" == -help ]]; then
## -?, syntax help
	echo "<==========> \n usage: bash TuCSoN_boot.sh \n\t { Node | CLI | Inspector } {args} to launch respectively: \n\t\t a TuCSoN Node \n\t\t the TuCSoN Command Line Interpreter (for human users) \n\t\t the TuCSoN Graphical Inspector (for human users) \n\t { Hello | PrologHello | JavaHello | Spawn | PrologSpawn | DiningPhilos | DDiningPhilos | TDiningPhilos | Thermostat | Persistency } {args} to launch respectively: \n\t\t the used & abused 'Hello World' example \n\t\t the same using a tuProlog (http://tuprolog.unibo.it) agent \n\t\t the same using a Java Tuples (not tuProlog-based as usual) \n\t\t an example involving basic ReSpecT programming and the <spawn> TuCSoN primitive \n\t\t the same using a tuProlog (http://tuprolog.unibo.it) agent \n\t\t the used & abused Dining Philosophers example \n\t\t the same in a 'distributed' setting (needs a TuCSoN node on port 20504 and one on port 20505) \n\t\t again the philosophers but timed (non-distributed) version \n\t\t an example showcasing ReSpecT situatedness \n\t\t an example showcasing persistency of tuple centres content (automatically launches a TuCSoN node on default port. After termination, launch a TuCSoN node on default port then inspect it) \n <==========>"
elif [ "$1" == Node ]; then
## $1=Node, $2=-portno [optional], $3={portno} [mandatory if $2 defined]
## Start TuCSoN node on port 20504 (default) / {portno}
## -help provide syntax help on arguments
java -cp TuCSoN-1.11.0.0209.jar:2p.jar alice.tucson.service.TucsonNodeService $2 $3
elif [ "$1" == CLI ]; then
## $1=CLI
## $i=-aid [optional], $i+1={agentid} [mandatory if $i defined]
## $j=-netid [optional], $j+1={ipaddress} [mandatory if $j defined]
## $k=-portno [optional], $k+1={portno} [mandatory if $k defined]
## (NB: i!=j!=k)
## Start TuCSoN Command Line Interpreter on port 20504 (default) / {portno} with agentid 'CLI_[localcurrenttime]' (default) / {aid} on node 'localhost' (default) / {ipaddress}
## -help provide syntax help on arguments
java -cp TuCSoN-1.11.0.0209.jar:2p.jar alice.tucson.service.tools.CommandLineInterpreter $2 $3 $4 $5 $6 $7
elif [ "$1" == Inspector ]; then
## Start TuCSoN Inspector
java -cp TuCSoN-1.11.0.0209.jar:2p.jar alice.tucson.introspection.tools.InspectorGUI
elif [ "$1" == Hello ]; then
## $1=Hello, $2={aid} [optional]
## Starts the 'Hello World' example with agentid 'helloWorldMain' (default) / {aid}
java -cp TuCSoN-1.11.0.0209.jar:2p.jar alice.tucson.examples.helloWorld.HelloWorldAgent $2
elif [ "$1" == PrologHello ]; then
## $1=PrologHello, $2={theory} [optional], $3={goal} [optional]
## Starts the 'Hello World' tuProlog (tuprolog.unibo.it) example using theory 'alice/tucson/examples/helloWorld/helloWorld.pl' / {theory} with goal 'runHelloWorld(agent-test, default@localhost:20504).' / {goal}
java -cp TuCSoN-1.11.0.0209.jar:2p.jar alice.tucson.examples.helloWorld.PrologHelloWorld $2 $3
elif [ "$1" == JavaHello ]; then
## $1=JavaHello, $2={aid} [optional]
## Starts the 'Hello World' example using Java tuples with agentid 'helloWorldMain' (default) / {aid}
java -cp TuCSoN-1.11.0.0209.jar:2p.jar alice.tucson.examples.helloWorld.HelloWorldJTuples $2
elif [ "$1" == Spawn ]; then
## $1=Spawn
## Starts a Master/Workers example exploiting basic ReSpecT programming and spawned (Java) activities
java -cp TuCSoN-1.11.0.0209.jar:2p.jar alice.tucson.examples.spawnedWorkers.MasterAgent $2 $3
elif [ "$1" == PrologSpawn ]; then
## $1=PrologSpawn
## Starts a Master/Workers example exploiting basic ReSpecT programming and spawned (tuProlog - tuprolog.unibo.it) activities using Master theory 'alice/tucson/examples/spawnedWorkers/masterAgent.pl' / {theory} with goal 'runMasterAgent(master-agent, [default@localhost:20504], 10, 5).' / {goal}
java -cp TuCSoN-1.11.0.0209.jar:2p.jar alice.tucson.examples.spawnedWorkers.PrologMasterAgent $2 $3
elif [ "$1" == DiningPhilos ]; then
## $1=DiningPhilos
## Starts the 'Dining Philosophers' example exploiting basic ReSpecT programming
java -cp TuCSoN-1.11.0.0209.jar:2p.jar alice.tucson.examples.diningPhilos.DiningPhilosophersTest
elif [ "$1" == DDiningPhilos ]; then
## $1=DDiningPhilos
## Starts the 'Dining Philosophers' example exploiting basic ReSpecT programming in a distributed setting
java -cp TuCSoN-1.11.0.0209.jar:2p.jar alice.tucson.examples.distributedDiningPhilos.DDiningPhilosophersTest
elif [ "$1" == TDiningPhilos ]; then
## $1=TDiningPhilos
## Starts the 'Timed Dining Philosophers' example exploiting timed ReSpecT programming in a non-distributed setting
java -cp TuCSoN-1.11.0.0209.jar:2p.jar alice.tucson.examples.timedDiningPhilos.TDiningPhilosophersTest
elif [ "$1" == Thermostat ]; then
## $1=Thermostat
## Starts the 'Thermostat' example exploiting situated ReSpecT programming in a non-distributed setting
java -cp TuCSoN-1.11.0.0209.jar:2p.jar alice.tucson.examples.situatedness.Thermostat
elif [ "$1" == Persistency ]; then
## $1=Persistency
## Starts the 'Persistency' example exploiting TuCSoN persistency feature allowing to save the state of tuple centres to be later recovered
java -cp TuCSoN-1.11.0.0209.jar:2p.jar alice.tucson.examples.persistency.PersistencyTester
else
	echo "<==========> \n usage: bash TuCSoN_boot.sh \n\t { Node | CLI | Inspector } {args} to launch respectively: \n\t\t a TuCSoN Node \n\t\t the TuCSoN Command Line Interpreter (for human users) \n\t\t the TuCSoN Graphical Inspector (for human users) \n\t { Hello | PrologHello | JavaHello | Spawn | PrologSpawn | DiningPhilos | DDiningPhilos | TDiningPhilos | Thermostat | Persistency } {args} to launch respectively: \n\t\t the used & abused 'Hello World' example \n\t\t the same using a tuProlog (http://tuprolog.unibo.it) agent \n\t\t the same using a Java Tuples (not tuProlog-based as usual) \n\t\t an example involving basic ReSpecT programming and the <spawn> TuCSoN primitive \n\t\t the same using a tuProlog (http://tuprolog.unibo.it) agent \n\t\t the used & abused Dining Philosophers example \n\t\t the same in a 'distributed' setting (needs a TuCSoN node on port 20504 and one on port 20505) \n\t\t again the philosophers but timed (non-distributed) version \n\t\t an example showcasing ReSpecT situatedness \n\t\t an example showcasing persistency of tuple centres content (automatically launches a TuCSoN node on default port. After termination, launch a TuCSoN node on default port then inspect it) \n <==========>"
fi
