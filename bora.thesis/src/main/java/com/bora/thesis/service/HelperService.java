package com.bora.thesis.service;

import java.util.Date;
import java.util.Optional;

import org.springframework.stereotype.Service;

/**
 * @author: bora
 */
@Service
public class HelperService {

	public String removeLastChars(final String s) {
		return Optional.ofNullable(s).filter(str -> str.length() != 0).map(str -> str.substring(0, str.length() - 3)).orElse(s);
	}

	public boolean checkDateRange(final Date min, final Date max, final Date current) {
		boolean check = Boolean.FALSE;
		if (current.after(min) && current.before(max)) {
			check = Boolean.TRUE;
		}
		return check;
	}
}
