CouponCodeGenerator
===================
A generator of randomized unique alpha-numeric codes of any size using an implementation of the Quadratic Residue Algorithm as suggested in a post in the [Preshing on Programming blog] (http://preshing.com/20121224/how-to-generate-a-sequence-of-unique-random-integers/).
General Description:
--------------------
The core of the system is the Quadratic Residue Algirthm permutation generator, which produced random but unique permutations of the Java BigInteger class. The values generated are randomised and unique within the range of total possible permutations, which can easily be determined by the size of the BigInteger (total permutations = 2 to the power of the number of bits). For example (and in keeping with Preshing's original post), the total possible 32 bit integers = 2^32 = approx 4.2 billion.

With the BigInteger class, much larger values are possible.

The generation of alph-numeric codes is then dealt with by manipulating the BigInteger permutations into base-36 numbers, which are represented by all the numeric characters (0-9) and all the alpha characters (a-z).

Functionality also exists to exclude certain characters, and avoid the use of bad words.

A GUI is also included that demonstrates the functionality.
