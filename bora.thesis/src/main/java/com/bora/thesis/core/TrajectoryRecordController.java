package com.bora.thesis.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bora.thesis.dataaccess.SingleRecord;
import com.bora.thesis.service.SingleRecordService;

/**
 * @author: bora
 */
@Controller
public class TrajectoryRecordController {

	@Autowired
	private SingleRecordService singleRecordService;

	@RequestMapping(value = "/routes/{hashmac}", method = RequestMethod.GET)
	public String doGetRouts(final Model model, @PathVariable("hashmac") String hashmac) {
		List<SingleRecord> routes = this.singleRecordService.getByMacAddress(hashmac);
		List<String> locations = routes.stream().map(x -> x.getZone()).distinct().collect(Collectors.toList());
		List<SingleRecord> selectedRoutes = new ArrayList<SingleRecord>();
		locations.stream().forEach(l -> {
			routes.stream().filter(x -> x.getZone() == l).findFirst().ifPresent(selectedRoutes::add);
		});
		StringBuilder sb = new StringBuilder();
		String finaltrajectory = null;
		for (SingleRecord record : selectedRoutes) {
			finaltrajectory = sb.append(" --> ").append(record.getZone()).append(" (").append(record.getTimestamp()).append(") ").toString();
		}
		model.addAttribute("totalentries", routes.size());
		model.addAttribute("distinctzones", routes.stream().map(x -> x.getZone()).distinct().count());
		model.addAttribute("distinctmacs", routes.stream().map(x -> x.getHashMac()).distinct().count());
		model.addAttribute("finaltrajectory", finaltrajectory);
		model.addAttribute("mac", hashmac);
		model.addAttribute("list", routes);
		return "trajectory-record-view";
	}
}
