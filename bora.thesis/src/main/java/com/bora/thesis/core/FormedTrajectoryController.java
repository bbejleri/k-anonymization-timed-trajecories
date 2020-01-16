package com.bora.thesis.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bora.thesis.dataaccess.SingleRecord;
import com.bora.thesis.dataaccess.TrajectoryRecord;
import com.bora.thesis.dataaccess.VisualTrajectoryRecord;
import com.bora.thesis.service.ParentService;
import com.bora.thesis.service.SingleRecordService;

/**
 * @author: bora
 */
@Controller
public class FormedTrajectoryController {

	@Autowired
	private SingleRecordService singleRecordService;

	@Autowired
	private ParentService parentService;

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
		final long multipleLocationTrajectories = visuals.stream().filter(x -> this.singleRecordService.isMultipleLocationTrajectory(x.getInicalTrajectory())).count();
		final long aTrajectories = visuals.stream().filter(x -> x.getInicalTrajectory().equals("A")).count();
		final long bTrajectories = visuals.stream().filter(x -> x.getInicalTrajectory().equals("B")).count();
		final long cTrajectories = visuals.stream().filter(x -> x.getInicalTrajectory().equals("B")).count();
		final long dTrajectories = visuals.stream().filter(x -> x.getInicalTrajectory().equals("D")).count();
		final long eTrajectories = visuals.stream().filter(x -> x.getInicalTrajectory().equals("E")).count();
		final long fTrajectories = visuals.stream().filter(x -> x.getInicalTrajectory().equals("F")).count();
		model.addAttribute("distinctMacAddresses", distinctMacAddresses);
		model.addAttribute("multipleLocationTrajectories", multipleLocationTrajectories);
		model.addAttribute("aTrajectories", aTrajectories);
		model.addAttribute("bTrajectories", bTrajectories);
		model.addAttribute("cTrajectories", cTrajectories);
		model.addAttribute("dTrajectories", dTrajectories);
		model.addAttribute("eTrajectories", eTrajectories);
		model.addAttribute("fTrajectories", fTrajectories);
		model.addAttribute("totalTrajectories", visuals.size());
		model.addAttribute("visuals", visuals);
		return "all-formed-trajectories";
	}
}
