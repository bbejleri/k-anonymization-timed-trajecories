package com.bora.thesis.core;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bora.thesis.dataaccess.ClusterRecord;
import com.bora.thesis.dataaccess.TrajectoryRecord;
import com.bora.thesis.dataaccess.VisualTrajectoryRecord;
import com.bora.thesis.service.ClusterRecordService;
import com.bora.thesis.service.SingleRecordService;

/**
 * @author: bora
 */
@Controller
public class ClusterRecordContoller {

	@Autowired
	ClusterRecordService clusterRecordService;

	@Autowired
	SingleRecordService singleRecordService;

	@RequestMapping(value = "/cluster", method = RequestMethod.GET)
	public String doGetCluster(final Model model) {
		List<ClusterRecord> clusters = this.clusterRecordService.kMember(20);
		model.addAttribute("clusters", clusters);
		return "cluster-record-view";
	}

	@RequestMapping(value = "/cluster/{id}", method = RequestMethod.GET)
	public String doGetRouts(final Model model, @PathVariable("id") int id) {
		List<TrajectoryRecord> list = this.clusterRecordService.getClusterById(id);
		List<VisualTrajectoryRecord> visuals = new ArrayList<VisualTrajectoryRecord>();
		for (TrajectoryRecord record : list) {
			visuals.add(this.singleRecordService.translateToVisualisedTrajectory(record));
		}
		model.addAttribute("visuals", visuals);
		model.addAttribute("id", id);
		return "single-cluster";
	}
}