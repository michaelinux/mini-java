package mini.java.fa;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;

import mini.java.fa.v3.DFA;
import mini.java.fa.v3.State;


public final class Helper {
    /**
     * Finder interface is used as callbacks for collection traversal algorithms. It defines
     * the actually structure of the collection being traversed by returning the following
     * nodes for the current node.
     */
    public interface Finder<T> {
        public Queue<T> findNext(T node_);
    }
    
    
    /**
     * Helper function used to find all nodes in a collection. Each node will be
     * included in the return set exactly once. The equivalence of the nodes
     * is based on the identity rather than the return value of equals(). The
     * initial node is provided as the parameter.
     */
    public static <T> Queue<T> findAll(T node_, Finder<T> finder_) {
        assert(node_   != null);
        assert(finder_ != null);
        
        Set<T> checkedNodes = new HashSet<T>();
        
        Queue<T> nodes = new LinkedList<T>();
        Queue<T> uncheckedNodes = new LinkedList<T>(Collections.singleton(node_));
        
        while (!uncheckedNodes.isEmpty()) {
            T node = uncheckedNodes.remove();
            checkedNodes.add(node); // mark the node as checked
            nodes.add(node);
            
            for (T next : finder_.findNext(node)) {
                if (!checkedNodes.contains(next)) {
                    uncheckedNodes.add(next);
                }
            }
        }
        
        return nodes;
    }
    
    
    /**
     * Helper function used to find the closure for the given NFAState.
     */
    public static Set<NFAState> findClosure(NFAState state_) {
        assert (state_ != null);
        return new HashSet<NFAState>(Helper.findAll(state_,
                new Finder<NFAState>() {
                    public Queue<NFAState> findNext(NFAState state_) {
                        return new LinkedList<NFAState>(state_.getStates());
                    }
                }));
    }
    
    /**
     * Helper function used to find all reachable states from the given
     * NFAState. States are returned in the order they are visited.
     */
    public static Queue<NFAState> findStates(NFAState state_) {
        return Helper.findAll(state_, new Finder<NFAState>() {
            @Override
            public Queue<NFAState> findNext(NFAState node_) {
                Queue<NFAState> nodes = new LinkedList<NFAState>();
                // first the epsilons
//                Set<NFAState> epsilons = new TreeSet<NFAState>(
//                        new Comparator<NFAState>() {
//                            public int compare(NFAState o1, NFAState o2) {
//                                return o1.toString().compareTo(o2.toString());
//                            }
//                        });
//                epsilons.addAll(node_.getStates());
//                nodes.addAll(epsilons);
                nodes.addAll(node_.getStates());
                
                // then we visit the transitions according to the order of
                // their string representations
//                Set<Object> inputs = new TreeSet<Object>(
//                        new Comparator<Object>() {
//                            public int compare(Object o1, Object o2) {
//                                return o1.toString().compareTo(o2.toString());
//                            }
//                        });
//                inputs.addAll(node_.getInputs());
//                for (Object input : inputs) {
                for (Object input : node_.getInputs()) {
                    nodes.add(node_.getState(input));
                }
                
                return nodes;
            }
        });
    }
    
    /**
     * Helper method used to generate the string representation of the whole NFA
     */
    public static String dumpString(NFAState state_) {
        StringBuilder sb = new StringBuilder();
        Queue<NFAState> states = findStates(state_);
        Map<NFAState, Integer> ids = new HashMap<NFAState, Integer>();
        // first we assign an id to each of the states
        for (NFAState state : states) {
            ids.put(state, ids.size());
        }
        // then we generates the string reps for all the transitions
        for (NFAState state : states) {
            Integer id = ids.get(state);
            // epsilons first
            for (NFAState target : state.getStates()) {
                sb.append(String.format("%s => %s%n",
                        id, ids.get(target)));
            }
            for (Object input : state.getInputs()) {
                sb.append(String.format("%s =>(%s) %s%n",
                        id, input, ids.get(state.getState(input))));
            }
        }
        // TODO - should we remove the tailing %n?
        return sb.toString();
    }
    
    
    /**
     * dumpString() for V3 interface
     */
    public static String dumpString(DFA dfa_) {
        final DFA dfa = dfa_;
        final StringBuilder sb = new StringBuilder();
        final Map<State, Integer> ids = new HashMap<State, Integer>();
        
        State init = dfa_.getInitialState();
        if (init == null) {
            throw new IllegalArgumentException("Invalid DFA: initial state is null!");
        }
        ids.put(init, 0);
        
        findAll(init, new Finder<State>() {
            public Queue<State> findNext(State node_) {
                if (node_ == null) {
                    throw new IllegalArgumentException("State cannot be null!");
                }
                Queue<State> ret = new LinkedList<State>();
                
                Set<Object> inputs = new TreeSet<Object>(new Comparator<Object>() {
                    public int compare(Object o1, Object o2) {
                        return ("" + o1).compareTo("" + o2); //null
                    }
                });
                inputs.addAll(dfa.getInputs(node_));
                
                for (Object input : inputs) {
                    State target = dfa.getState(node_, input);
                    if (target == null) {
                        throw new IllegalArgumentException("Unable to find the corresponding target state for ("
                                + node_ + ", " + input + ")");
                    }
                    if (!ids.containsKey(target)) {
                        ids.put(target, ids.size());
                    }
                    ret.add(target);
                    // XXX - is this method guaranteed to be called the same order
                    // as the states in the final queue?
                    sb.append(String.format("%s =>(%s) %s%n",
                            ids.get(node_), input, ids.get(target)));
                }
                return ret;
            }
        });
        return sb.toString();
    }
    
    /**
     * Alternative implementation of DFAComparator.
     */
    public static boolean compare(DFA A_, DFA B_) {
        return ((A_ == null) && (B_ == null)
                || dumpString(A_).equals(dumpString(B_)));
    }
}
