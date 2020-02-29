package com.bora.thesis.service;

import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bora.thesis.repositories.SingleRecordRepository;

/**
 * @author: bora
 */
@Service
public class HelperService {

	@Autowired
	private SingleRecordService singleRecordService;

	@Autowired
	private SingleRecordRepository singleRecordRepository;

	public String removeLastChars(final String s) {
		return Optional.ofNullable(s).filter(str -> str.length() != 0).map(str -> str.substring(0, str.length() - 3)).orElse(s);
	}

	@Transactional
	public void removeTimestampLocaltime() {
		this.singleRecordService.getList().stream().forEach(x -> {
			this.singleRecordRepository.updateTimestamp(this.removeLastChars(x.getTimestamp()), x.getTrackid(), this.singleRecordService.getById(x.getTrackid()));
		});
	}

	public boolean checkDateRange(final Date min, final Date max, final Date current) {
		boolean check = Boolean.FALSE;
		if (current.after(min) && current.before(max)) {
			check = Boolean.TRUE;
		}
		return check;
	}

	public boolean isAnagramSort(final String string1, final String string2) {
		if (string1.length() != string2.length()) {
			return false;
		}
		char[] a1 = string1.toCharArray();
		char[] a2 = string2.toCharArray();
		Arrays.sort(a1);
		Arrays.sort(a2);
		return Arrays.equals(a1, a2);
	}
}
