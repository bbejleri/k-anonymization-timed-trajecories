package com.bora.thesis.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bora.thesis.dataaccess.SingleRecord;
import com.bora.thesis.service.SingleRecordService;

/**
 * @author: bora
 */
@Controller
public class FormedTrajectoryController {

	@Autowired
	private SingleRecordService singleRecordService;

	@RequestMapping(value = "/alltrajectories", method = RequestMethod.GET)
	public String doGet(final Model model) {
		List<String> distinctMacAddresses = this.singleRecordService.getDistinctMacAdresses();
		List<String> trajectories = new ArrayList<String>();
		distinctMacAddresses.stream().forEach(x -> {
			List<SingleRecord> routes = this.singleRecordService.getByMacAddress(x);
			trajectories.add(this.singleRecordService.getTrajectoryForMacRoutes(routes));
		});
		model.addAttribute("distinctMacAddresses", distinctMacAddresses);
		model.addAttribute("list", trajectories.subList(0, 50));
		return "all-formed-trajectories";
	}
}
