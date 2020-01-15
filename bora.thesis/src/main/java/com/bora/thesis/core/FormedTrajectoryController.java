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
		List<String> trajectories = new ArrayList<String>();
		List<String> trajectoriesWithInicials = new ArrayList<String>();
		List<String> trajectoriesWithNames = new ArrayList<String>();
		List<TrajectoryRecord> trajectoryRecords = new ArrayList<TrajectoryRecord>();
		distinctMacAddresses.stream().forEach(x -> {
			List<SingleRecord> routes = this.singleRecordService.getByMacAddress(x);
			// trajectoryRecords.add(this.singleRecordService.formTrajectoryByPointLocations(routes));
			trajectories.add(this.singleRecordService.getTrajectoryForMacRoutes(routes));
			trajectoriesWithInicials.add(this.singleRecordService.getTrajectoriesWithInicials(routes));
			trajectoriesWithNames.add(this.singleRecordService.getTrajectoriesWithNames(routes));
		});
		List<String> multipleLocationTrajectories = this.singleRecordService.getMultipleLocationTrajectories(trajectoriesWithInicials);
		List<VisualTrajectoryRecord> visualTrajectoryRecords = this.singleRecordService.fillTrajectoryRecords(trajectories, trajectoriesWithInicials, trajectoriesWithNames);
		final long aTrajectories = visualTrajectoryRecords.stream().filter(x -> x.getInicalTrajectory().equals("A")).count();
		final long bTrajectories = visualTrajectoryRecords.stream().filter(x -> x.getInicalTrajectory().equals("B")).count();
		final long cTrajectories = visualTrajectoryRecords.stream().filter(x -> x.getInicalTrajectory().equals("B")).count();
		final long dTrajectories = visualTrajectoryRecords.stream().filter(x -> x.getInicalTrajectory().equals("D")).count();
		final long eTrajectories = visualTrajectoryRecords.stream().filter(x -> x.getInicalTrajectory().equals("E")).count();
		final long fTrajectories = visualTrajectoryRecords.stream().filter(x -> x.getInicalTrajectory().equals("F")).count();
		model.addAttribute("distinctMacAddresses", distinctMacAddresses);
		model.addAttribute("multipleLocationTrajectories", multipleLocationTrajectories.size());
		model.addAttribute("aTrajectories", aTrajectories);
		model.addAttribute("bTrajectories", bTrajectories);
		model.addAttribute("cTrajectories", cTrajectories);
		model.addAttribute("dTrajectories", dTrajectories);
		model.addAttribute("eTrajectories", eTrajectories);
		model.addAttribute("fTrajectories", fTrajectories);
		model.addAttribute("totalTrajectories", visualTrajectoryRecords.size());
		model.addAttribute("list", visualTrajectoryRecords);
		return "all-formed-trajectories";
	}
}
