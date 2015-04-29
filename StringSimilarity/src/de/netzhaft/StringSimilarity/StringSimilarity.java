package de.netzhaft.StringSimilarity;

public class StringSimilarity {
	private static final int maxCharVal = 65536;
	/**
	 * Count the character-value's apperances.
	 * @param c An array of int's with a size of exactly 255. If the array is null or c.length != 255 a new array will be allocated.
	 * @param a The String to be analysed as characters.
	 * @param leftBound a[leftbound] to a[rightbound - 1] will be the boundaries of the analysis.
	 * @param rightBound a[leftbound] to a[rightbound - 1] will be the boundaries of the analysis.
	 * @return An array of int's which holds a count for every character-value.
	 */
	private static int[] count(int[] c, final char[] a, final int leftBound, final int rightBound) {
		if(c==null || c.length != maxCharVal) c = new int[maxCharVal]; 
		for(int i=leftBound;i<rightBound;++c[a[i++]]);
		return c;
	}
	/**
	 * Compare two Strings for similarity. 
	 * @param a first String
	 * @param b second String which will be compared against a.
	 * @param cap {@link #compareCharacterDifference(char[], int, int, char[], int, int)} will be called first to avoid unnecessary comparision if compareCharacterDifference returns a value smaller or equal than cap, the String will not be analysed any further.
	 * @return Percentage of similarity ranging from 0-100 where 100 is equality and 0 means they have absolutely nothing in common.
	 */
	public static int compare(String a, String b, int cap) {
		char [] as = new char[a.length()];
		char [] bs = new char[b.length()];
		a.getChars(0, a.length(), as, 0);
		b.getChars(0, b.length(), bs, 0);
		return compareCharacters(as,bs, cap);
	}
	/**
	 * Executes the changes in a and b necessary to do {@link #incremetalCharacterDifference(int, int[], char, int[], char)} for more
	 * than one iteration.
	 * @param a the array of ints computed by {@link #count(int[], char[], int, int)}.
	 * @param asub the character which is subtracted from the String which resulted in a if no character shall be subtracted asub must be 0.
	 * @param b see a.
	 * @param bsub see asub.
	 */
	private static void incrementalCharacterExecute(int[] a, char asub, int[] b, char bsub) {
		if(asub != 0) a[asub]--;
		if(bsub != 0) b[bsub]--;
	}
	/**
	 * Lookahead method to compute the difference of two Strings if one of the Strings or both is changed so that the first character is subtracted.
	 * @param diff the difference of the two Strings as computed by {@link #count(int[], char[], int, int)} for the first String.
	 * @param a the array of ints as computed by {@link #count(int[], char[], int, int)} for the first String.
	 * @param asub the character which is subtracted from the String which resulted in a if no character shall be subtracted asub must be 0.
	 * @param b see a.
	 * @param bsub see asub.
	 * @return the difference of the two Strings after the change.
	 */
	private static int incremetalCharacterDifference(int diff, int[] a, char asub, int[] b, char bsub) {
		if(asub != 0) diff += (Math.abs((a[asub]-1) - b[asub])) < Math.abs((a[asub]+1 - b[asub])) ? -1 : 1;
		if(bsub != 0) diff += (Math.abs(a[bsub] - (b[bsub]-1))) < Math.abs((a[bsub] - b[bsub])) ? -1 : 1;
		return diff;
	}
	/**
	 * Compares two Strings on the basis of the characters included, the order of the characters is not taken into account. This method is running in O(255). 
	 * @param a String a which is compared to b.
	 * @param aLeftBound a will be compared from a[aLeftBound] to a[aRightBound].
	 * @param aRightBound see aLeftBound.
	 * @param b see a.
	 * @param bLeftBound see aLeftBound.
	 * @param bRightBound see bRightBound.
	 * @return the number of differences found, which is SUM(i=0,i<255, |a[i] - b[i]|)
	 */
	public static int compareCharacterDifference(	char [] a, final int aLeftBound, final int aRightBound, 
			char [] b, final int bLeftBound, final int bRightBound) {
		int [] ac = new int[maxCharVal], bc = new int[maxCharVal]; 
		return compareCharacterDifference(ac, bc, a, aLeftBound, aRightBound, b, bLeftBound, bRightBound);
	}
	/**
	 * @see {@link #compareCharacterDifference(char[], int, int, char[], int, int)}.
	 * The only difference to {@link #compareCharacterDifference(char[], int, int, char[], int, int)} is that the arrays ac and bc which will hold the
	 * count data are parameters, which can be analysed further. 
	 */
	private static int compareCharacterDifference(	int[] ac, int[] bc, 
			char [] a, final int aLeftBound, final int aRightBound, 
			char [] b, final int bLeftBound, final int bRightBound) {
		int count = 0;
		ac = count(ac, a, aLeftBound, aRightBound); bc = count(bc, b,bLeftBound,bRightBound);
		for(int i=1;i<ac.length && i < bc.length; count += Math.abs(ac[i] - bc[i++])) ;
		return count;
	}
	/**
	 * Calculated MAX( 1 - ( count / ( (lengthA + lengthB) / 2 ) ) ) * 100, 
	 * which is the percentage of the differences as compared to the arithmetic 
	 * mean of the length of the Strings involved in the comparison.
	 */
	private static int calculatePercentage(final int count, final int lengthA, final int lengthB) {
		return Math.max((int)((1 - ((double)count / ((double)(lengthA + lengthB) / 2f) ) ) * 100d),0);
	}
	/**
	 * @see {@link #compare(String, String, int)}
	 * The only difference to {@link #compare(String, String, int)} is that char arrays are taken as paramters.
	 * <b>As for how it compares these two char-arrays:</b> 
	 * <ul>
	 * 	<li><i>if</i> cap < calculatePercentage({@link #compareCharacterDifference(char[], int, int, char[], int, int)},...) proceed <i>else</i> return 0</li>
	 *  <li>calculate the minimum ignored characters needed to iterate over the arrays. 
	 *  	<ul>
	 *  		<li> if a[i] == b[i] proceed.</li>
	 *  		<li> else:  count++ & calculate the best skip method (options are skip over 1 character in a OR 1 character in b OR 1 character in a and b) to avoid as many future skips as possible.</li>
	 *  	</ul>
	 *  </li> 
	 * </ul>
	 */
	public static int compareCharacters(char [] a, char [] b, int cap) {
		char[] as = new char[Math.max(a.length, b.length)], bs = new char[Math.max(a.length, b.length)];
		int[] ac = new int[maxCharVal], bc = new int[maxCharVal];
		//for(int i=0;i<as.length; i++) { as[i]=0; bs[i]=0; } // init arrays with 0's 
		System.arraycopy(a, 0, as, 0, a.length);
		System.arraycopy(b, 0, bs, 0, b.length);
		int diff = compareCharacterDifference(ac, bc, as, 0, as.length, bs, 0, bs.length);
		if(diff == 0)
			return calculatePercentage(diff, a.length, b.length);
		if((calculatePercentage(diff, a.length, b.length)) > cap) {
			int count=0,ia=0,ib=0;
			for(int i=0;i<as.length;i++)  {
				if(as[ia] == bs[ib]) {
					ia++; ib++;
				} else {
					count++;
					int incIa = incremetalCharacterDifference(diff, ac, as[ia], bc, (char)0);
					int incIb = incremetalCharacterDifference(diff, ac, (char)0, bc, bs[ib]);
					int incboth = incremetalCharacterDifference(diff, ac, as[ia], bc, bs[ib]);
					switch (minimum(incIa, incIb, incboth)) {
						case 0:
							incrementalCharacterExecute(ac, as[ia], bc, (char)0);
							ia++;
							diff = incIa;
							break;
						case 1:
							incrementalCharacterExecute(ac, (char)0, bc, bs[ib]);
							ib++;
							diff = incIb;
							break;
						case 2:
							incrementalCharacterExecute(ac, as[ia], bc, bs[ib]);
							ia++;
							ib++;
							diff = incboth;
							break;
						default:
							return 0;
					}
				}
			}
			return calculatePercentage(count, a.length, b.length);
		} return 0;
	}

	/**
	 * Calculates the index of the minimum of three ints.
	 * @param one first int to be compared.
	 * @param two second int to be compared.
	 * @param three third int to be compared.
	 * @return the index of the minimum.
	 */
	private static int minimum(int one, int two, int three) {
		if(one <= two && one <= three) return 0;
		else if(two <= one && two <= three) return 1;
		else if(three <= one && three <= two) return 2;
		else return -1;
	}
}
