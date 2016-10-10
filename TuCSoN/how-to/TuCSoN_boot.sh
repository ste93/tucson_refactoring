#! /bin/sh
if [ $# -lt 1 ]; then
## no args, syntax help
	echo "<==========> \n usage: bash TuCSoN_boot.sh \n\t { Node | CLI | Inspector } {args} to launch respectively: \n\t\t a TuCSoN Node \n\t\t the TuCSoN Command Line Interpreter (for human users) \n\t\t the TuCSoN Graphical Inspector (for human users) \n\t { Hello | PrologHello | JavaHello | Messages | RPC | Workers | Bulk | Dice | Balancing | Bag | Spawn | PrologSpawn | DiningPhilos | DDiningPhilos | TDiningPhilos | Thermostat | Swarms | Persistency | RBAC | AsynchronousAPI } {args} to launch respectively: \n\t\t the used & abused 'Hello World' example \n\t\t the same using a tuProlog (http://tuprolog.unibo.it) agent \n\t\t the same using a Java Tuples (not tuProlog-based as usual) \n\t\t a message passing example exploiting tuple-based coordination to re-create a synchronous exchange of messages between agents \n\t\t a Remote Procedure Call (RPC) example exploiting tuple-based coordination to re-create an rpc call \n\t\t a Master/Workers example exploiting only tuple-based coordination \n\t\t a Master/Workers example exploiting tuple-based coordination through bulk primitives \n\t\t the 'dice player' example, showcasing uniform primitives \n\t\t the 'load balancing' example, showcasing how uniform primitives inject non-functional properties into a system, e.g., load balancing \n\t\t a Master/Workers example exploiting basic ReSpecT programming \n\t\t an example involving basic ReSpecT programming and the <spawn> TuCSoN primitive \n\t\t the same using a tuProlog (http://tuprolog.unibo.it) agent \n\t\t the used & abused Dining Philosophers example \n\t\t the same in a 'distributed' setting (needs a TuCSoN node on port 20504 and one on port 20505) \n\t\t again the philosophers but timed (non-distributed) version \n\t\t an example showcasing ReSpecT situatedness \n\t\t the 'Swarms' example showcasing how a swarm intelligence scenario can be programmed in ReSpecT and upon TuCSoN services \n\t\t an example showcasing persistency of tuple centres content (automatically launches a TuCSoN node on default port. After termination, launch a TuCSoN node on default port then inspect it) \n\t\t an example showcasing TuCSoN support to RBAC (automatically launches a TuCSoN node on default port) \n\t\t an example showcasing TuCSoN support to asynchronous mode of operation \n <==========>"
elif [[ "$1" == -? || "$1" == -help ]]; then
## -?, syntax help
    echo "<==========> \n usage: bash TuCSoN_boot.sh \n\t { Node | CLI | Inspector } {args} to launch respectively: \n\t\t a TuCSoN Node \n\t\t the TuCSoN Command Line Interpreter (for human users) \n\t\t the TuCSoN Graphical Inspector (for human users) \n\t { Hello | PrologHello | JavaHello | Messages | RPC | Workers | Bulk | Dice | Balancing | Bag | Spawn | PrologSpawn | DiningPhilos | DDiningPhilos | TDiningPhilos | Thermostat | Swarms | Persistency | RBAC | AsynchronousAPI } {args} to launch respectively: \n\t\t the used & abused 'Hello World' example \n\t\t the same using a tuProlog (http://tuprolog.unibo.it) agent \n\t\t the same using a Java Tuples (not tuProlog-based as usual) \n\t\t a message passing example exploiting tuple-based coordination to re-create a synchronous exchange of messages between agents \n\t\t a Remote Procedure Call (RPC) example exploiting tuple-based coordination to re-create an rpc call \n\t\t a Master/Workers example exploiting only tuple-based coordination \n\t\t a Master/Workers example exploiting tuple-based coordination through bulk primitives \n\t\t the 'dice player' example, showcasing uniform primitives \n\t\t the 'load balancing' example, showcasing how uniform primitives inject non-functional properties into a system, e.g., load balancing \n\t\t a Master/Workers example exploiting basic ReSpecT programming \n\t\t an example involving basic ReSpecT programming and the <spawn> TuCSoN primitive \n\t\t the same using a tuProlog (http://tuprolog.unibo.it) agent \n\t\t the used & abused Dining Philosophers example \n\t\t the same in a 'distributed' setting (needs a TuCSoN node on port 20504 and one on port 20505) \n\t\t again the philosophers but timed (non-distributed) version \n\t\t an example showcasing ReSpecT situatedness \n\t\t the 'Swarms' example showcasing how a swarm intelligence scenario can be programmed in ReSpecT and upon TuCSoN services \n\t\t an example showcasing persistency of tuple centres content (automatically launches a TuCSoN node on default port. After termination, launch a TuCSoN node on default port then inspect it) \n\t\t an example showcasing TuCSoN support to RBAC (automatically launches a TuCSoN node on default port) \n\t\t an example showcasing TuCSoN support to asynchronous mode of operation \n <==========>"
elif [ "$1" == Node ]; then
## $1=Node, $2=-portno [optional], $3={portno} [mandatory if $2 defined]
## Start TuCSoN node on port 20504 (default) / {portno}
## -help provide syntax help on arguments
java -cp tucson.jar:../libs/2p.jar alice.tucson.service.TucsonNodeService $2 $3
elif [ "$1" == CLI ]; then
## $1=CLI
## $i=-aid [optional], $i+1={agentid} [mandatory if $i defined]
## $j=-netid [optional], $j+1={ipaddress} [mandatory if $j defined]
## $k=-portno [optional], $k+1={portno} [mandatory if $k defined]
## (NB: i!=j!=k)
## Start TuCSoN Command Line Interpreter on port 20504 (default) / {portno} with agentid 'CLI_[localcurrenttime]' (default) / {aid} on node 'localhost' (default) / {ipaddress}
## -help provide syntax help on arguments
java -cp tucson.jar:../libs/2p.jar alice.tucson.service.tools.CommandLineInterpreter $2 $3 $4 $5 $6 $7
elif [ "$1" == Inspector ]; then
## Start TuCSoN Inspector
java -cp tucson.jar:../libs/2p.jar alice.tucson.introspection.tools.InspectorGUI
elif [ "$1" == Hello ]; then
## $1=Hello, $2={aid} [optional]
## Starts the 'Hello World' example with agentid 'helloWorldMain' (default) / {aid}
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.helloWorld.HelloWorldAgent $2
elif [ "$1" == PrologHello ]; then
## $1=PrologHello, $2={theory} [optional], $3={goal} [optional]
## Starts the 'Hello World' tuProlog (tuprolog.unibo.it) example using theory 'alice/tucson/examples/helloWorld/helloWorld.pl' / {theory} with goal 'runHelloWorld(agent-test, default@localhost:20504).' / {goal}
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.helloWorld.PrologHelloWorld $2 $3
elif [ "$1" == JavaHello ]; then
## $1=JavaHello, $2={aid} [optional]
## Starts the 'Hello World' example using Java tuples with agentid 'helloWorldMain' (default) / {aid}
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.helloWorld.HelloWorldJTuples $2
elif [ "$1" == Messages ]; then
## $1=Messages
## Starts a message passing example exploiting tuple-based coordination to re-create a synchronous exchange of messages between agents
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.messagePassing.SenderAgent &
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.messagePassing.ReceiverAgent
elif [ "$1" == Rpc ]; then
## $1=Rpc
## Starts a Remote Procedure Call (RPC) example exploiting tuple-based coordination to re-create an rpc call
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.rpc.CalleeAgent &
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.rpc.CallerAgent
elif [ "$1" == Workers ]; then
## $1=Workers
## Starts a Master/Workers example exploiting only tuple-based coordination
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.masterWorkers.MasterAgent &
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.masterWorkers.WorkerAgent
elif [ "$1" == Bulk ]; then
## $1=Bulk
## Starts a Master/Workers example exploiting tuple-based coordination through bulk primitives
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.masterWorkers.bulk.MasterAgent &
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.masterWorkers.bulk.WorkerAgent
elif [ "$1" == Dice ]; then
## $1=Dice
## Starts the 'dice player' example, showcasing uniform primitives
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.uniform.dice.DicePlayer
elif [ "$1" == Balancing ]; then
## $1=Balancing
## Starts the 'load balancing' example, showcasing how uniform primitives inject non-functional properties into a system, e.g., load balancing
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.uniform.loadBalancing.ServiceProvider &
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.uniform.loadBalancing.ServiceRequestor
elif [ "$1" == Bag ]; then
## $1=Bag
## Starts a Master/Workers example exploiting basic ReSpecT programming
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.respect.bagOfTask.BagOfTaskTest
elif [ "$1" == Spawn ]; then
## $1=Spawn
## Starts a Master/Workers example exploiting basic ReSpecT programming and spawned (Java) activities
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.spawnedWorkers.MasterAgent $2 $3
elif [ "$1" == PrologSpawn ]; then
## $1=PrologSpawn
## Starts a Master/Workers example exploiting basic ReSpecT programming and spawned (tuProlog - tuprolog.unibo.it) activities using Master theory 'alice/tucson/examples/spawnedWorkers/masterAgent.pl' / {theory} with goal 'runMasterAgent(master-agent, [default@localhost:20504], 10, 5).' / {goal}
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.spawnedWorkers.PrologMasterAgent $2 $3
elif [ "$1" == DiningPhilos ]; then
## $1=DiningPhilos
## Starts the 'Dining Philosophers' example exploiting basic ReSpecT programming
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.diningPhilos.DiningPhilosophersTest
elif [ "$1" == DDiningPhilos ]; then
## $1=DDiningPhilos
## Starts the 'Dining Philosophers' example exploiting basic ReSpecT programming in a distributed setting
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.distributedDiningPhilos.DDiningPhilosophersTest
elif [ "$1" == TDiningPhilos ]; then
## $1=TDiningPhilos
## Starts the 'Timed Dining Philosophers' example exploiting timed ReSpecT programming in a non-distributed setting
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.timedDiningPhilos.TDiningPhilosophersTest
elif [ "$1" == Thermostat ]; then
## $1=Thermostat
## Starts the 'Thermostat' example exploiting situated ReSpecT programming in a non-distributed setting
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.situatedness.Thermostat
elif [ "$1" == Swarms ]; then
## $1=Swarms
## Starts the 'Swarms' example showcasing how a swarm intelligence scenario can be programmed in ReSpecT and upon TuCSoN services
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.uniform.swarms,launchers.LaunchSwarmWithGUI
elif [ "$1" == Persistency ]; then
## $1=Persistency
## Starts the 'Persistency' example exploiting TuCSoN persistency feature allowing to save the state of tuple centres to be later recovered
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.persistency.PersistencyTester
elif [ "$1" == RBAC ]; then
## $1=RBAC
## Starts the 'RBAC' example exploiting TuCSoN support to RBAC allowing the infrastructure to allow/deny oeprations based on agents' roles
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.rbac.RBACLauncher
elif [ "$1" == AsynchronousAPI ]; then
## $1=AsynchronousAPI
## Starts the 'AsynchronousAPI' example exploiting TuCSoN asynchronous mode of operation allowing agents to request blocking coordination operations without being suspended in case of failure, but being notified upon completion
java -cp tucson.jar:../libs/2p.jar alice.tucson.examples.asynchAPI.PrimeCalculationLauncher
else
    echo "<==========> \n usage: bash TuCSoN_boot.sh \n\t { Node | CLI | Inspector } {args} to launch respectively: \n\t\t a TuCSoN Node \n\t\t the TuCSoN Command Line Interpreter (for human users) \n\t\t the TuCSoN Graphical Inspector (for human users) \n\t { Hello | PrologHello | JavaHello | Messages | RPC | Workers | Bulk | Dice | Balancing | Bag | Spawn | PrologSpawn | DiningPhilos | DDiningPhilos | TDiningPhilos | Thermostat | Swarms | Persistency | RBAC | AsynchronousAPI } {args} to launch respectively: \n\t\t the used & abused 'Hello World' example \n\t\t the same using a tuProlog (http://tuprolog.unibo.it) agent \n\t\t the same using a Java Tuples (not tuProlog-based as usual) \n\t\t a message passing example exploiting tuple-based coordination to re-create a synchronous exchange of messages between agents \n\t\t a Remote Procedure Call (RPC) example exploiting tuple-based coordination to re-create an rpc call \n\t\t a Master/Workers example exploiting only tuple-based coordination \n\t\t a Master/Workers example exploiting tuple-based coordination through bulk primitives \n\t\t the 'dice player' example, showcasing uniform primitives \n\t\t the 'load balancing' example, showcasing how uniform primitives inject non-functional properties into a system, e.g., load balancing \n\t\t a Master/Workers example exploiting basic ReSpecT programming \n\t\t an example involving basic ReSpecT programming and the <spawn> TuCSoN primitive \n\t\t the same using a tuProlog (http://tuprolog.unibo.it) agent \n\t\t the used & abused Dining Philosophers example \n\t\t the same in a 'distributed' setting (needs a TuCSoN node on port 20504 and one on port 20505) \n\t\t again the philosophers but timed (non-distributed) version \n\t\t an example showcasing ReSpecT situatedness \n\t\t the 'Swarms' example showcasing how a swarm intelligence scenario can be programmed in ReSpecT and upon TuCSoN services \n\t\t an example showcasing persistency of tuple centres content (automatically launches a TuCSoN node on default port. After termination, launch a TuCSoN node on default port then inspect it) \n\t\t an example showcasing TuCSoN support to RBAC (automatically launches a TuCSoN node on default port) \n\t\t an example showcasing TuCSoN support to asynchronous mode of operation \n <==========>"
fi
