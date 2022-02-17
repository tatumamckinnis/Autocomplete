# P5: Autocomplete, Fall 2021
- [Project Introduction](#project-introduction)
    - [Background](#background)
    - [Acknowledgements](#acknowledgments)
- [Overview: What to Do](#overview-what-to-do)
- [Overview: How to Do It](#overview-how-to-do-it)
    - [Git](#git)
    - [Running AutocompleteMain](#running-autocompletemain)
    - [PrefixComparator](#prefixcomparator)
    - [Implementing BinarySearchLibary](#implementing-binarysearchlibrary)
    - [Implementing BinarySearchAutocomplete](#implementing-binarysearchautocomplete)
    - [HashListAutocomplete](#hashlistautocomplete)
    - [Extra Challenge: SlowBruteAutocomplete](#extra-challenge-slowbruteautocomplete)
- [Analysis](#analysis)
- [Reflect](#reflect)
- [Grading](#grading)

## Project Introduction	

### Background
<details>
<summmary>Background on Autocomplete</summary>

<br>

As with most backgrounds, you don't need to read this to do the assignment, but it does supply a very useful background. Autocomplete is an algorithm used in many modern software applications. In all of these applications, the user types text and the application suggests possible completions for that text as shown below -- on the left in March 2019 and on the right on  October 9, 2020.

<div align="center">
  <img width="384" height="344 "src="p5-figures/googleSearch.png">
  <img width="384" height="345" src="p5-figures/googleSearch2.png">
</div>

Although finding terms that contain a query by searching through all possible results is possible, these applications need some way to select only the most useful terms to display (since users will likely not comb through thousands of terms, nor will obscure terms like "duke cookiemonster" be useful to most users). Thus, autocomplete algorithms not only need a way to find terms that start with or contain the prefix, but a way of determining how likely each one is to be useful to the user and displaying "good" terms first.

According to one study, in order to be useful the algorithm must do all this in less than 100 milliseconds (see article linked below). If it takes any longer, the user will already be inputting the next keystroke (while humans do not on average input one keystroke every 50 milliseconds, additional time is required for server communication, input delay, and other processes). Furthermore, the server must be able to run this computation for every keystroke, for every user. In this assignment, you will be implementing autocomplete using three different algorithms and data structures. Your autocomplete will be different than the industrial examples described above in two ways:

1. Each term will have a predetermined, constant weight/likelihood, whereas actual autocomplete algorithms might change a term's likelihood based on previous searches.
2. We will only consider terms which start with the user query, whereas actual autocomplete algorithms (such as the web browser example above) might consider terms which contain but do not start with the query.

The article linked below describes one group's recent analysis of different data structures to implement autocomplete efficiently. You'll be implementing a version of what they call a prefix hash tree, though we'll use a prefix hash list which is more efficient when terms aren't updated dynamically.
- https://medium.com/@prefixyteam/how-we-built-prefixy-a-scalable-prefix-search-service-for-powering-autocomplete-c20f98e2eff1

### Acknowledgements
The assignment was developed by Kevin Wayne and Matthew Drabick at Princeton University for their Computer Science 226 class. Former head CompSci 201 UTAs, Arun Ganesh (Trinity '17) and Austin Lu (Trinity '15) adapted the assignment for Duke with help from Jeff Forbes. Josh Hug updated the assignment and provided more of the testing framework. The current version is the result of simplification done in Fall 2018 and then modified again in Spring 2019 based on the article above and experience from previous semesters. The version in Fall 2020 reflects updates to the java.util.Comparator API, now updated for academic year 2021-2022.

</details>

## Overview: What to Do
<details>
<summary>High-level what to Do</summary>

<br>

Here's a high-level view of the assignment. This is enough information to know what to do, but not necessarily how to do it. For details, you can refer to sections later in this write-up. You can, could, perhaps even should? also work to figure some things out on your own, referring to the later sections to clear up misunderstandings, for example.

You'll be creating two classes that implement the `Autocompletor` interface you'll get when you start the project. You're given one class, `BruteAutocomplete`, that uses brute-force to find those entries that match a query; this class implements the `Autocomplete` interface. You'll compare performance with the two classes you create, `BinarySearchAutocomplete` (partially written) and `HashListAutocomplete`(not provided) and answer questions about them and their performance.

You'll test your new classes with JUnit tests and *also by* running the `AutocompleteMain` class and comparing the results to known output.  The `AutocompleteMain` class launches a GUI (Graphical User Interface) supporting queries. This will run as soon as you clone/import/create your project.  The general steps you'll do to complete the project are

1. Run `AutocompleteMain` to be sure it works as shown in this write-up. 
2. Implement the `PrefixComparator.compare` method that is used in the `BinarySearchAutocomplete` class.
3. Implement `BinarySearchAutocomplete` that extends `Autocompletor`. This requires _first implementing_ two methods in `BinarySearchLibrary`: `firstIndex` and `lastIndex`, then one method in `BinarySearchAutocomplete` -- you're given some code in that class.
    1. There are two Test programs, one for each of these files: `TestBinarySearchLibrary` and `TestBinarySearchAutocomplete`. Run them both to help with development and debugging.
    2. Verify results by running the `AutocompleteMain` program with this new class.
4. Implement `HashListAutocomplete` that implements interface `Autocompletor`.
    1. Test by verifying output compared to other implementations by running the main program with this class.
5. ***Optional***: implement `SlowBruteAutocomplete` that extends `BruteAutocomplete`.

***After implementing and testing these classes you'll complete benchmarks and analyses as explained below.***

### Git
<details>
<summary>Git Details</summary>

<br>

Fork, clone, and import the cloned project from the file system. Use this URL from the course GitLab site: https://coursework.cs.duke.edu/201fall21/P5-Autocomplete. ***Be sure to fork first*** (see screen shot). Then, clone using the SSH URL after using a terminal window to `cd` into your IntelliJ workspace. 

<div align="center">
  <img src="p5-figures/gitFork.png">
</div>

When you make a series of changes you want to 'save', you'll push those changes to your GitHub repository. You should do this after major changes, certainly every hour or so of coding. You'll need to use the standard Git sequence to commit and push to GitHub:

```bash
git add .
git commit -m 'a short description of your commit here'
git push
```

</details>

</details>

## Overview: How to Do It
<details>

<summary>A faster version of Autocomplete</summary>

<br>

You'll run the program `AutocompleteMain`, and note its output (see below) for testing other versions
of `Autocomplete`. Then you'll implement the class `PrefixComparator` which you'll use to implement
an efficient version of _binary saerch_ you'll use as the basis of `BinarySearchAutocomplete`.

### Running AutocompleteMain
<details>
<summary>Running the main program</summary>

<br>

When you fork and clone the project you'll be able to run the main/driver program AutocompleteMain. 
If you run `AutocompleteMain` and select the file `words-333333.txt` from the data folder you should see the output below shown in the GUI window for the search query **auto**.  You'll use this same search term, `auto` to test the other implementations you develop.

<div align="center">
  <img src="p5-figures/astrachanSearch.png">
</div>

</details>

### PrefixComparator
<details>
<summary>Comparing by prefix</summary>

<br>

You'll need to implement the `compare` method in the class `PrefixComparator` so that it conforms to specifications explained here.

A `PrefixComparator` object is obtained by calling `PrefixComparator.getComparator` with an integer argument `r`, the size of the prefix for comparison purposes. The value is stored in the instance variable `myPrefixSize` as you'll see in the code. This class is used in `BinarySearchAutocomplete`, but not in `BruteAutocomplete`.

You must use only the first `myPrefixSize` characters of the words stored in `Term` objects `v` and `w` that are passed to `PrefixComparator.compare`. However, if the length of either word is less than `myPrefixSize`, this comparator only compares up ***until the end of the shorter word.*** This means that although `"beeswax"` is greater than `"beekeeper"` when compared lexicographically, i.e., with the natural order for strings, the two words are considered equal using a `PrefixComparator.getComparator(3)` since only the first three characters are compared.

For a `PrefixComparator.getComparator(4)`, `"beeswax"` is greater than `"beekeeper"` since `"bees"` is greater than `"beek"`. But `"bee"` is less than `"beekeeper"` and `"beeswax"` since only the first three characters are compared --- since `"bee"` has only three characters and these three characters are the same. ***The length*** of `"bee"` ***makes it less than*** `"beekeeper"`, just as it is when eight characters are used to compare these words.

***Your code should examine only as many characters as needed to return a value.*** You should examine this minimal number of characters using a loop and calling `.charAt` to examine characters--- you'll need to write your loop and comparisons carefully to ensure that the prefix size is used correctly. See the table below for some examples. Recall that you can subtract characters, so `'a'` - `'b'` is a negative value and `'z'` - `'a'` is a positive value. You can also use `<` and `>` with `char` values.

Here is a reference table for the `PrefixComparator` comparator. 

|r/prefix|v| |w| Note |
|    -   |-|-|-| -    |
|   4  |bee|<|beekeeper|"bee" < "beek"|
|4|bees|>|beek|‘s’ > ‘k’|
|4|bug|>|beeswax|‘u’ > ‘e’|
|4|bees|=|beeswax|"bees" == "bees"|
|3|beekeeper|=|beeswax|"bee" == "bee"|
|3|bee|=|beeswax|"bee" == "bee"|



You can test your code with the `TestTerm` JUnit class which has several tests for the `PrefixComparator` class.

</details>

</details>

## Implementing BinarySearchLibrary
<details>

<summary>A Better Binary Search</summary>

<br>

The class `BinarySearchLibrary` stores methods used in the implementation of the `BinarySearchAutocomplete` class. Once you've implemented methods `firstIndex` and `lastIndex` in `BinarySearchLibrary`, you'll also need to implement the `BinarySearchAutocomplete.topMatches` method that will call these methods.

You're given code in `BinarySearchLibrary.firstIndexSlow` that is correct, ***but does not meet performance requirements***. This slow implementation will be very slow in some situations, e.g., when a list has many equal values, as could be the case when many words share a  prefix and the comparator is a PrefixComparator comparator. The code in the slow method  is **O(*N*)** where there are *N* equal values since the code could examine all the values. To meet performance criteria your code must not be simply **O(log *N*)**, but should make at most $`1 + log_2N`$ comparisons -- that's one more than the ceiling of $`log2(N).`$ To do this we strongly, strongly, strongly suggest using the loop invariant explained below.


### Implementing method firstIndex (and lastIndex)

<br>

In many implementations of binary search, variables `low` and `high` are used to bracket/delimit the range of possible values for the search target. You can see this example in the code at the end of this section that comes from `Collections.binarySearch`. ***That method does not return the first index matching a target, but _some index_ that matches.***

### Invariant for `firstIndex`

Instead of using the initialization of low and high shown in the code below, ***you should initialize these values to establish the following loop invariant*** -- an expression that will be true every time the loop test is evaluated, that is both before the loop executes the first time and at the end of every loop iteration.

**(low, high] is interval containing  target, if target is in the list. **

Notice that this is an open interval on the left, in particular this means that **`list.get(low)` cannot be equal to target.** To establish this invariant before the loop executes the first time you should consider that ***the open interval `(-1,list.length()-1]` which must contain target if it is present since this interval represents every possible index in `list`.*** Since the interval is open on the left, you cannot initialize low to zero since `(0,99]` includes 99, but not zero! So for a 100-element array you'd set `low=-1` and `high=99`, so the interval `(-1,99]` ensures that if target is present, it's either `list.get(0)` or `list.get(1)` or … `list.get(99)`.

After calculating the midpoint (see reference code below), you'll need to ***re-establish the invariant by comparing the target to the middle value.*** In particular, in the while loop, if you determine (conceptually)

`list[mid] < target`

Then you can set low to mid since you know that before the loop `(low,high]` was the interval and if `list[mid] < target` then `(mid,high]` still maintains the invariant.

Otherwise, meaning `list[mid] >= target`, then you can set `high` to `mid` since if before the loop `(low,high]` was the interval and `list[mid] >= target`, then `(low,mid]` still maintains the invariant.

### Loop Termination

If `low` and `high` differ by 1 in the interval `(low,high]`, and the list is sorted, then `list.get(high)` is target and `high` is the lowest/first index (if the interval isn't empty) since the interval contains a single value. This means you should use a loop guard/test that loops 

`while (low+1 != high)`

since you know that `low <= high` is always true. Thus, when the loop exits, you'll know that `low == high-1` and the interval `(low,high]` is the same as `(high-1,high]` -- which contains a single value -- the index whose value is `high`. 

You can determine whether to return -1 (target not present) based on the value of `high` and the value of `list.get(high)`. For example, if `high` is not a valid index, the interval is empty. ***After the loop you'll need to make one more comparison*** of `a.get(high)` with `target` to see if they are equal. ***This loop will be correct and will meet the performance bounds if you develop it using the invariant.***

### Summary of firstIndex as Explained
```java

int low = -1;
int high = list.size()-1;
// (low,high] contains target
		
while (low + 1 != high) {
	int mid = (low+high)/2;
			
	// use comp.compare here to adjust low or high
}
// check that high is an index in list, if not? return -1
// check list.get(high) to see if it's target, use comp
```

### Code for `lastIndex`

You should develop a similar invariant and loop for the method `lastIndex` that you'll implement. In this case, the interval you'll consider is `[low,high)`, i.e., open on the right. Establish the invariant before the loop, and reason about how to assign to `low` or `high` depending on how the middle value compares to key. You'll initialize `low = 0` before the loop since the interval is open on the left.


### Example and Incorrect Binary Search Code 

<br>

The code below returns ***some value*** equal to `target`, but ***not necessarily the first value.*** This is code from `Collections.binarySearch` rewritten to be close to the code you'll write. 

***Do not use this code*** except to see how to write code using a `Comparator`. This code is the basis for `firstIndexSlow` since it is the code from the `java.util.Collections` class that finds a match, but not necessarily the first match.

```java
public static <T> int binarySearch(List<T> list, T target,
                                   Comparator<T> comp) {
    int low = 0;
    int high = list.size()-1;
    while (low <= high) {
        int mid = (low + high)/2;
        T midval = list.get(mid);
        int cmp = comp.compare(midval,target);

        if (cmp < 0)
            low = mid + 1;
        else if (cmp > 0)
            high = mid - 1;
        else
            return mid; // target found
     }
     return -(low + 1);  // target not found
}
```


### Testing BinarySearchLibrary Methods

<br>

You're given two classes to help verify that your methods are correct and meet performance requirements. The JUnit tests in `TestBinarySearchLibrary` can help you verify that your methods return the correct values. The output of running `BinaryBenchmark` can help verify both ***correctness and performance requirements.*** The output using a correct `BinarySearchLibrary` class is shown below when running `BinaryBenchmark`. The values in both `index` columns should be the same: the location of the first occurrence of the prefix shown. The `cslow` column is the number of comparisons made by the slow implementation `firstIndexSlow`. The `cfast` column is the number of comparisons made by `firstIndex`. Note that $`log2(26000)`$ is 14.666, and that 1+15 = 16, so the performance criterion is met.
```
size of list = 26000
Prefix index    index	  cslow   cfast

aaa	     0	      0	   817	15
fff	  5000	   5000	   693	16
kkk	 10000    10000	   568	16
ppp	 15000    15000	   443	16
uuu	 20000    20000	   318	15
zzz	 25000    25000	   194	16
```
</details>

## Implementing BinarySearchAutocomplete
<details>
<summary>Implementing an Interface</summary>

Once you've implemented the methods in class `BinarySearchLibrary`, you'll still need to implement code for `topMatches` in the `BinarySearchAutocomplete` class -- a method required as specified in the `Autocompletor` interface. The other methods in `BinarySearchAutocomplete` are written, though two rely on the code you implemented in `BinarySearchLibrary`.

### Code Already Written

Code in static methods `firstIndexOf` and `lastIndexOf` is written to use the API exported by `BinarySearchLibrary`. You'll see that the `Term[]` parameter to these methods is transformed to a `List<Term>` since that's the type of parameter that must be passed to `BinarySearchLibrary` methods. 

You'll also see a `Term` object created from the `String` passed to `topMatches`. The weight for the `Term` doesn't matter since only the `String` field of the `Term` is used. You'll then implement `topMatches` as described below.


### Implementing topMatches Efficiently

<br>

The `topMatches` method requires that you return the weightiest `k` matches that match `prefix` that's a parameter to `topMatches` --- note that `k` is a parameter to the method as well. If there are `M` terms that match the prefix, then the simple method of finding the `M` matches, copying them to a list, sorting them in reverse weight order, and then choosing the first `k` of them will run in the total of the times given below. Using this approach will thus have complexity/performance of `O(log N + M log M)`. An approach after the table shows how to use a size-limited priority queue to achieve a bound of `O(log N + M log k)` to find `k` matches. Here the `log(N)` term comes from doing binary search on `N` terms.

|Complexity|Reason|
| ---      |  ---  |
|O(log N)|Call firstIndex and lastIndex|
|O(M log M)|Sort all M elements that match prefix|
|O(k)|Return list of top k matches|

It's possible of course that `k < M` and often `k` will be much less than `M`. Rather than sorting all `M` entries that match the prefix, you can use a size-limited priority queue using the same idea that's used in the `topMatches` method from `BruteAutocomplete`. Reference the code there for ideas. ***You must implement this approach in the code you write.***
If you implement this priority queue approach, you'll make topMatches run in `O(log N + M log k)` time instead of `O(log N + M log M)` using the ideas from the table above. In benchmarking, there is no noticeable difference for the data files you're given for small values of `M`, though with larger values of `M` there will be a difference. In particular, when the prefix is the empty string, which matches every `Term`, there will be a difference.

|Complexity|Reason|
| ---      |  ---  |
|O(log N)|Call firstIndex and lastIndex|
|O(M log k)|Keep best k elements in priority queue|
|O(k)|Return list of top k matches|


You'll see code written that calls `firstIndex` and `lastIndex` which provides a range for all possible prefix matches. If there are no matches,  an empty list of `Term` objects is returned. Then there is a section of code that *you must write.*

**If there are matches, model the code you write based on the `PriorityQueue` code from `BruteAutocomplete.topMatches`, but only insert the `Term` objects between `firstIndex` and `lastIndex` instead of all `Terms`. You'll then take the best `k` matches (if there are that many).**


### Testing BinarySearchAutocomplete

<br>

You're given a JUnit test class `TestBinarySearchAutocomplete` that you should run to verify your methods work correctly. You should also change the code in `AutocompleteMain` to use the `BinarySearchAutocomplete` class -- see the commented out lines as shown below. 
Then be sure that the output using the target auto matches the output shown at the beginning of this write-up.

```java
final static String AUTOCOMPLETOR_CLASS_NAME = BRUTE_AUTOCOMPLETE;
//final static String AUTOCOMPLETOR_CLASS_NAME = BINARY_SEARCH_AUTOCOMPLETE;
//final static String AUTOCOMPLETOR_CLASS_NAME = HASHLIST_AUTOCOMPLETE;
```

</details>



## HashListAutocomplete
<details>

<summary>Implement and Test HashListAutocomplete</summary>

<br>

Create a class named `HashListAutocomplete` that implements the `Autocompletor` interface. You'll do this be creating a new class, and ensuring that it `implements Autocomplete` -- then allowing IntelliJ to fill in the methods for the interface.  This class will provide an `O(1)` implementation of `topMatches` --- with a tradeoff of requiring more memory. The method outlined here is a hybrid of the approach outlined in the article referenced at the beginning of this write-up.

The class maintains a `HashMap` of _every possible prefix_ (for each term) (up to the number of characters specified by a constant `MAX_PREFIX` that you should set to 10 as shown. 

```java
private static final int MAX_PREFIX = 10;
private Map<String, List<Term>> myMap;
private int mySize;
```

The key in the map is a prefix/substring. The value for each prefix key is a weight-sorted list of `Term` objects that share that prefix. The diagram below shows part of such a `HashMap`. Three prefixes are shown---the corresponding values are shown as a weight-sorted list of `Term` objects.

|Prefix|Term Objects|
| --   |    ----    |
|"ch"| ("chat",50), ("chomp",40), ("champ",30), ("chocolate",10)|
|"cho"|("chomp",40), ("chocolate",10)|
|"cha | ("chat",50), ("champ", 30)|

You should create a constructor similar to those in the other implementations. The constructor body is one line: a call to `initialize()` -- though you'll need to throw exceptions just as the other implementations throw.

***For each `Term` in `initialize`, use the first `MAX_PREFIX` substrings as a key in the map the class maintains and uses.*** For each prefix you'll store the `Term` objects with that prefix in an `ArrayList` that is the corresponding value for the prefix in the map.

***After all keys and values*** have been entered into the map, you'll write code to sort every value in the map, that is each `ArrayList` corresponding to each prefix. You must use a `Comparator.comparing(Term::getWeight).reversed()` object to sort so that the list is maintained sorted from high to low by weight, e.g., see below, and sort using this idea for each list associated with a key in the map.

`Collections.sort(list,Comparator.comparing(Term::getWeight).reversed())`

The implementation of `topMatches` can then be done in about five lines of code or fewer: if the prefix is in the map, get the corresponding value and return a sublist of the first `k` entries. Here's code that can help:

```java
List<Term> all = myMap.get(prefix);
List<Term> list = all.subList(0, Math.min(k, all.size()));
```

***All prefixes passed to `topMatches` should be shortened to `MAX_PREFIX` characters if necessary.***

You'll also need to implement the required `sizeInBytes` method. This should account for every `Term` object and every String/key in the map. Use the implementations of `sizeInBytes` in the other `Autocomplete` classes as a model. Each string stored contributes `BYTES_PER_CHAR*length` to the bytes need. Each double stored contributes `BYTES_PER_DOUBLE`. You'll account for every `Term` and for every key in the map -- these are all strings.

</details>

## Extra Challenge: SlowBruteAutocomplete
<details>

<summary>Extra Challenge is not required</summary>

<br>

The code in `BruteAutoComplete` uses a priority queue in `topMatches`. It's also possible to sort all the elements to find the top `M` matches from `N`. The code below is a partially complete version of `topMatches` that uses this idea.

```java
public List<Term> slowTopM(String prefix, int k) {
    List<Term> list = new ArrayList<>();
    for (Term t : myTerms) {
        if (t.getWord().startsWith(prefix)) {
            list.add(t);
        }
    }
    Collections.sort(list),Comparator.comparing(Term::getWeight).reversed());
    // continue with implementation here
```

For extra credit, implement a class `SlowBruteAutocomplete` that extends `BruteAutocomplete` and that overrides the method `topMatches` to use code based on what's shown above. IntelliJ will indicate you need to implement a specific constructor and call `super(terms,weights)`, the constructor in the base/parent class. ***You'll also need to change*** the instance variables in `BruteAutocomplete` from `private` to `protected` so they can be accessed in the new class. Test this class and add it to the `BenchMarkForAutocomplete` class so you can get timing data on how it works. Include comments on the efficiency of this class in comments you add to the beginning of the class in `SlowBruteAutocomplete.java. `

</details>

## Analysis
<details>

<summary>Analysis and Reflect</summary>

<br>

You'll submit the analysis as a PDF separate from the code in Gradescope. 

1. Run the program `BenchmarkForAutocomplete` and copy/paste the results into the file you submit. You'll need to run three times, once for each of the files in the Benchmark program: `threeletterwords.txt`, `fourletterwords.txt`, and `alexa.txt`. On ola's computer, the first few lines are what's shown below for `data/threeletterwords.txt`. The unlabeled "search" is for an empty string "" which matches every string stored. These numbers are for a file of every three letter word "aaa, "aab", … "zzy", "zzz", not actual words, but 3-character strings.

```
init time: 0.006699	for BruteAutocomplete
init time: 0.004799	for BinarySearchAutocomplete
init time: 0.07067	for HashListAutocomplete

search  size  #match     BruteAutoc  BinarySear HashListAu
		17576   50      0.00238732	0.00219437	0.00019249
		17576   50      0.00056931	0.00136807	0.00000449
a		676     50      0.00044899	0.00015267	0.00000443
a		676     50      0.00042797	0.00013736	0.00000575
b		676     50      0.00051954	0.00015502	0.00000640
...
```

2. Run the program again for `alexa.txt` with  `matches = 10000`, paste the results, and then explain to what extent the number of matches affects the runtime. The number of matches, `matchSize`, is specified in the method `runAM` (for run all matches)
3. Explain why the last for loop in `BruteAutocomplete.topMatches` uses a `LinkedList` (and not an `ArrayList`) AND why the `PriorityQueue` uses `Comparator.comparing(Term::getWeight)` to get the top `k` heaviest matches.
4. Explain why `HashListAutocomplete` uses more memory than the other `Autocomplete` implementations. Be brief.
5. Read this article from Wired: _Genome Hackers Show No One’s DNA Is Anonymous
Anymore_ [https://courses.cs.duke.edu/compsci201/current/netid/genome-hackers-wired.pdf] and comment on some aspect of the article relating to privacy and DNA.

After completing the analysis questions you should push to Git and submit the entire project on Gradescope. ***Submit analysis and program separately.***


## Reflect

Complete the reflect: https://do-compsci.com/201fall21-p5-reflect

</details>

## Grading
<details>

<summary>Expand for Grading</summary>

<br>

| Points | Grading Criteria |
| ------ | ------ |
| 4 | Code Comparator|
| 8 |  Code for BinarySearchLibrary firstIndex and lastIndex |
| 6 | Code for BinarySearchAutocomplete.topMatches |
| 9 | Code for HashListAutocomplete |
| 1 | API |
| 8 | Analysis code and questions answered. UTAs will grade and comment on this |


We will map total points you earn to scores as follows. We will record the letter grade as your grade for this assignment. For example, a score in the range of 32-26 will range from A- to A+.

32-36:  A<br>
26-31:  B<br>
20-25:  C<br>
14-19:  D


There is no extra credit for `SlowBruteAutocomplete`, it is a challenge for "fun". You must be sure to add comments at the top of the source `SlowBruteAutocomplete.java` file with information on timing you get from the benchmark class. The benchmark class you submit should include this new class as well.

</details>




