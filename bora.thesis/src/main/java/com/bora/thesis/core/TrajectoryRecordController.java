package com.bora.thesis.core;

import java.util.List;

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
		model.addAttribute("totalentries", routes.size());
		model.addAttribute("distinctzones", routes.stream().map(x -> x.getZone()).distinct().count());
		model.addAttribute("finaltrajectory", this.singleRecordService.getTrajectoryForMacRoutes(routes));
		model.addAttribute("mac", hashmac);
		model.addAttribute("list", routes);
		return "trajectory-record-view";
	}
}
