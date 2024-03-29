package mini.java.fa.helper;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import mini.java.fa.AcceptableNFAState;
import mini.java.fa.NFAClosure;
import mini.java.fa.NFAState;


public final class Helper {
    
    // Comparison based on object's string representation
    public final static Comparator<Object> STR_CMP = new Comparator<Object>() {
        public int compare(Object s1, Object s2) {
            return ("" + s1).compareTo("" + s2);
        }
    };
    
    
    /**
     * (Singleton version)
     * Graph traversal algorithm implementation. Each node will be visited once.
     */
    public static <T> void visit(T node_, IFinder<T> finder_) {
        visit(Collections.singleton(node_), finder_);
    }
    
    /**
     * Graph traversal algorithm implementation. Each node will be visited once.
     */
    public static <T> void visit(Collection<T> nodes_, IFinder<T> finder_) {
        Set<T> checkedNodes = new HashSet<T>();
        Queue<T> queue = new LinkedList<T>(nodes_);
        
        while (!queue.isEmpty()) {
            T node = queue.remove();
            
            if (checkedNodes.add(node)) { // mark the node as checked
                queue.addAll(finder_.findNext(node));
            }
        }
    }
    

    
    
    /**
     * Yet another implementation for dump().
     */
    public static String dump(NFAState root_) {
        final StringBuilder sb = new StringBuilder();
        final Map<NFAState, Integer> ids = new HashMap<NFAState, Integer>();
        ids.put(root_, 0);
        
        visit(root_, new NFAStateFinder(
                new IFinderCallback<NFAState>() {
                    public boolean onNext(NFAState src_, NFAState dest_, Object input_) {
                        if (input_ == null) {
                            // for epsilons, we don't have a deterministic order
                            throw new UnsupportedOperationException("dump() for NFA is not supported");
                        }
                        if (!ids.containsKey(dest_)) {
                            ids.put(dest_, ids.size());
                        }
                        sb.append(String.format("%s =>(%s) %s%n",
                                ids.get(src_), input_, ids.get(dest_)));
                        return true;
                    }
                }));
        return sb.toString();
    }
    
    /**
     * (Singleton version) Yet another implementation for findClosure().
     */
    public static Set<NFAState> findClosure(NFAState state_) {
        return findClosure(Collections.singleton(state_));
    }
    
    /**
     * Yet another implementation for findClosure().
     */
    public static Set<NFAState> findClosure(Set<NFAState> states_) {
        final Set<NFAState> ret = new HashSet<NFAState>();
        ret.addAll(states_); // closure includes itself
        
        visit(states_, new NFAStateFinder(
                new IFinderCallback<NFAState>() {
                    public boolean onNext(NFAState src_, NFAState dest_, Object input_) {
                        if (input_ == null) {
                            ret.add(dest_);
                            return true;
                        }
                        return false; // skip normal transitions
                    }
                }));
        return ret;
    }
    
    /**
     * Collapses the epsilons in the given NFA to create a DFA.
     */
    public static NFAState collapse(NFAState root_) {

        // mapping from closures to new DFA states
        final Map<NFAClosure, NFAState> mapping = new HashMap<NFAClosure, NFAState>();
        
        
        NFAClosure init = root_.getClosure();
        NFAState dfa = init.isAcceptable()
            ? new AcceptableNFAState() : new NFAState();
        mapping.put(init, dfa);
        
        visit(init, new NFAClosureFinder(
                new IFinderCallback<NFAClosure>() {
                    public boolean onNext(NFAClosure src_, NFAClosure dest_, Object input_) {
                        assert(input_ != null);
                        
                        if (!mapping.containsKey(dest_)) {
                            NFAState state = dest_.isAcceptable()
                                ? new AcceptableNFAState() : new NFAState();
                            mapping.put(dest_, state);
                        }
                        // add the corresponding transitions in the newly created DFA
                        NFAState src = mapping.get(src_);
                        src.addTransition(mapping.get(dest_), input_);
                        return true;
                    }
                }));
        return dfa;
    }
    
//    /**
//     * Alternative implementation of NFAConvertor.
//     */
//    public static DFA collapse(NFA nfa_) {
//        if (nfa_ == null) {
//            throw new IllegalArgumentException("NFA cannot be null");
//        }
//        if (! (nfa_ instanceof V3Adapter)) {
//            throw new UnsupportedOperationException("Only V3Adapter impl is supported: " + nfa_.getClass());
//        }
//        
//        NFAState root = ((V3Adapter)nfa_).getUnderlying();
//        // NFA(v4) --> DFA(v4) --> DFA(v3)
//        return convert(collapse(root));
//    }
    
//    /**
//     * Helper method used to convert V4 back to V3
//     */
//    public static DFA convert(NFAState root_) {
//        final Map<NFAState, State> mapping = new HashMap<NFAState, State>();    
//        final DFABuilder builder = ComponentFactory.createDFABuilder();
//        
//        State init = (root_ instanceof AcceptableNFAState)
//            ? new AcceptableInitialState() : new InitialState();
//        mapping.put(root_, init);
//        
//        visit(root_, new NFAStateFinder(
//                new IFinderCallback<NFAState>() {
//                    public boolean onNext(NFAState src_, NFAState dest_, Object input_) {
//                        if (!mapping.containsKey(dest_)) {
//                            mapping.put(dest_,
//                                    (dest_ instanceof AcceptableNFAState)
//                                        ? new AcceptableState() : new State());
//                        }
//                        builder.addTransition(mapping.get(src_), mapping.get(dest_), input_);
//                        return true;
//                    }
//                }));
//        return builder.buildDFA();
//    }
    
    /**
     * Helper method used to find all states in the given NFA
     */
    public static Set<NFAState> findAll(NFAState root_) {
        final Set<NFAState> ret = new HashSet<NFAState>();
        ret.add(root_);
        
        visit(root_, new NFAStateFinder(
                new IFinderCallback<NFAState>() {
                    @Override
                    public boolean onNext(NFAState src_, NFAState dest_,
                            Object input_)
                    {
                        ret.add(dest_);
                        return true;
                    }
                }));
        return ret;
    }
    
}
