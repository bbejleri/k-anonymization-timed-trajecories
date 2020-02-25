package com.bora.thesis.service;

import java.util.List;

import org.apache.commons.text.similarity.EditDistance;
import org.apache.commons.text.similarity.LongestCommonSubsequence;
import org.springframework.stereotype.Service;

/**
 * @author: bora
 */
@Service
public class ParentService implements EditDistance<Integer> {

	/**
	 * Object for calculating the longest common subsequence that we can then normalize in apply.
	 */
	private final LongestCommonSubsequence longestCommonSubsequence = new LongestCommonSubsequence();

	/**
	 * @param left
	 *           first character sequence
	 * @param right
	 *           second character sequence
	 * @return lcsLengthArray
	 * @throws IllegalArgumentException
	 *            if either String input {@code null}
	 */
	public CharSequence logestCommonSubsequence(final CharSequence left, final CharSequence right) {
		if (left == null || right == null) {
			throw new IllegalArgumentException();
		}
		StringBuilder longestCommonSubstringArray = new StringBuilder(Math.max(left.length(), right.length()));
		int[][] lcsLengthArray = longestCommonSubstringLengthArray(left, right);
		int i = left.length() - 1;
		int j = right.length() - 1;
		int k = lcsLengthArray[left.length()][right.length()] - 1;
		while (k >= 0) {
			if (left.charAt(i) == right.charAt(j)) {
				longestCommonSubstringArray.append(left.charAt(i));
				i = i - 1;
				j = j - 1;
				k = k - 1;
			} else if (lcsLengthArray[i + 1][j] < lcsLengthArray[i][j + 1]) {
				i = i - 1;
			} else {
				j = j - 1;
			}
		}
		return longestCommonSubstringArray.reverse().toString();
	}

	/**
	 * Calculates an edit distance between two {@code CharSequence}'s {@code left} and {@code right}
	 * 
	 * @param left
	 *           first character sequence
	 * @param right
	 *           second character sequence
	 * @return distance
	 * @throws IllegalArgumentException
	 *            if either String input {@code null}
	 */
	@Override
	public Integer apply(final CharSequence left, final CharSequence right) {
		if (left == null || right == null) {
			throw new IllegalArgumentException();
		}
		return logestCommonSubsequence(left, right).length();
	}

	/**
	 *
	 * Computes the lcsLengthArray for the sake of doing the actual lcs calculation. This is the dynamic programming portion of the
	 * algorithm, and is the reason for the runtime complexity being O(m*n), where m=left.length() and n=right.length().
	 *
	 * @param left
	 *           first character sequence
	 * @param right
	 *           second character sequence
	 * @return lcsLengthArray
	 */
	public int[][] longestCommonSubstringLengthArray(final CharSequence left, final CharSequence right) {
		int[][] lcsLengthArray = new int[left.length() + 1][right.length() + 1];
		for (int i = 0; i < left.length(); i++) {
			for (int j = 0; j < right.length(); j++) {
				if (i == 0) {
					lcsLengthArray[i][j] = 0;
				}
				if (j == 0) {
					lcsLengthArray[i][j] = 0;
				}
				if (left.charAt(i) == right.charAt(j)) {
					lcsLengthArray[i + 1][j + 1] = lcsLengthArray[i][j] + 1;
				} else {
					lcsLengthArray[i + 1][j + 1] = Math.max(lcsLengthArray[i + 1][j], lcsLengthArray[i][j + 1]);
				}
			}
		}
		return lcsLengthArray;
	}

	/**
	 * generates all subsets of an initial trajectory
	 * 
	 * @param {@link
	 * 				String} list
	 * @param s
	 */
	public List<String> getAllSubstrings(String s, List<String> list) {
		return comb2("", s, list);
	}

	/**
	 * @param prefix
	 * @param s
	 * @param list
	 * @return
	 */
	private static List<String> comb2(String prefix, String s, List<String> list) {
		for (int i = 0; i < s.length(); i++) {
			comb2(prefix + s.charAt(i), s.substring(i + 1), list);
		}
		list.add(prefix);
		return list;
	}
}
