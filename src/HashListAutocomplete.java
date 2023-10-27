import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashListAutocomplete implements Autocompletor {
    private static final int MAX_PREFIX = 10;
    private Map<String, List<Term>> myMap;
    private int mySize;
        
    public HashListAutocomplete(String[] terms, double[] weights){
        if (terms == null || weights == null) {
            throw new NullPointerException("One or more arguments null");
        }
        initialize(terms,weights);
    }
    @Override
    public List<Term> topMatches(String prefix, int k) {
            if (myMap.get(prefix) == null || k == 0){
                    return new ArrayList<Term>();
            }

            List<Term> all = myMap.get(prefix);
            List<Term>  list = all.subList(0, Math.min(k, all.size()));
            //System.out.printf("%s\t%d\n%s\n", prefix,k,list);
            return list;
    }

    @Override
    public void initialize(String[] terms, double[] weights) {
        if (myMap != null) {
                myMap.clear();
        }
        else {
                myMap = new HashMap<>();
        }
        for(int k=0; k < terms.length; k++) {
                insert(terms[k],weights[k]);
        }
        Comparator<Term> rwo =
                        Comparator.comparing(Term::getWeight).reversed();
        for(String key : myMap.keySet()) {
                Collections.sort(myMap.get(key), rwo);
        }
    }
    private void insert(String string, double d ){
                
            Term t = new Term(string,d);
            mySize += BYTES_PER_CHAR * string.length() + BYTES_PER_DOUBLE;
            int size = Math.min(string.length(), MAX_PREFIX);
            for(int len=0; len <= size; len++) {
                String sub = string.substring(0, len);
                
                if (! myMap.containsKey(sub)) {
                        myMap.put(sub, new ArrayList<>());
                        mySize += BYTES_PER_CHAR * sub.length();
                }
                myMap.get(sub).add(t);
            }
    }

    @Override
    public int sizeInBytes() {
        return mySize;
    }
    
}

