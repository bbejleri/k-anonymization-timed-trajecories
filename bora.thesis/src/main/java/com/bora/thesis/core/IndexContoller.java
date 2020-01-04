package com.bora.thesis.core;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bora.thesis.service.SingleRecordService;

/**
 * @author: bora
 */
@Controller
public class IndexContoller {

	@Autowired
	private SingleRecordService singleRecordService;

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String doGet(final Model model) {
		int numberOfEntities = this.singleRecordService.getDistinctMacAdresses().size();
		List<String> distinctMacAddresses = this.singleRecordService.getDistinctMacAdresses();
		model.addAttribute("totalentries", numberOfEntities);
		model.addAttribute("distinctMacAddresses", distinctMacAddresses);
		return "statistics";
	}
}
