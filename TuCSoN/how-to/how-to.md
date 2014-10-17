# TuCSoN "how-to"

In this brief "how-to", you will learn how to get TuCSoN, build it and run examples showcasing its features. <!--You will also learn main API available to JADE developers and how to use them in your code, for your JADE agents.-->

Assumptions are you are familiar with Java (compilation), Git (cloning repositories) and, optionally, ANT (buildfiles).

---

1. <a href="#getting">Getting Started with t4j</a>
   
   1.1 <a href="#downloading">Downloading</a>
   
   1.2 <a href="#compiling">Compiling</a>
   
   1.3 <a href="#deploying">Deploying</a>
   
   1.4 <a href="#running">Running</a>
   
<!--2. <a href="#using">Using t4j</a>

   2.1 <a href="#api">API Overview</a>
   
   2.2 <a href="#hands-on">"Hands-on" step-by-step tour</a>-->
   
3. <a href="#contact">Contact Information</a>

---

### 1. <a name="getting">Getting Started with TuCSoN</a>

###### 1.1 <a name="downloading">Downloading</a>

If you want the *ready-to-use* distribution of TuCSoN, download **TuCSoN-1.11.0.0209.jar** archive from the "Downloads" section, here > <http://bitbucket.org/smariani/tucson/downloads>. **TuCSoN_boot.sh** bash script is also available for quick boot of TuSCoN components and examples.

If you want the *source code* of TuCSoN, clone the TuCSoN **[Git](http://git-scm.com) repository** hosted here on Bitbucket, at > <http://smariani@bitbucket.org/smariani/tucson.git> (e.g., from a command prompt type `$> git clone https://smariani@bitbucket.org/smariani/tucson.git`) <a href="#1">\[1\]</a>.

In the former case, skip to the "<a href="#running">Running</a>" section below. In the latter case, keep reading.

###### 1.2 <a name="compiling">Compiling</a>

By cloning TuCSoN you have downloaded a folder named `tucson/`, with the following *directory structure*:

    tucson/
    |__...
    |__TuCSoN/
       |__...
       |__ant-scripts/
          |__build.xml
          |__environment.properties
       |__eclipse-config/
       |__how-to/
       |__license-info/
       |__trunk/
          |__src/

TuCSoN depends on 1 other Java library to function properly <a href="#2">\[2\]</a>:

 * **tuProlog**, downloadable from tuProlog "Download" page, here > <http://apice.unibo.it/xwiki/bin/view/Tuprolog/Download> (**2p.jar**)
 
Once you got the above libraries, you are ready to compile TuCSoN source code. 

The easiest way to do so is by exploiting the [ANT](http://ant.apache.org) script named `build.xml` within folder `ant-scripts/`, which takes care of the whole building process for you, from compilation to deployment (covered in next section). To do so, you need to have ANT installed on your machine <a href="#3">\[3\]</a>. If you don't want to use ANT, build TuCSoN jar archive using the tools you prefer, then skip to the "<a href="#running">Running</a>" section below.

To compile TuCSoN using ANT:

 1. Edit the `environment.properties` file according to your system configuration:
 
    1.1 Tell ANT where your JDK and your `java` tool are
    
    1.2 Tell ANT which libraries are needed to compile TuCSoN (the one you just downloaded, that is tuProlog)
    
    1.3 Tell ANT where you put such libraries (e.g. if you put them into `TuCSoN/libs/` you are already set)
    
    *\[1.4 Tell ANT your Bitbucket username (for automatic syncing with TuCSoN repository, **not supported at the moment**)\]*
 
 2. Launch the ANT script using target `compile` (e.g., from a command prompt position yourself into the `ant-scripts/` folder then type `$> ant compile`) <a href="#4">\[4\]</a>. This will create folder `classes/` within folder `TuCSoN/` and therein store Java `.class` files.
 
Other ANT targets are available through the `build.xml` file: to learn which, launch the ANT script using target `help`.

###### <a name="deploying">Deploying</a>

Deploying TuCSoN is as simple as giving a different build target to the ANT script `build.xml`:

 * if you only want the **TuCSoN jar** archive, ready to be included in your Java project, launch the script using target `lib`. This will compile TuCSoN source code into binaries (put into `TuCSoN/classes/` folder) then package them to **TuCSoN-1.11.0.0209.jar** into `TuCSoN/lib/` folder <a href="#5">\[5\]</a>.
 
 * if you want a **ready-to-release distribution** of TuCSoN, including also documentation and support libraries, launch the script using target `dist`. This will:
   
   * compile TuCSoN source code into binaries, put into `TuCSoN/classes/` folder
   * package them to TuCSoN-1.11.0.0209.jar, put into `TuCSoN/lib/` folder
   * generate Javadoc information, put into `TuCSoN/doc/` folder
   * create folder `rel/TuCSoN-1.11.0.0209` including:
   
     * folder `docs/` including the generated Javadoc information as well as this "how-to"
     * folder `libs/` including tuProlog library used to build TuCSoN
     * folder `rel/` including TuCSoN jar archives
     
The complete directory structure obtained by launching `ant dist` build process should look like the following (assuming you put tuProlog library in folder `TuCSoN/libs/`):

    tucson/
    |__...
    |__TuCSoN/
       |__...
       |__ant-scripts/
          |__build.xml
          |__environment.properties
       |__classes/
       |__doc/
       |__eclipse-config/
       |__how-to/
       |__rel/
          |__TuCSoN4JADE-1.0/
             |__docs/
                |__how-to/
                |__javadoc/
             |__libs/
             |__rel/
          |__...
       |__lib/
       |__libs/
       |__license-info/
       |__trunk/
          |__src/

Other ANT targets are available through the `build.xml` file: to learn which, launch the ANT script using target `help`.

###### <a name="running">Running</a>

To run TuCSoN, you need:

 * TuCSoN jar, e.g. **TuCSoNTuCSoN-1.11.0.0209.jar**
 * tuProlog jar, e.g. **2p.jar**

Supposing you built TuCSoN using the provided ANT script <a href="#6">\[6\]</a> and that you are comfortable with using a command prompt to launch Java applications <a href="#7">\[7\]</a>:

 1. open a command prompt and position yourself into either `TuCSoN/lib/` or `TuCSoN/rel/TuCSoN-1.11.0.0209/rel/` folder
 2. launch the TuCSoN Node service, e.g. as follows <a href="#8">\[8\]</a>:
 
         java -cp TuCSoN-1.11.0.0209.jar:../libs/2p.jar alice.tucson.service.TucsonNodeService

The TuCSoN ASCII logo on the command prompt, as depicted below.

<img src="TuCSoN-boot.png" alt="TuCSoN in execution" height="280" width="600">

As long as no TuCSoN agents start exploiting TuCSoN coordination services, nothing happens. Thus, here follows instructions on how to launch one of the example applications shipped within TuCSoN-1.11.0.0209.jar, showcasing its features: the *old-but-gold* "Dining Philosophers" example (package `alice.tucson.examples.timedDiningPhilos.TDiningPhilosophersTest`).

Supposing you successfully launched the TuCSoN Node Service as described above, to launch the "Dining Philosophers" example:
      
 1. open a new command prompt window/tab and position yourself into either `TuCSoN/lib/` or `TuCSoN/rel/TuCSoN-1.11.0.0209/rel/` folder
 2. launch the "Dining Philosophers" example, e.g. as follows <a href="#8">\[8\]</a>:
 
         java -cp TuCSoN-1.11.0.0209.jar:../libs/2p.jar alice.tucson.examples.timedDiningPhilos.TDiningPhilosophersTest

You should see many prints on the command prompt, tracking what the philosophers are doing.

---

<!--### <a name="api">API Overview</a>

The first step to integrate TuCSoN and JADE has been implementing TuCSoN *as a* JADE *service*. Thus, JADE `BaseService` class has been extended with a class representing the TuCSoN service entry point: in TuCSoN4JADE, **the `TucsonService` class**. This class **should be used to get an helper class** extending JADE `ServiceHelper` interface, working as the interface between the clients and the service. In TuCSoN4JADE, the helper role is played by the **`TucsonHelper`** interface---whose implementation class is hidden to clients. Its methods are quite self-explanatory (if you know TuCSoN terminology) and are listed in the picture below.

<img src="helper-bridge.png" alt="TucsonHelper & BridgeToTucson" height="300" width="600">

The only "unusual" method is `getBridgeToTucson`: **`BridgeToTucson` is the class which `TucsonHelper` delegates TuCSoN coordination operations invocation to**. To this end, `BridgeToTucson` exposes the following API:


 * `synchronousInvocation()` — lets clients *synchronously* invoke TuCSoN coordination operations. In particular, given a coordination operation to perform (`AbstractTucsonAction` subtypes), a maximum waiting time to be possibly suspended for (`timeout`), and a reference to the caller Jade behaviour, the chosen coordination operation is requested to the active TuCSoN service synchronously w.r.t. the caller behaviour. This means **the caller behaviour is (possibly) suspended and automatically resumed by the TuCSoN4JADE bridge as soon as the requested operation completes**—--returning the completion event reified by the `TucsonOpCompletionEvent` object. Such mechanism encourages JADE programmers using the TuCSoN4JADE bridge to adopt the same programming style suggested by the JADE Programmers Guide (available from <http://jade.tilab.com> after registering) regarding message reception---depicted both for JADE (top) and TuCSoN4JADE (bottom) in picture below:


      1. the communication method – `synchronousInvocation()` in TuCSoN4JADE, `receive()` in JADE – is first called

      
      2. the result is checked, and (i) handled, if available, (ii) otherwise method `block()` is called

                @Override
                public void action () {
                    // field 'mt' stores the ACL message template
                    final ACLMessage msg = myAgent.receive(mt);
                    if (msg != null) { // message received: process it
                        ...
                    } else { // message not received yet: wait
                        block();
                    }
                }

                @Override
                public void action () {
                    // field 'tuple' stores the TuCSoN tuple template
                    final Rd op = new Rd(tcid, tuple);
                    final TucsonOpCompletionEvent
                            res = bridge.synchronousInvocation(op, null, this);
                    if (res != null) { // tuple found: process it
                        ...
                    } else { // tuple not found: wait
                        block();
                    }
                }

   This allows the JADE runtime – through the behaviours scheduler – to **keep on scheduling others behaviours belonging to the caller agent while the coordination operation invoking behaviour remains suspended** (within JADE waiting queue).
   
   * `asynchronousInvocation()` — lets clients *asynchronously* invoke TuCSoN coordination operations. Such method comes in two flavours:
 
   * the first (in black) works *by interrupt* — that is, **when the requested operation completes, the JADE behaviour passed in as an actual parameter is activated** (put in the ready queue, thus ready to be scheduled) to handle the operation result
   * the second (in red) works *by polling* — that is, **the caller agent gets a data structure** (`AsynchTucsonOpResult`, depicted below) **representing the operation result, which it may query to check completion and (eventually) retrieve the actual result**.
   
   **In both cases, regardless of whether the coordination operation suspends or not, the agent does not**, thus the caller behaviour keeps on executing.

<img src="bridge-res.png" alt="AsynchTucsonOpResult" height="100" width="360">

The "result-handling" behaviour written by JADE programmers should implement the TuCSoN4JADE `IAsynchCompletionBehaviour` interface: the `setTucsonOpCompletionEvent()` method is the "hook" for TuCSoN4JADE to share completion events between the caller and the "result handler" behaviour, transparently to JADE programmers.

As a last note, the type hierarchy representing TuCSoN coordination operations is in package `it.unibo.tucson.sd.jade.operations` as depicted in figure below.

<img src="actions.png" alt="TuCSoN operations as JADE actions" height="180" width="520">


### <a name="hands-on">"Hands-on" step-by-step tour</a>

---

### ! Disclaimer !

**NB:** TuCSoN4JADE has been tested against **JDK 6**, **7**, **8**, using **TuCSoN-1.10.8.0208**, **tuProlog-2.8.0** and **JADE v. 4.3.1**. Other combinations may not function properly.

---

What follows is a "hands-on how-to" describing how to exploit TuCSoN4JADE API: what classes you should know, what you have to develop on your own, etc.

**Prior to reading this how-to is *highly recommended* to read the reference paper on integrating TuCSoN and JADE**:

 * Mariani, S., Omicini, A., Sangiorgi, L.: *"Models of Autonomy and Coordination:
   Integrating Subjective & Objective Approaches in Agent Development Frameworks"*.
   Published in 8th International Symposium on Intelligent Distributed Computing
   (IDC 2014), 3-5 September 2014
   
available from here > <http://apice.unibo.it/xwiki/bin/view/Publications/ObjsubjIdc2014>

If you want to work with TuCSoN4JADE, first of all, **remember to instruct the JADE platform to boot the TuCSoN service**, as explained in the beginning of this how-to.

Then, diving into the code, regardless of how you are willing to exploit TuCSoN services, you need to:

 1. get the service helper class from the TuCSoN service instance
 
         ITucsonHelper helper = (TucsonHelper) this.getHelper(TucsonService.NAME);
         
 2. [OPTIONAL] start the TuCSoN node you wish to operate on
 
         if (!this.helper.isActive(20504)) {
             this.helper.startTucsonNode(20504);
         }
         
 3. get an ACC (*which is actually associated to the* `BridgeToTucson` *object* you'll
    get from the helper in step 4)
 
         this.helper.acquireACC(this);
         
 4. get the bridge object *through which all your TuCSoN operations will go*
 
         BridgeToTucson bridge = this.helper.getBridgeToTucson(this);
         
 5. [OPTIONAL] stop the TuCSoN node
 
         if (this.helper.isActive(20504)) {
             this.helper.stopTucsonNode(20504);
         }

Now, what to do next obviously depends on what your program logic needs. Anyway, you will likely perform some of the following operations:

 * build the identifier of the tuple centre you wish to use (e.g., "hello" on
   default TuCSoN node)
 
        TucsonTupleCentreId tcid = this.helper.buildTucsonTupleCentreId(
                         "default", "localhost", 20504);
                    
 * build the tuples you need (using usual TuCSoN facilities)
 
        LogicTuple adv = LogicTuple.parse(
                         "advertise(provider("
                             + this.getAID().getName()
                             + "), service('book-trading')))");
                    
 * *build an "action"*, representing TuCSoN (meta-)coordination operations,
   choosing from all the type hierarchy rooted in `it.unibo.sd.jade.operations.AbstractTucsonAction`
    
        Out out = new Out(this.tcid, this.adv);
        
 * perform the action according to your preferred *invocation semantics* (e.g.
   asynchronous, "polling" mode)
 
        AsynchTucsonOpResult res = this.bridge.asynchronousInvocation(out);
        
   (e.g. asynchronous, "interrupt" mode)
   
        this.bridge.asynchronousInvocation(out,
                    new AdvertisingCompletedBehaviour(this.adv), this);
                    
        private class AdvertisingCompletedBehaviour extends OneShotBehaviour
            implements IAsynchCompletionBehaviour {...}
            
   (e.g. synchronous mode)
   
        TucsonOpCompletionEvent result =
                BookSellerAgent.this.bridge.synchronousInvocation(in, null, this);
                
   remembering, if needed, to exploit JADE's usual programming pattern
   
        if (result != null) { // tuple found: process it
            ...
        } else { // tuple not found: wait
            block();
        }

---
-->
### <a name="contact">Contact Information</a>

**Author** of this "how-to":

 * *Stefano Mariani*, DISI - Università di Bologna (<s.mariani@unibo.it>)

**Authors** of TuCSoN in TuCSoN "People" section of its main site, here > <http://apice.unibo.it/xwiki/bin/view/TuCSoN/People>
 
---

<a name="1">\[1\]</a> Git standalone clients are available for any platform (e.g., [SourceTree](http://www.sourcetreeapp.com) for Mac OS and Windows). Also, if you are using [Eclipse IDE](http://www.eclipse.org/home/index.php) for developing in JADE, the [EGit plugin](http://marketplace.eclipse.org/content/egit-git-team-provider) is included in the [Java Developers version](http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/lunasr1) of the IDE.

<a name="2">\[2\]</a> Recommended tuProlog version is **2.9.1**. Others (both newer and older) may work properly, but they have not been tested.

<a name="3">\[3\]</a> Binaries available [here](http://ant.apache.org/bindownload.cgi), installation instructions covering Linux, MacOS X, Windows and Unix systems [here](http://ant.apache.org/manual/install.html).

<a name="4">\[4\]</a> If you are using [Eclipse IDE](http://www.eclipse.org/home/index.php) for developing in Java, ANT is included: click "Window > Show View > Ant" then click "Add buildfiles" from the ANT view and select file `build.xml` within `ant-scripts/` folder. Now expand the "TuCSoN build file" from the ANT view and finally double click on target `compile` to start the build process.

<a name="5">\[5\]</a> Actually, also a **TuCSoN-1.11.0.0209-noexamples.jar** is built. It is the same as **TuCSoN-1.11.0.0209.jar** except for the explanatory examples in package `alice.tucson.examples.*`, which are excluded.

<a name="6">\[6\]</a> If you directly downloaded TuCSoN jar or if you built it from sources without using the provided ANT script, simply adjust the given command to suit your configuration.

<a name="7">\[7\]</a> If you do not want to use the command prompt to launch Java applications, adjust the given command to suit your configuration, e.g., if your are using [Eclipse IDE](http://www.eclipse.org/home/index.php): right-click on "TuCSoN-1.11.0.0209.jar > Run As > Run Configurations..." then double-click on "Java Application", finally select "TucsonNodeService - alice.tucson.service" as the main class (`-cp TuCSoN-1.11.0.0209.jar:../libs/2p.jar` is automatically added by Eclipse according to project's build path settings).

<a name="8">\[8\]</a> Separator `:` works on Mac & Linux only, use `;` on Windows.

<a name="9">\[9\]</a> Actually, a TuCSoN agent identifier can be any valid tuProlog *ground term*. See tuProlog documentation, [here](http://apice.unibo.it/xwiki/bin/download/Tuprolog/Download/tuprolog-guide-2.9.0.pdf).

---