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
		List<TrajectoryRecord> trajectoryRecords = new ArrayList<TrajectoryRecord>();
		List<String> trajectoriesWithInicials = new ArrayList<String>();
		distinctMacAddresses.stream().forEach(x -> {
			List<SingleRecord> routes = this.singleRecordService.getByMacAddress(x);
			trajectories.add(this.singleRecordService.getTrajectoryForMacRoutes(routes));
			trajectoriesWithInicials.add(this.singleRecordService.getTrajectoriesWithInicials(routes));
		});
		trajectories.stream().forEach(x -> {
			TrajectoryRecord trajectroyRecord = new TrajectoryRecord();
			trajectroyRecord.setVizualizedTrajectory(x);
			trajectoryRecords.add(trajectroyRecord);
		});
		for (int i = 0; i < trajectoryRecords.size(); i++) {
			trajectoryRecords.get(i).setInicalTrajectory(trajectoriesWithInicials.get(i));
		}
		final long aTrajectories = trajectoryRecords.stream().filter(x -> x.getInicalTrajectory().equals("A")).count();
		final long bTrajectories = trajectoryRecords.stream().filter(x -> x.getInicalTrajectory().equals("B")).count();
		final long cTrajectories = trajectoryRecords.stream().filter(x -> x.getInicalTrajectory().equals("B")).count();
		final long dTrajectories = trajectoryRecords.stream().filter(x -> x.getInicalTrajectory().equals("D")).count();
		final long eTrajectories = trajectoryRecords.stream().filter(x -> x.getInicalTrajectory().equals("E")).count();
		final long fTrajectories = trajectoryRecords.stream().filter(x -> x.getInicalTrajectory().equals("F")).count();
		model.addAttribute("distinctMacAddresses", distinctMacAddresses);
		model.addAttribute("aTrajectories", aTrajectories);
		model.addAttribute("bTrajectories", bTrajectories);
		model.addAttribute("cTrajectories", cTrajectories);
		model.addAttribute("dTrajectories", dTrajectories);
		model.addAttribute("eTrajectories", eTrajectories);
		model.addAttribute("fTrajectories", fTrajectories);
		model.addAttribute("totalTrajectories", trajectoryRecords.size());
		model.addAttribute("list", trajectoryRecords);
		return "all-formed-trajectories";
	}
}
