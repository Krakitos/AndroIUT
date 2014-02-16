<?php
require_once("ADERequester.php");
class ICalendarGenerator{
	
	public static function fromADE($resource, $end) {
		$ics_data = "";
		
		$ade_requester = new ADERequester();		
		$ade_requester->connect();
		$ade_requester->setProject(28);
	
		$xml = $ade_requester->getEvents($resource, $end);

		if (!empty($xml)) {
			try {
				$ics_data = "BEGIN:VCALENDAR\nMETHOD:REQUEST\nPRODID:-//ADE/version 5.2\nVERSION:2.0\nCALSCALE:GREGORIAN\r\n";
				$schoolFirstWeek = intval($ade_requester->getSchoolYearDateStart(), 10);
				
				$events_xml = simplexml_load_string($xml);
				
				foreach($events_xml->event as $event) {
					$ics_data .= "BEGIN:VEVENT\r\n";
					foreach($event->attributes() as $name => $value) {
						switch($name) {
							case "name" :
								$ics_data .= 'SUMMARY:'.utf8_decode($value)."\r\n";
								break;
							case "week" :
								$week = intval($value, 10);
								$day = $event['day'];
								$slot = $event['slot'];
								
								$ts = intval($ade_requester->getDate($slot, $day, $week));
								$ics_data .= 'DTSTART:'.strftime('%Y%m%dT%H%M%SZ', $ts)."\r\n";
								
								$ics_data .= 'DTEND:'.strftime('%Y%m%dT%H%M%SZ', $ts + 3600 * (intval($event['duration']) / 4))."\r\n";
								break;
						}
					}
					foreach($event->children()->children() as $resource) {					
						switch($resource['category']) {
							case 'classroom' : {
								$ics_data .= 'LOCATION:'.utf8_decode($resource['name'])."\r\n";
								break;
							}
							case 'teacher' : {
								$ics_data .= 'DESCRIPTION:'.utf8_decode($resource['name'])."\r\n";
								break;
							}
						}
					}
					$ics_data .= "END:VEVENT\r\n";
				}
				$ics_data .= 'END:VCALENDAR';
			}catch (Exception $e) {
				echo $e;
				$ics_data = "";
			}	
		}
		
		echo $ics_data;
	}
}
?>