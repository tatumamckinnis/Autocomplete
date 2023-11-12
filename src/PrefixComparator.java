import java.util.Comparator;

/**
 * Factor pattern for obtaining PrefixComparator objects
 * without calling new. Users simply use
 *
 *     Comparator<Term> comp = PrefixComparator.getComparator(size)
 *
 * @author owen astrachan
 * @date October 8, 2020
 */
public class    PrefixComparator implements Comparator<Term> {

    private int myPrefixSize; // size of prefix

    /**
     * private constructor, called by getComparator
     * @param prefix is prefix used in compare method
     */
    private PrefixComparator(int prefix) {
        myPrefixSize = prefix;
    }


    /**
     * Factory method to return a PrefixComparator object
     * @param prefix is the size of the prefix to compare with
     * @return PrefixComparator that uses prefix
     */
    public static PrefixComparator getComparator(int prefix) {
        return new PrefixComparator(prefix);
    }


    @Override
    /**
     * Use at most myPrefixSize characters from each of v and w
     * to return a value comparing v and w by words. Comparisons
     * should be made based on the first myPrefixSize chars in v and w.
     * @return < 0 if v < w, == 0 if v == w, and > 0 if v > w
     */
    public int compare(Term v, Term w) {
        // change this to use myPrefixSize as specified,
        // replacing line below with code
        if (myPrefixSize <= v.getWord().length() && myPrefixSize <= w.getWord().length())
        {
            return v.getWord().substring(0,myPrefixSize).compareTo(w.getWord().substring(0,myPrefixSize));
        }
        if (myPrefixSize > v.getWord().length() && myPrefixSize > w.getWord().length())
        {
            return v.getWord().compareTo(w.getWord()); 
        }
        if (myPrefixSize <= v.getWord().length() && myPrefixSize > w.getWord().length())
        {
            return v.getWord().substring(0,myPrefixSize).compareTo(w.getWord());
        }
        if (myPrefixSize > v.getWord().length() && myPrefixSize <= w.getWord().length())
        {
            return v.getWord().compareTo(w.getWord().substring(0,myPrefixSize));
        }
   

    return 0;

}
}
