%:-initialization((agent_name(N), acquire_acc(N))).

agent_name($agentName).

agent_execution :-
  agent_name(Name),
  acquire_acc(Name),
  write("Acquired ACC for agent "), write(Name), nl,
  (agent_loop; true),
  release_acc,
  write("Released ACC for agent "), write(Name).
  
agent_loop :- !,
  agent_loop_step,
  agent_loop.
  
agent_loop_step :-
  write("Doing something..."), nl,
  thread_sleep(1000). % ms  