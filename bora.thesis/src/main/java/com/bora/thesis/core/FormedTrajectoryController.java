package com.bora.thesis.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bora.thesis.dataaccess.SingleRecord;
import com.bora.thesis.dataaccess.TrajectoryRecord;
import com.bora.thesis.dataaccess.VisualTrajectoryRecord;
import com.bora.thesis.service.SingleRecordService;
import com.bora.thesis.service.VisualTrajectoryRecordService;

/**
 * @author: bora
 */
@Controller
public class FormedTrajectoryController {

	@Autowired
	private SingleRecordService singleRecordService;

	@Autowired
	private VisualTrajectoryRecordService visualTrajectoryRecordService;

	@RequestMapping(value = "/alltrajectories", method = RequestMethod.GET)
	public String doGet(final Model model) {
		List<String> distinctMacAddresses = this.singleRecordService.getDistinctMacAdresses();
		List<TrajectoryRecord> trajectoryRecords = new ArrayList<TrajectoryRecord>();
		distinctMacAddresses.stream().forEach(x -> {
			List<SingleRecord> routes = this.singleRecordService.getByMacAddress(x);
			trajectoryRecords.add(this.singleRecordService.formTrajectoryByPointLocations(routes));
		});
		List<VisualTrajectoryRecord> visuals = new ArrayList<VisualTrajectoryRecord>();
		trajectoryRecords.stream().forEach(t -> {
			visuals.add(this.singleRecordService.translateToVisualisedTrajectory(t));
		});
		// List<VisualTrajectoryRecord> visualssorted = visuals.stream().sorted((o1, o2) ->
		// o1.getNamedTrajectory().compareTo(o2.getNamedTrajectory())).collect(Collectors.toList());
		final HashMap<String, Integer> initialsMap = this.visualTrajectoryRecordService.countDistinctInitialTrajectories(visuals);
		model.addAttribute("distinctMacAddresses", distinctMacAddresses);
		model.addAttribute("map", initialsMap);
		model.addAttribute("totalTrajectories", visuals.size());
		model.addAttribute("visuals", visuals);
		return "all-formed-trajectories";
	}
}
