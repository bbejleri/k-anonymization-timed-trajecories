package com.bora.thesis.core;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bora.thesis.dataaccess.ClusterRecord;
import com.bora.thesis.dataaccess.ClusterWrapper;
import com.bora.thesis.dataaccess.TrajectoryRecord;
import com.bora.thesis.service.ClusterRecordService;
import com.bora.thesis.service.SingleRecordService;

/**
 * @author: bora
 */
@Controller
public class ClusterRecordContoller {

	@Autowired
	private ClusterRecordService clusterRecordService;

	@Autowired
	private SingleRecordService singleRecordService;

	@RequestMapping(value = "/cluster", method = RequestMethod.GET)
	public String doGetCluster(final Model model, @RequestParam(name = "k", required = false) int k) {
		List<ClusterRecord> clusters = this.clusterRecordService.finalizeKMember(k);
		model.addAttribute("clusters", clusters);
		return "cluster-record-view";
	}

	@RequestMapping(value = "/allclusters", method = RequestMethod.GET)
	public String doGetAllCluster(final Model model) {
		List<ClusterWrapper> wrappers = this.clusterRecordService.getAllClusterTrajectories();
		model.addAttribute("wrappers", wrappers);
		return "all-clusters";
	}

	@RequestMapping(value = "/cluster/{id}", method = RequestMethod.GET)
	public String doGetClusters(final Model model, @PathVariable("id") int id) {
		ClusterRecord cluster = this.clusterRecordService.getClusterById(id);
		List<TrajectoryRecord> list = cluster.getTrajectories();
		model.addAttribute("cluster", cluster);
		model.addAttribute("visuals", this.singleRecordService.getListVisualTrajectoryRecord(list));
		return "single-cluster";
	}

	@RequestMapping(value = "/allclustercentroids", method = RequestMethod.GET)
	public String doGetAllClusterCentroids(final Model model) {
		List<ClusterRecord> clusters = this.clusterRecordService.finalizeKMember(5);
		model.addAttribute("clusters", clusters);
		return "all-cluster-centroids";
	}
}