import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class HashListAutocomplete implements Autocompletor {
    private static int MAX_PREFIX = 10;
    private HashMap<String, ArrayList<Term>> map;
    private int mySize;

    public HashListAutocomplete(String[] terms, double[] weights) {
 	if (terms == null || weights == null) {
			throw new NullPointerException("One or more arguments null");
		}

		if (terms.length != weights.length) {
			throw new IllegalArgumentException("terms and weights are not the same length");
		}
		initialize(terms,weights);
	
	
    }

    @Override
   
     public List<Term> topMatches(String prefix, int k) {

   
       if (prefix.length() > MAX_PREFIX) {
            prefix = prefix.substring(0, MAX_PREFIX);
        }

        List<Term> all = map.get(prefix);

        if (all != null) {
    
            
           
            return all.subList(0, Math.min(k, all.size()));
        } else {
            return Collections.emptyList();
        }
    
	}

    

    @Override

public void initialize(String[] terms, double[] weights) 
	{
		map = new HashMap<String, ArrayList<Term>>();
		for (int i = 0; i < terms.length; i+= 1) 
		{
			String currentTerm = terms[i];
			for (int j = 0; j <= Math.min(MAX_PREFIX, currentTerm.length()); j += 1) 
			{
				if (currentTerm.length() >= j) {
					String currPrefix = currentTerm.substring(0, j);
					Term newTerm = new Term(terms[i], weights[i]);
					map.putIfAbsent(currPrefix, new ArrayList<Term>());
					map.get(currPrefix).add(newTerm);
				}
			}
		}
		

for (String key : map.keySet()) {
    Collections.sort(map.get(key), Collections.reverseOrder(Comparator.comparing(Term::getWeight)));
}

	}

    @Override
       public int sizeInBytes() 
	{
		if (mySize == 0) 
		{
			for (String key : map.keySet()) 
			{
				mySize += key.length() * BYTES_PER_CHAR;
				ArrayList<Term> listTerm = map.get(key);
				for (int i = 0; i < listTerm.size(); i++) 
				{
					Term currTerm = listTerm.get(i);
					mySize = mySize + BYTES_PER_DOUBLE + BYTES_PER_CHAR*currTerm.getWord().length();
				}
			}
		}
		return mySize;
	}
    }  


