package com.bora.thesis.core;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.bora.thesis.dataaccess.SingleRecord;
import com.bora.thesis.repositories.SingleRecordRepository;
import com.bora.thesis.service.ParentService;
import com.bora.thesis.service.SingleRecordService;

/**
 * @author: bora
 */
@Controller
public class SingleRecordController {

	@Autowired
	private SingleRecordService singleRecordService;

	@Autowired
	private ParentService parentService;

	@Autowired
	private SingleRecordRepository singleRecordRepository;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String doGet(final Model model) {
		final List<SingleRecord> records = this.singleRecordService.getList();
		final List<String> hours = Arrays.asList("17", "18", "19", "20", "21", "22");
		final List<String> zones = records.stream().map(x -> x.getZone()).distinct().collect(Collectors.toList());
		model.addAttribute("totalentered", records.stream().filter(x -> x.getEventtype() == 0).count());
		model.addAttribute("totalleft", records.stream().filter(x -> x.getEventtype() == 1).count());
		model.addAttribute("totalentries", records.size());
		model.addAttribute("distinctzones", records.stream().map(x -> x.getZone()).distinct().count());
		model.addAttribute("distinctmacs", records.stream().map(x -> x.getHashMac()).distinct().count());
		model.addAttribute("distinctvendors", records.stream().map(x -> x.getTruncmac()).distinct().count());
		model.addAttribute("hours", hours);
		model.addAttribute("zones", zones);
		model.addAttribute("list", records);
		return "main";
	}

	@RequestMapping(value = "/zone/{zone}", method = RequestMethod.GET)
	public String doGetByZone(final Model model, @PathVariable("zone") String zone) {
		final List<SingleRecord> list = this.singleRecordService.getByZone(zone);
		model.addAttribute("zone", zone);
		model.addAttribute("list", list);
		model.addAttribute("totalentries", list.size());
		model.addAttribute("distinctmacs", list.stream().map(x -> x.getHashMac()).distinct().count());
		return "zone";
	}

	@RequestMapping(value = "/hourly/{time}", method = RequestMethod.GET)
	public String doGetRangeRoutes(final Model model, @PathVariable("time") String time) throws ParseException {
		final String startDate = "2015-07-09 " + time + ":00:00.000000+02";
		final int inttime = Integer.parseInt(time) + 1;
		final String endDate = inttime < 10 ? "2015-07-09 " + "0" + Integer.toString(inttime) + ":00:00.000000+02" : "2015-07-09 " + Integer.toString(inttime) + ":00:00.000000+02";
		;
		final List<SingleRecord> records = this.singleRecordRepository.findAllRecordsBetween(startDate, endDate);
		int totalentries = records == null ? 0 : records.size();
		long distinctzones = records == null ? 0 : records.stream().map(x -> x.getZone()).distinct().count();
		long distinctmacs = records == null ? 0 : records.stream().map(x -> x.getHashMac()).distinct().count();
		model.addAttribute("totalentries", totalentries);
		model.addAttribute("distinctzones", distinctzones);
		model.addAttribute("distinctmacs", distinctmacs);
		model.addAttribute("list", records);
		return "range-routes";
	}

	@RequestMapping(value = "/entered", method = RequestMethod.GET)
	public String doGetEnteredZones(final Model model) {
		final List<SingleRecord> records = this.singleRecordService.getAllEntered();
		model.addAttribute("totalentries", records.size());
		model.addAttribute("distinctzones", records.stream().map(x -> x.getZone()).distinct().count());
		model.addAttribute("distinctmacs", records.stream().map(x -> x.getHashMac()).distinct().count());
		model.addAttribute("list", records);
		return "entered-zone";
	}

	@RequestMapping(value = "/left", method = RequestMethod.GET)
	public String doGetLeftZones(final Model model) {
		final List<SingleRecord> records = this.singleRecordService.getAllLeft();
		model.addAttribute("totalleft", records.size());
		model.addAttribute("totalentries", records.size());
		model.addAttribute("distinctzones", records.stream().map(x -> x.getZone()).distinct().count());
		model.addAttribute("distinctmacs", records.stream().map(x -> x.getHashMac()).distinct().count());
		model.addAttribute("list", records);
		return "left-zone";
	}
}
